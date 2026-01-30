package net.abe.tutorialmod.worldgen;

import net.abe.tutorialmod.AlphabetBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.abe.tutorialmod.TutorialMod;
import net.abe.tutorialmod.dimension.LetterverseDimension;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.List;
import java.util.Random;

/**
 * Generates floating islands in the Letterverse using chunk events
 * This is simpler than custom ChunkGenerator and works with vanilla generation
 */
@EventBusSubscriber(modid = TutorialMod.MODID)
public class LetterverseIslandGenerator {
    
    private static final Random random = new Random();
    
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            // Only generate in Letterverse dimension
            if (LetterverseDimension.isLetterverse(serverLevel)) {
                ChunkAccess chunk = event.getChunk();
                generateFloatingIslands(chunk, serverLevel);
            }
        }
    }
    
    /**
     * Generate floating islands with letter blocks
     */
    private static void generateFloatingIslands(ChunkAccess chunk, ServerLevel level) {
        ChunkPos chunkPos = chunk.getPos();
        random.setSeed(chunkPos.x * 341873128712L + chunkPos.z * 132897987541L);
        
        // 20% chance for an island in this chunk
        if (random.nextDouble() < 0.2) {
            generateIsland(chunk, level, random.nextInt(16), 80 + random.nextInt(40), random.nextInt(16));
        }
        
        // 10% chance for a second smaller island
        if (random.nextDouble() < 0.1) {
            generateIsland(chunk, level, random.nextInt(16), 60 + random.nextInt(30), random.nextInt(16));
        }
    }
    
    /**
     * Generate a single floating island made entirely of one letter
     */
    private static void generateIsland(ChunkAccess chunk, ServerLevel level, int centerX, int centerY, int centerZ) {
        int radius = 3 + random.nextInt(5); // Island radius 3-7 blocks

        // Pick ONE letter for this entire island
        BlockState letterBlock = getRandomLetterBlock();

        // Create the island entirely out of this letter
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= radius) {
                    // Create a rounded dome/sphere shape
                    int height = (int) (3 - (distance / radius) * 2); // Dome shape

                    // Also add some depth below for a floating island look
                    int depth = (int) (2 - (distance / radius) * 1.5);

                    for (int y = -depth; y <= height; y++) {
                        BlockPos pos = new BlockPos(centerX + x, centerY + y, centerZ + z);
                        chunk.setBlockState(pos, letterBlock, false);
                    }
                }
            }
        }
    }
    
    /**
     * Get a random letter block
     */
    private static BlockState getRandomLetterBlock() {
        List<DeferredBlock<?>> letterBlocks = List.of(
            AlphabetBlocks.BLOCK_A, AlphabetBlocks.BLOCK_B, AlphabetBlocks.BLOCK_C,
            AlphabetBlocks.BLOCK_D, AlphabetBlocks.BLOCK_E, AlphabetBlocks.BLOCK_F,
            AlphabetBlocks.BLOCK_G, AlphabetBlocks.BLOCK_H, AlphabetBlocks.BLOCK_I,
            AlphabetBlocks.BLOCK_J, AlphabetBlocks.BLOCK_K, AlphabetBlocks.BLOCK_L,
            AlphabetBlocks.BLOCK_M, AlphabetBlocks.BLOCK_N, AlphabetBlocks.BLOCK_O,
            AlphabetBlocks.BLOCK_P, AlphabetBlocks.BLOCK_Q, AlphabetBlocks.BLOCK_R,
            AlphabetBlocks.BLOCK_S, AlphabetBlocks.BLOCK_T, AlphabetBlocks.BLOCK_U,
            AlphabetBlocks.BLOCK_V, AlphabetBlocks.BLOCK_W, AlphabetBlocks.BLOCK_X,
            AlphabetBlocks.BLOCK_Y, AlphabetBlocks.BLOCK_Z
        );
        
        return letterBlocks.get(random.nextInt(letterBlocks.size())).get().defaultBlockState();
    }
}
