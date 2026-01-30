package net.abe.tutorialmod.dimension;

import net.abe.tutorialmod.TutorialMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles special effects in the Letterverse dimension,
 * including true zero gravity.
 */
@EventBusSubscriber(modid = TutorialMod.MODID)
public class LetterverseEffects {

    // Minecraft gravity acceleration per tick
    private static final double GRAVITY = 0.08;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Check if player is in the Letterverse
        if (LetterverseDimension.isLetterverse(player.level())) {
            Vec3 motion = player.getDeltaMovement();

            // Counteract gravity by adding upward force
            // Also apply drag to slow down over time for a floaty feel
            double newY = motion.y + GRAVITY; // Cancel gravity
            newY *= 0.98; // Apply slight drag so player slows down

            // Allow jumping to move up and sneaking to move down
            if (player.jumping) {
                newY = 0.15; // Move up
            } else if (player.isShiftKeyDown()) {
                newY = -0.15; // Move down
            }

            player.setDeltaMovement(motion.x, newY, motion.z);

            // Prevent fall damage
            player.fallDistance = 0;
        }
    }
}
