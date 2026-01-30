package net.abe.tutorialmod.portal;

import net.abe.tutorialmod.TutorialMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Registers the /testportal command
 */
@EventBusSubscriber(modid = TutorialMod.MODID)
public class CommandRegistry {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        PortalTestCommand.register(event.getDispatcher());
        TutorialMod.LOGGER.info("Registered /testportal command");
    }
}
