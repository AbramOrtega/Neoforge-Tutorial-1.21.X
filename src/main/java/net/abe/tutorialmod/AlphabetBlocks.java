package com.example.examplemod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Registry class for all alphabet letter blocks A-Z
 * Each block represents a letter for teaching phonics
 */
public class AlphabetBlocks {
    
    // Define all 26 letter blocks
    public static final DeferredBlock<Block> BLOCK_A = registerLetterBlock("a", MapColor.COLOR_RED);
    public static final DeferredBlock<Block> BLOCK_B = registerLetterBlock("b", MapColor.COLOR_BLUE);
    public static final DeferredBlock<Block> BLOCK_C = registerLetterBlock("c", MapColor.COLOR_CYAN);
    public static final DeferredBlock<Block> BLOCK_D = registerLetterBlock("d", MapColor.COLOR_BROWN);
    public static final DeferredBlock<Block> BLOCK_E = registerLetterBlock("e", MapColor.EMERALD);
    public static final DeferredBlock<Block> BLOCK_F = registerLetterBlock("f", MapColor.COLOR_PINK);
    public static final DeferredBlock<Block> BLOCK_G = registerLetterBlock("g", MapColor.COLOR_GREEN);
    public static final DeferredBlock<Block> BLOCK_H = registerLetterBlock("h", MapColor.COLOR_LIGHT_BLUE);
    public static final DeferredBlock<Block> BLOCK_I = registerLetterBlock("i", MapColor.ICE);
    public static final DeferredBlock<Block> BLOCK_J = registerLetterBlock("j", MapColor.COLOR_ORANGE);
    public static final DeferredBlock<Block> BLOCK_K = registerLetterBlock("k", MapColor.COLOR_MAGENTA);
    public static final DeferredBlock<Block> BLOCK_L = registerLetterBlock("l", MapColor.COLOR_LIGHT_GREEN);
    public static final DeferredBlock<Block> BLOCK_M = registerLetterBlock("m", MapColor.COLOR_PURPLE);
    public static final DeferredBlock<Block> BLOCK_N = registerLetterBlock("n", MapColor.NETHER);
    public static final DeferredBlock<Block> BLOCK_O = registerLetterBlock("o", MapColor.GOLD);
    public static final DeferredBlock<Block> BLOCK_P = registerLetterBlock("p", MapColor.PODZOL);
    public static final DeferredBlock<Block> BLOCK_Q = registerLetterBlock("q", MapColor.QUARTZ);
    public static final DeferredBlock<Block> BLOCK_R = registerLetterBlock("r", MapColor.COLOR_RED);
    public static final DeferredBlock<Block> BLOCK_S = registerLetterBlock("s", MapColor.SAND);
    public static final DeferredBlock<Block> BLOCK_T = registerLetterBlock("t", MapColor.TERRACOTTA_BROWN);
    public static final DeferredBlock<Block> BLOCK_U = registerLetterBlock("u", MapColor.COLOR_BLUE);
    public static final DeferredBlock<Block> BLOCK_V = registerLetterBlock("v", MapColor.COLOR_PURPLE);
    public static final DeferredBlock<Block> BLOCK_W = registerLetterBlock("w", MapColor.WOOD);
    public static final DeferredBlock<Block> BLOCK_X = registerLetterBlock("x", MapColor.COLOR_BLACK);
    public static final DeferredBlock<Block> BLOCK_Y = registerLetterBlock("y", MapColor.COLOR_YELLOW);
    public static final DeferredBlock<Block> BLOCK_Z = registerLetterBlock("z", MapColor.TERRACOTTA_MAGENTA);
    
