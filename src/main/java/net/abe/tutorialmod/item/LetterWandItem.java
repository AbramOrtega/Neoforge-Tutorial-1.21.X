package net.abe.tutorialmod.item;

import net.abe.tutorialmod.AlphabetBlocks;
import net.abe.tutorialmod.TutorialMod;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A magical wand that shoots out random letter blocks!
 * Perfect for teaching phonics to kids.
 */
public class LetterWandItem extends Item {
    
    private static final Random RANDOM = new Random();
    
    // List of all letter block items
    private static final List<DeferredItem<?>> LETTER_ITEMS = new ArrayList<>();
    
    static {
        // Add all 26 letter block items to the list
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_A);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_B);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_C);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_D);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_E);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_F);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_G);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_H);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_I);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_J);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_K);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_L);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_M);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_N);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_O);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_P);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_Q);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_R);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_S);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_T);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_U);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_V);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_W);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_X);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_Y);
        LETTER_ITEMS.add(AlphabetBlocks.ITEM_Z);
    }
    
    public LetterWandItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // Spawn 10 random letter blocks
            spawnRandomLetterBlocks(level, player);
            
            // Play a magical sound
            level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS,
                1.0f,
                1.0f + (RANDOM.nextFloat() * 0.4f) // Slightly random pitch
            );
            
            // Add a cooldown so it can't be spammed instantly
            player.getCooldowns().addCooldown(this, 20); // 1 second cooldown
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
    
    /**
     * Spawn 10 random letter blocks that fly out in different directions
     */
    private void spawnRandomLetterBlocks(Level level, Player player) {
        BlockPos playerPos = player.blockPosition();
        Vec3 playerLook = player.getLookAngle();
        
        for (int i = 0; i < 10; i++) {
            // Pick a random letter block
            DeferredItem<?> randomLetterItem = LETTER_ITEMS.get(RANDOM.nextInt(LETTER_ITEMS.size()));
            ItemStack letterStack = new ItemStack(randomLetterItem.get());
            
            // Create the item entity
            ItemEntity itemEntity = new ItemEntity(
                level,
                player.getX(),
                player.getY() + 1.5, // Spawn at chest height
                player.getZ(),
                letterStack
            );
            
            // Calculate a random direction to shoot the item
            // Base direction is where the player is looking, with some random spread
            double spreadX = (RANDOM.nextDouble() - 0.5) * 0.5; // Random spread
            double spreadY = RANDOM.nextDouble() * 0.3 + 0.2; // Upward arc
            double spreadZ = (RANDOM.nextDouble() - 0.5) * 0.5; // Random spread
            
            // Set the velocity to make items fly out
            itemEntity.setDeltaMovement(
                playerLook.x * 0.5 + spreadX,
                spreadY,
                playerLook.z * 0.5 + spreadZ
            );
            
            // Make it pickable immediately
            itemEntity.setDefaultPickUpDelay();
            
            // Add the item to the world
            level.addFreshEntity(itemEntity);
        }
        
        TutorialMod.LOGGER.info("Letter Wand used! Spawned 10 random letter blocks");
    }
    
    /**
     * Make the wand glow like an enchanted item
     */
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Makes it glow!
    }
}
