package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles detection of spelled words and rewards players with items
 */
public class WordDetectionSystem {
    
    // Map of words to their reward items
    private static final Map<String, ItemStack> WORD_REWARDS = new HashMap<>();
    
    static {
        // Add word-to-item mappings here
        // Simple words for a 4-year-old
        WORD_REWARDS.put("wood", new ItemStack(Blocks.OAK_WOOD));
        WORD_REWARDS.put("iron", new ItemStack(Items.IRON_INGOT));
        WORD_REWARDS.put("gold", new ItemStack(Items.GOLD_INGOT));
        WORD_REWARDS.put("coal", new ItemStack(Items.COAL));
        WORD_REWARDS.put("stone", new ItemStack(Blocks.STONE));
        WORD_REWARDS.put("grass", new ItemStack(Blocks.GRASS_BLOCK));
        WORD_REWARDS.put("sand", new ItemStack(Blocks.SAND));
        WORD_REWARDS.put("dirt", new ItemStack(Blocks.DIRT));
        WORD_REWARDS.put("apple", new ItemStack(Items.APPLE));
        WORD_REWARDS.put("bread", new ItemStack(Items.BREAD));
        WORD_REWARDS.put("cake", new ItemStack(Items.CAKE));
        WORD_REWARDS.put("fish", new ItemStack(Items.COOKED_COD));
        WORD_REWARDS.put("egg", new ItemStack(Items.EGG));
        WORD_REWARDS.put("bed", new ItemStack(Items.RED_BED));
        WORD_REWARDS.put("book", new ItemStack(Items.BOOK));
        WORD_REWARDS.put("bow", new ItemStack(Items.BOW));
        WORD_REWARDS.put("axe", new ItemStack(Items.IRON_AXE));
        WORD_REWARDS.put("sword", new ItemStack(Items.IRON_SWORD));
        WORD_REWARDS.put("star", new ItemStack(Items.NETHER_STAR));
        WORD_REWARDS.put("rose", new ItemStack(Blocks.POPPY));
        WORD_REWARDS.put("cookie", new ItemStack(Items.COOKIE));
        WORD_REWARDS.put("melon", new ItemStack(Items.MELON));
        WORD_REWARDS.put("carrot", new ItemStack(Items.CARROT));
        WORD_REWARDS.put("potato", new ItemStack(Items.POTATO));
        WORD_REWARDS.put("pumpkin", new ItemStack(Blocks.PUMPKIN));
        
        // Animal words
        WORD_REWARDS.put("cow", new ItemStack(Items.BEEF));
        WORD_REWARDS.put("pig", new ItemStack(Items.PORKCHOP));
        WORD_REWARDS.put("sheep", new ItemStack(Items.MUTTON));
        WORD_REWARDS.put("chicken", new ItemStack(Items.CHICKEN));
        
        // Simple 3-letter words
        WORD_REWARDS.put("cat", new ItemStack(Items.STRING)); // cats drop string
        WORD_REWARDS.put("dog", new ItemStack(Items.BONE)); // dogs like bones
        WORD_REWARDS.put("bat", new ItemStack(Items.PHANTOM_MEMBRANE));
        WORD_REWARDS.put("bee", new ItemStack(Items.HONEYCOMB));
        WORD_REWARDS.put("fox", new ItemStack(Items.SWEET_BERRIES));
        WORD_REWARDS.put("ice", new ItemStack(Blocks.ICE));
        WORD_REWARDS.put("sun", new ItemStack(Blocks.GLOWSTONE));
        WORD_REWARDS.put("log", new ItemStack(Blocks.OAK_LOG));
        WORD_REWARDS.put("gem", new ItemStack(Items.DIAMOND));
    }
    
    /**
     * Check for words in all directions when a block is placed
     */
    public static void checkForWords(Level level, BlockPos pos, Block placedBlock) {
        if (level.isClientSide || !AlphabetBlocks.isLetterBlock(placedBlock)) {
            return;
        }
        
        // Check all 6 directions (horizontal and vertical)
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            checkWordInDirection(level, pos, direction);
        }
    }
    
    /**
     * Check for a word in a specific direction
     */
    private static void checkWordInDirection(Level level, BlockPos startPos, Direction direction) {
        List<BlockPos> letterPositions = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        
        // Start from the furthest back position in this direction
        BlockPos currentPos = startPos;
        
        // Go backwards to find the start of the word
        while (true) {
            BlockPos backPos = currentPos.relative(direction.getOpposite());
            Block backBlock = level.getBlockState(backPos).getBlock();
            if (AlphabetBlocks.isLetterBlock(backBlock)) {
                currentPos = backPos;
            } else {
                break;
            }
        }
        
        // Now read forwards to build the word
        while (true) {
            Block block = level.getBlockState(currentPos).getBlock();
            if (AlphabetBlocks.isLetterBlock(block)) {
                letterPositions.add(currentPos);
                word.append(AlphabetBlocks.getLetterFromBlock(block));
                currentPos = currentPos.relative(direction);
            } else {
                break;
            }
        }
        
        // Check if we found a valid word (at least 2 letters)
        if (word.length() >= 2) {
            String wordStr = word.toString().toLowerCase();
            if (WORD_REWARDS.containsKey(wordStr)) {
                rewardWord(level, letterPositions, wordStr);
            }
        }
    }
    
    /**
     * Reward the player for spelling a word correctly
     */
    private static void rewardWord(Level level, List<BlockPos> positions, String word) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Calculate center position for spawning the reward
        BlockPos centerPos = positions.get(positions.size() / 2);
        
        // Remove all letter blocks
        for (BlockPos pos : positions) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        
        // Spawn the reward item
        ItemStack reward = WORD_REWARDS.get(word).copy();
        ItemEntity itemEntity = new ItemEntity(
            level,
            centerPos.getX() + 0.5,
            centerPos.getY() + 0.5,
            centerPos.getZ() + 0.5,
            reward
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
        
        // Play success sound
        level.playSound(
            null,
            centerPos,
            SoundEvents.PLAYER_LEVELUP,
            SoundSource.BLOCKS,
            1.0f,
            1.0f
        );
        
        ExampleMod.LOGGER.info("Player spelled word: {} and received reward!", word);
    }
    
    /**
     * Add a custom word-to-item mapping
     */
    public static void addWordReward(String word, ItemStack reward) {
        WORD_REWARDS.put(word.toLowerCase(), reward);
    }
}