    // Define all 26 letter block items
    public static final DeferredItem<BlockItem> ITEM_A = registerLetterBlockItem("a", BLOCK_A);
    public static final DeferredItem<BlockItem> ITEM_B = registerLetterBlockItem("b", BLOCK_B);
    public static final DeferredItem<BlockItem> ITEM_C = registerLetterBlockItem("c", BLOCK_C);
    public static final DeferredItem<BlockItem> ITEM_D = registerLetterBlockItem("d", BLOCK_D);
    public static final DeferredItem<BlockItem> ITEM_E = registerLetterBlockItem("e", BLOCK_E);
    public static final DeferredItem<BlockItem> ITEM_F = registerLetterBlockItem("f", BLOCK_F);
    public static final DeferredItem<BlockItem> ITEM_G = registerLetterBlockItem("g", BLOCK_G);
    public static final DeferredItem<BlockItem> ITEM_H = registerLetterBlockItem("h", BLOCK_H);
    public static final DeferredItem<BlockItem> ITEM_I = registerLetterBlockItem("i", BLOCK_I);
    public static final DeferredItem<BlockItem> ITEM_J = registerLetterBlockItem("j", BLOCK_J);
    public static final DeferredItem<BlockItem> ITEM_K = registerLetterBlockItem("k", BLOCK_K);
    public static final DeferredItem<BlockItem> ITEM_L = registerLetterBlockItem("l", BLOCK_L);
    public static final DeferredItem<BlockItem> ITEM_M = registerLetterBlockItem("m", BLOCK_M);
    public static final DeferredItem<BlockItem> ITEM_N = registerLetterBlockItem("n", BLOCK_N);
    public static final DeferredItem<BlockItem> ITEM_O = registerLetterBlockItem("o", BLOCK_O);
    public static final DeferredItem<BlockItem> ITEM_P = registerLetterBlockItem("p", BLOCK_P);
    public static final DeferredItem<BlockItem> ITEM_Q = registerLetterBlockItem("q", BLOCK_Q);
    public static final DeferredItem<BlockItem> ITEM_R = registerLetterBlockItem("r", BLOCK_R);
    public static final DeferredItem<BlockItem> ITEM_S = registerLetterBlockItem("s", BLOCK_S);
    public static final DeferredItem<BlockItem> ITEM_T = registerLetterBlockItem("t", BLOCK_T);
    public static final DeferredItem<BlockItem> ITEM_U = registerLetterBlockItem("u", BLOCK_U);
    public static final DeferredItem<BlockItem> ITEM_V = registerLetterBlockItem("v", BLOCK_V);
    public static final DeferredItem<BlockItem> ITEM_W = registerLetterBlockItem("w", BLOCK_W);
    public static final DeferredItem<BlockItem> ITEM_X = registerLetterBlockItem("x", BLOCK_X);
    public static final DeferredItem<BlockItem> ITEM_Y = registerLetterBlockItem("y", BLOCK_Y);
    public static final DeferredItem<BlockItem> ITEM_Z = registerLetterBlockItem("z", BLOCK_Z);
    
    /**
     * Helper method to register a letter block with specific color
     */
    private static DeferredBlock<Block> registerLetterBlock(String letter, MapColor color) {
        return ExampleMod.BLOCKS.registerSimpleBlock(
            "letter_" + letter,
            BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(1.0f)
                .sound(SoundType.WOOD)
        );
    }
    
    /**
     * Helper method to register a letter block item
     */
    private static DeferredItem<BlockItem> registerLetterBlockItem(String letter, DeferredBlock<Block> block) {
        return ExampleMod.ITEMS.registerSimpleBlockItem("letter_" + letter, block);
    }
    
    /**
     * Get the letter character from a letter block
     */
    public static char getLetterFromBlock(Block block) {
        if (block == BLOCK_A.get()) return 'a';
        if (block == BLOCK_B.get()) return 'b';
        if (block == BLOCK_C.get()) return 'c';
        if (block == BLOCK_D.get()) return 'd';
        if (block == BLOCK_E.get()) return 'e';
        if (block == BLOCK_F.get()) return 'f';
        if (block == BLOCK_G.get()) return 'g';
        if (block == BLOCK_H.get()) return 'h';
        if (block == BLOCK_I.get()) return 'i';
        if (block == BLOCK_J.get()) return 'j';
        if (block == BLOCK_K.get()) return 'k';
        if (block == BLOCK_L.get()) return 'l';
        if (block == BLOCK_M.get()) return 'm';
        if (block == BLOCK_N.get()) return 'n';
        if (block == BLOCK_O.get()) return 'o';
        if (block == BLOCK_P.get()) return 'p';
        if (block == BLOCK_Q.get()) return 'q';
        if (block == BLOCK_R.get()) return 'r';
        if (block == BLOCK_S.get()) return 's';
        if (block == BLOCK_T.get()) return 't';
        if (block == BLOCK_U.get()) return 'u';
        if (block == BLOCK_V.get()) return 'v';
        if (block == BLOCK_W.get()) return 'w';
        if (block == BLOCK_X.get()) return 'x';
        if (block == BLOCK_Y.get()) return 'y';
        if (block == BLOCK_Z.get()) return 'z';
        return '\0'; // Not a letter block
    }
    
    /**
     * Check if a block is a letter block
     */
    public static boolean isLetterBlock(Block block) {
        return getLetterFromBlock(block) != '\0';
    }
}
