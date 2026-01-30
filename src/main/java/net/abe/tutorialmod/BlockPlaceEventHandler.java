package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Event handler for detecting when letter blocks are placed
 * and checking if they form valid words
 */
@EventBusSubscriber(modid = ExampleMod.MODID)
public class BlockPlaceEventHandler {
    
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        Block block = event.getPlacedBlock().getBlock();
        
        // Check if a letter block was placed
        if (AlphabetBlocks.isLetterBlock(block)) {
            // Check for words in all directions
            WordDetectionSystem.checkForWords(level, pos, block);
        }
    }
}
