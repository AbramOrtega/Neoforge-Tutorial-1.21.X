package net.abe.tutorialmod;

import net.abe.tutorialmod.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Gives the player a Letter Wand when they first join the world
 */
@EventBusSubscriber(modid = TutorialMod.MODID)
public class PlayerJoinHandler {
    
    // NBT tag to track if player has received their starter wand
    private static final String RECEIVED_WAND_TAG = "tutorialmod_received_wand";
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Check if player has already received the wand
            if (!player.getPersistentData().getBoolean(RECEIVED_WAND_TAG)) {
                // Give the player a Letter Wand
                ItemStack wandStack = new ItemStack(ModItems.LETTER_WAND.get());
                
                // Try to add to inventory
                boolean added = player.getInventory().add(wandStack);
                
                // If inventory is full, drop it at their feet
                if (!added) {
                    player.drop(wandStack, false);
                }
                
                // Mark that they've received the wand
                player.getPersistentData().putBoolean(RECEIVED_WAND_TAG, true);
                
                TutorialMod.LOGGER.info("Gave Letter Wand to player: {}", player.getName().getString());
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        // Optional: Give wand again on respawn after dying
        // Remove this method if you only want them to get it once ever
        if (event.getEntity() instanceof ServerPlayer player) {
            // Only give on respawn if they died (not from returning from End)
            if (!event.isEndConquered()) {
                ItemStack wandStack = new ItemStack(ModItems.LETTER_WAND.get());
                
                boolean added = player.getInventory().add(wandStack);
                
                if (!added) {
                    player.drop(wandStack, false);
                }
                
                TutorialMod.LOGGER.info("Gave Letter Wand to respawned player: {}", player.getName().getString());
            }
        }
    }
}
