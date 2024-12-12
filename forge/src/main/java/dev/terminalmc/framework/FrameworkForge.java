/*
 * Framework by TerminalMC
 *
 * To the extent possible under law, the person who associated CC0 with
 * Framework has waived all copyright and related or neighboring rights
 * to Framework.
 *
 * You should have received a copy of the CC0 legalcode along with this
 * work. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package dev.terminalmc.framework;

import dev.terminalmc.framework.command.Commands;
import dev.terminalmc.framework.gui.screen.ConfigScreenProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Framework.MOD_ID)
@Mod.EventBusSubscriber(modid = Framework.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FrameworkForge {
    public FrameworkForge() {
        // Config screen
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> ConfigScreenProvider.getConfigScreen(parent))
        );

        // Main initialization
        Framework.init();
    }

    // Keybindings
    @SubscribeEvent
    static void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.register(Framework.EXAMPLE_KEY);
    }

    @Mod.EventBusSubscriber(modid = Framework.MOD_ID, value = Dist.CLIENT)
    static class ClientEventHandler {
        // Commands
        @SubscribeEvent
        static void registerClientCommands(RegisterClientCommandsEvent event) {
            new Commands<CommandSourceStack>().register(
                    event.getDispatcher(), event.getBuildContext());
        }

        // Tick events
        @SubscribeEvent
        public static void clientTickEvent(TickEvent.ClientTickEvent event) {
            if (event.phase.equals(TickEvent.Phase.END)) {
                Framework.onEndTick(Minecraft.getInstance());
            }
        }
    }
}
