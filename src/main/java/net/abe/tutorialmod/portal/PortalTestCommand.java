package net.abe.tutorialmod.portal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.abe.tutorialmod.TutorialMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Debug command to test portal detection
 * Usage: /testportal
 */
public class PortalTestCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("testportal")
                .executes(PortalTestCommand::testPortal)
        );
    }
    
    private static int testPortal(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            BlockPos pos = player.blockPosition();
            Level level = player.level();
            
            // Check if standing in portal
            boolean inPortal = level.getBlockState(pos).is(Blocks.NETHER_PORTAL);
            
            player.sendSystemMessage(Component.literal("=== PORTAL TEST ==="));
            player.sendSystemMessage(Component.literal("Position: " + pos));
            player.sendSystemMessage(Component.literal("In portal block: " + inPortal));
            
            if (inPortal) {
                // Count nearby blocks
                int bookshelves = 0;
                int obsidian = 0;
                
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            BlockPos checkPos = pos.offset(dx, dy, dz);
                            
                            if (level.getBlockState(checkPos).is(Blocks.BOOKSHELF)) {
                                bookshelves++;
                            } else if (level.getBlockState(checkPos).is(Blocks.OBSIDIAN)) {
                                obsidian++;
                            }
                        }
                    }
                }
                
                player.sendSystemMessage(Component.literal("Bookshelves nearby: " + bookshelves));
                player.sendSystemMessage(Component.literal("Obsidian nearby: " + obsidian));
                
                boolean isBookshelf = bookshelves >= 8 && obsidian == 0;
                player.sendSystemMessage(Component.literal("Portal type: " + (isBookshelf ? "LETTERVERSE" : "NETHER")));
                
                TutorialMod.LOGGER.info("Portal test at {}: {} bookshelves, {} obsidian = {}", 
                    pos, bookshelves, obsidian, isBookshelf ? "LETTERVERSE" : "NETHER");
            } else {
                player.sendSystemMessage(Component.literal("Not standing in a portal!"));
            }
            
            player.sendSystemMessage(Component.literal("=================="));
            
            return 1;
        }
        
        return 0;
    }
}
