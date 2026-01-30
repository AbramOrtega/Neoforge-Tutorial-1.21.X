package net.abe.tutorialmod.portal;

import net.abe.tutorialmod.TutorialMod;
import net.abe.tutorialmod.dimension.LetterverseDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Automatically lights bookshelf portals when the frame is completed!
 * Prevents portal from breaking by continuously maintaining it.
 */
@EventBusSubscriber(modid = TutorialMod.MODID)
public class AutoPortalActivator {
    
    private static final int PORTAL_WIDTH = 4;
    private static final int PORTAL_HEIGHT = 5;
    
    // Track active portal positions to maintain them
    private static final Set<BlockPos> ACTIVE_PORTALS = new HashSet<>();
    
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        
        // Only check if a bookshelf was placed
        if (event.getPlacedBlock().is(Blocks.BOOKSHELF)) {
            // Check if this completes a portal frame
            checkAndLightPortal(level, pos);
        }
    }
    
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        
        // If a bookshelf is broken, remove any portals that depended on it
        if (level.getBlockState(pos).is(Blocks.BOOKSHELF)) {
            extinguishNearbyPortals(level, pos);
        }
    }
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            // Every 20 ticks (1 second), check and maintain active portals
            if (serverLevel.getGameTime() % 20 == 0) {
                maintainPortals(serverLevel);
            }
        }
    }
    
    /**
     * Maintain all active portals by relighting them if needed
     */
    private static void maintainPortals(ServerLevel level) {
        Set<BlockPos> toRemove = new HashSet<>();
        
        for (BlockPos portalCorner : ACTIVE_PORTALS) {
            // Check both orientations
            boolean stillValid = false;
            Direction validFacing = null;
            
            if (isValidPortalFrame(level, portalCorner, Direction.NORTH)) {
                stillValid = true;
                validFacing = Direction.NORTH;
            } else if (isValidPortalFrame(level, portalCorner, Direction.EAST)) {
                stillValid = true;
                validFacing = Direction.EAST;
            }
            
            if (stillValid) {
                // Relight the portal if any blocks are missing
                relightIfNeeded(level, portalCorner, validFacing);
            } else {
                // Frame is broken, mark for removal
                toRemove.add(portalCorner);
            }
        }
        
        // Remove invalid portals
        ACTIVE_PORTALS.removeAll(toRemove);
    }
    
    /**
     * Relight portal blocks if they've disappeared
     */
    private static void relightIfNeeded(ServerLevel level, BlockPos corner, Direction facing) {
        Direction right = facing.getClockWise();
        Direction.Axis axis = (facing == Direction.NORTH || facing == Direction.SOUTH) 
            ? Direction.Axis.X 
            : Direction.Axis.Z;
        
        boolean anyMissing = false;
        
        // Check if any portal blocks are missing
        for (int x = 1; x < PORTAL_WIDTH - 1; x++) {
            for (int y = 1; y < PORTAL_HEIGHT - 1; y++) {
                BlockPos pos = corner.relative(right, x).above(y);
                if (!level.getBlockState(pos).is(Blocks.NETHER_PORTAL)) {
                    anyMissing = true;
                    break;
                }
            }
            if (anyMissing) break;
        }
        
        // Relight if needed
        if (anyMissing) {
            for (int x = 1; x < PORTAL_WIDTH - 1; x++) {
                for (int y = 1; y < PORTAL_HEIGHT - 1; y++) {
                    BlockPos pos = corner.relative(right, x).above(y);
                    BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                        .setValue(net.minecraft.world.level.block.NetherPortalBlock.AXIS, axis);
                    level.setBlock(pos, portalState, 2); // Use flag 2 to prevent block updates
                }
            }
        }
    }
    
    /**
     * Extinguish any portals that no longer have a valid frame
     */
    private static void extinguishNearbyPortals(Level level, BlockPos brokenBookshelf) {
        // Search for portal blocks nearby and remove them
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos checkPos = brokenBookshelf.offset(dx, dy, dz);
                    if (level.getBlockState(checkPos).is(Blocks.NETHER_PORTAL)) {
                        level.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        // Remove from active portals list
        ACTIVE_PORTALS.removeIf(pos -> pos.closerThan(brokenBookshelf, 10));
    }
    
    /**
     * Check all nearby positions to see if a portal frame was completed
     */
    private static void checkAndLightPortal(Level level, BlockPos placedPos) {
        // Search area around the placed bookshelf
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos testPos = placedPos.offset(dx, dy, dz);
                    
                    // Try both orientations
                    if (tryLightPortal(level, testPos, Direction.NORTH) ||
                        tryLightPortal(level, testPos, Direction.EAST)) {
                        return; // Portal lit, we're done!
                    }
                }
            }
        }
    }
    
    /**
     * Try to light a portal with given corner and orientation
     */
    private static boolean tryLightPortal(Level level, BlockPos corner, Direction facing) {
        if (isValidPortalFrame(level, corner, facing) && !isAlreadyLit(level, corner, facing)) {
            lightPortal(level, corner, facing);
            
            // Add to active portals list for maintenance
            ACTIVE_PORTALS.add(corner.immutable());
            
            // Play activation sound
            level.playSound(
                null,
                corner,
                SoundEvents.PORTAL_TRIGGER,
                SoundSource.BLOCKS,
                1.0f,
                1.0f
            );
            
            TutorialMod.LOGGER.info("Auto-lit portal at {} facing {} - Added to maintenance list", corner, facing);
            return true;
        }
        return false;
    }
    
    /**
     * Check if portal is already lit
     */
    private static boolean isAlreadyLit(Level level, BlockPos corner, Direction facing) {
        Direction right = facing.getClockWise();
        
        // Check if any portal blocks exist in the frame
        for (int x = 1; x < PORTAL_WIDTH - 1; x++) {
            for (int y = 1; y < PORTAL_HEIGHT - 1; y++) {
                BlockPos pos = corner.relative(right, x).above(y);
                if (level.getBlockState(pos).is(Blocks.NETHER_PORTAL)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check if there's a valid 4x5 bookshelf frame at this position
     */
    private static boolean isValidPortalFrame(Level level, BlockPos corner, Direction facing) {
        Direction right = facing.getClockWise();
        
        // Check bottom row (4 bookshelves)
        for (int i = 0; i < PORTAL_WIDTH; i++) {
            BlockPos pos = corner.relative(right, i);
            if (!level.getBlockState(pos).is(Blocks.BOOKSHELF)) {
                return false;
            }
        }
        
        // Check top row (4 bookshelves)
        for (int i = 0; i < PORTAL_WIDTH; i++) {
            BlockPos pos = corner.relative(right, i).above(PORTAL_HEIGHT - 1);
            if (!level.getBlockState(pos).is(Blocks.BOOKSHELF)) {
                return false;
            }
        }
        
        // Check left side (3 middle bookshelves)
        for (int i = 1; i < PORTAL_HEIGHT - 1; i++) {
            BlockPos pos = corner.above(i);
            if (!level.getBlockState(pos).is(Blocks.BOOKSHELF)) {
                return false;
            }
        }
        
        // Check right side (3 middle bookshelves)
        for (int i = 1; i < PORTAL_HEIGHT - 1; i++) {
            BlockPos pos = corner.relative(right, PORTAL_WIDTH - 1).above(i);
            if (!level.getBlockState(pos).is(Blocks.BOOKSHELF)) {
                return false;
            }
        }
        
        // Check that the inside is empty or has portal blocks
        for (int x = 1; x < PORTAL_WIDTH - 1; x++) {
            for (int y = 1; y < PORTAL_HEIGHT - 1; y++) {
                BlockPos pos = corner.relative(right, x).above(y);
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && !state.is(Blocks.NETHER_PORTAL)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Fill the portal frame with portal blocks
     */
    private static void lightPortal(Level level, BlockPos corner, Direction facing) {
        Direction right = facing.getClockWise();
        
        // Determine the axis for the portal
        Direction.Axis axis = (facing == Direction.NORTH || facing == Direction.SOUTH) 
            ? Direction.Axis.X 
            : Direction.Axis.Z;
        
        // Fill the inside with portal blocks with correct orientation
        for (int x = 1; x < PORTAL_WIDTH - 1; x++) {
            for (int y = 1; y < PORTAL_HEIGHT - 1; y++) {
                BlockPos pos = corner.relative(right, x).above(y);
                
                // Set portal block with correct axis - use flag 2 to prevent updates
                BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.NetherPortalBlock.AXIS, axis);
                
                level.setBlock(pos, portalState, 2);
            }
        }
        
        TutorialMod.LOGGER.info("Portal lit at {} with axis {}", corner, axis);
    }
    
    /**
     * Teleport a player to the Letterverse or back
     */
    public static void teleportToLetterverse(ServerPlayer player) {
        ServerLevel currentLevel = (ServerLevel) player.level();
        ServerLevel targetLevel;
        Vec3 targetPos;
        
        if (LetterverseDimension.isLetterverse(currentLevel)) {
            // Return to Overworld
            targetLevel = player.server.getLevel(Level.OVERWORLD);
            targetPos = new Vec3(
                player.getX(),
                100, // Spawn high up
                player.getZ()
            );
            TutorialMod.LOGGER.info("Teleporting {} from Letterverse to Overworld", player.getName().getString());
        } else {
            // Go to Letterverse
            targetLevel = player.server.getLevel(LetterverseDimension.LETTERVERSE_LEVEL);
            if (targetLevel == null) {
                TutorialMod.LOGGER.error("Letterverse dimension not found! Check dimension JSON files.");
                return;
            }
            
            // Find a safe spawn position
            BlockPos spawnPos = new BlockPos((int)player.getX(), 100, (int)player.getZ());
            
            // Create a spawn platform
            createSpawnPlatform(targetLevel, spawnPos);
            
            // Create a return portal
            createReturnPortal(targetLevel, spawnPos);
            
            targetPos = new Vec3(
                spawnPos.getX() + 0.5,
                spawnPos.getY() + 1, // Spawn on top of platform
                spawnPos.getZ() + 0.5
            );
            
            TutorialMod.LOGGER.info("Teleporting {} from Overworld to Letterverse at {}", player.getName().getString(), spawnPos);
        }
        
        if (targetLevel != null) {
            // Create dimension transition
            DimensionTransition transition = new DimensionTransition(
                targetLevel,
                targetPos,
                Vec3.ZERO, // velocity
                player.getYRot(),
                player.getXRot(),
                DimensionTransition.DO_NOTHING
            );
            
            player.changeDimension(transition);
            
            // Play teleport sound
            player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }
    
    /**
     * Create a spawn platform in the Letterverse
     */
    private static void createSpawnPlatform(ServerLevel level, BlockPos center) {
        // Create a 7x7 platform
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos pos = center.offset(x, -1, z);
                
                // Make it look nice - grass in center, stone around edges
                double distance = Math.sqrt(x * x + z * z);
                if (distance < 2) {
                    level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
                }
                
                // Clear space above
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(3), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        
        TutorialMod.LOGGER.info("Created spawn platform at {}", center);
    }
    
    /**
     * Create a return portal in the Letterverse
     */
    private static void createReturnPortal(ServerLevel level, BlockPos center) {
        // Build portal on the platform, offset slightly so player doesn't spawn inside
        BlockPos portalCorner = center.offset(-1, 0, 2);
        
        Direction facing = Direction.NORTH;
        Direction right = facing.getClockWise();
        
        // Build the bookshelf frame
        // Bottom row
        for (int i = 0; i < PORTAL_WIDTH; i++) {
            BlockPos pos = portalCorner.relative(right, i);
            level.setBlock(pos, Blocks.BOOKSHELF.defaultBlockState(), 3);
        }
        
        // Top row
        for (int i = 0; i < PORTAL_WIDTH; i++) {
            BlockPos pos = portalCorner.relative(right, i).above(PORTAL_HEIGHT - 1);
            level.setBlock(pos, Blocks.BOOKSHELF.defaultBlockState(), 3);
        }
        
        // Left side
        for (int i = 1; i < PORTAL_HEIGHT - 1; i++) {
            BlockPos pos = portalCorner.above(i);
            level.setBlock(pos, Blocks.BOOKSHELF.defaultBlockState(), 3);
        }
        
        // Right side
        for (int i = 1; i < PORTAL_HEIGHT - 1; i++) {
            BlockPos pos = portalCorner.relative(right, PORTAL_WIDTH - 1).above(i);
            level.setBlock(pos, Blocks.BOOKSHELF.defaultBlockState(), 3);
        }
        
        // Light the portal
        lightPortal(level, portalCorner, facing);
        
        // Add to active portals
        ACTIVE_PORTALS.add(portalCorner.immutable());
        
        TutorialMod.LOGGER.info("Created return portal at {}", portalCorner);
    }
}
