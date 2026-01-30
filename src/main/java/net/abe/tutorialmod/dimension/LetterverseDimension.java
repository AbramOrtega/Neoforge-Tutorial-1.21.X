package net.abe.tutorialmod.dimension;

import net.abe.tutorialmod.TutorialMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * The Letterverse - A magical dimension full of floating islands with letter blocks!
 * Accessible through a bookshelf portal activated with a book.
 */
public class LetterverseDimension {
    
    // Dimension key for the Letterverse
    public static final ResourceKey<Level> LETTERVERSE_LEVEL = ResourceKey.create(
        Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath(TutorialMod.MODID, "letterverse")
    );
    
    // Dimension type key
    public static final ResourceKey<DimensionType> LETTERVERSE_TYPE = ResourceKey.create(
        Registries.DIMENSION_TYPE,
        ResourceLocation.fromNamespaceAndPath(TutorialMod.MODID, "letterverse")
    );
    
    /**
     * Check if a level is the Letterverse
     */
    public static boolean isLetterverse(Level level) {
        return level.dimension() == LETTERVERSE_LEVEL;
    }
}
