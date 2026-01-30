package net.abe.tutorialmod.portal;

import net.abe.tutorialmod.TutorialMod;
import net.abe.tutorialmod.dimension.LetterverseDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * New approach: Just teleport players who are in bookshelf portals
 * Don't try to cancel vanilla behavior - just override it by teleporting first
 */
@EventBusSubscriber(modid = TutorialMod.MODID)
public class PortalTeleportHandler {
    
    // Track time players spend in portals
    private static final Map<UUID, Integer> PORTAL_TIMER = new HashMap<>();
    private static final Map<UUID, BlockPos> LAST_PORTAL_POS = new HashMap<>();
    
    /**
     * Cancel vanilla nether portal travel when player is in a bookshelf portal
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDimensionTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Check if player is in a nether portal block
        if (level.getBlockState(playerPos).is(Blocks.NETHER_PORTAL)) {
            // If it's a bookshelf portal, cancel vanilla travel
            if (isBookshelfPortal(level, playerPos)) {
                // Cancel vanilla nether/end travel - we'll handle it ourselves
                event.setCanceled(true);
                TutorialMod.LOGGER.info("Cancelled vanilla portal travel for {} - bookshelf portal detected",
                    player.getName().getString());
            }
        }

        // Also cancel if player is in Letterverse and trying to go to Nether
        // (they should go to Overworld instead via our custom handler)
        if (LetterverseDimension.isLetterverse(level) && event.getDimension() == Level.NETHER) {
            event.setCanceled(true);
            TutorialMod.LOGGER.info("Cancelled nether travel from Letterverse for {}",
                player.getName().getString());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        
        BlockPos playerPos = player.blockPosition();
        ServerLevel level = (ServerLevel) player.level();
        
        // Check if player is standing in a portal block
        if (level.getBlockState(playerPos).is(Blocks.NETHER_PORTAL)) {
            
            // Check if it's a bookshelf portal
            if (isBookshelfPortal(level, playerPos)) {
                
                // Track time in this portal
                UUID playerId = player.getUUID();
                BlockPos lastPos = LAST_PORTAL_POS.get(playerId);
                
                // If player moved to a different portal or just entered, reset timer
                if (lastPos == null || !lastPos.equals(playerPos)) {
                    PORTAL_TIMER.put(playerId, 0);
                    LAST_PORTAL_POS.put(playerId, playerPos);
                    TutorialMod.LOGGER.info("Player {} entered bookshelf portal at {}", player.getName().getString(), playerPos);
                }
                
                // Increment timer
                int ticks = PORTAL_TIMER.getOrDefault(playerId, 0) + 1;
                PORTAL_TIMER.put(playerId, ticks);

                // After 2 ticks, teleport (almost instant)
                if (ticks == 2) {
                    boolean inLetterverse = LetterverseDimension.isLetterverse(level);
                    TutorialMod.LOGGER.info("Teleporting {} via bookshelf portal to {}!",
                        player.getName().getString(),
                        inLetterverse ? "Overworld" : "Letterverse");
                    AutoPortalActivator.teleportToLetterverse(player);
                    
                    // Reset timer  
                    PORTAL_TIMER.put(playerId, -1000); // Negative to prevent re-trigger
                }
                
                // Log every second
                if (ticks % 20 == 0 && ticks > 0) {
                    TutorialMod.LOGGER.info("Player in bookshelf portal for {} ticks", ticks);
                }
                
            } else {
                // In a Nether portal (obsidian frame) - let vanilla handle it
                UUID playerId = player.getUUID();
                PORTAL_TIMER.remove(playerId);
                LAST_PORTAL_POS.remove(playerId);
            }
            
        } else {
            // Not in any portal - reset tracking
            UUID playerId = player.getUUID();
            PORTAL_TIMER.remove(playerId);
            LAST_PORTAL_POS.remove(playerId);
        }
    }
    
    /**
     * Check if a portal position is surrounded by bookshelves
     */
    private static boolean isBookshelfPortal(Level level, BlockPos portalPos) {
        int bookshelfCount = 0;
        int obsidianCount = 0;
        
        // Check surrounding blocks in a 5x5x5 area
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos checkPos = portalPos.offset(dx, dy, dz);
                    
                    if (level.getBlockState(checkPos).is(Blocks.BOOKSHELF)) {
                        bookshelfCount++;
                    } else if (level.getBlockState(checkPos).is(Blocks.OBSIDIAN)) {
                        obsidianCount++;
                    }
                }
            }
        }
        
        // Must have at least 8 bookshelves and NO obsidian
        boolean isBookshelf = bookshelfCount >= 8 && obsidianCount == 0;
        
        // Log once when first detected
        if (isBookshelf) {
            TutorialMod.LOGGER.info("Bookshelf portal detected! {} bookshelves, {} obsidian", bookshelfCount, obsidianCount);
        }
        
        return isBookshelf;
    }
}
