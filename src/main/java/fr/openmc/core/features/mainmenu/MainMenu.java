package fr.openmc.core.features.mainmenu;

import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.commands.MainMenuCommand;
import fr.openmc.core.features.mainmenu.listeners.MainMenuListener;
import fr.openmc.core.features.mainmenu.menus.Page1;
import fr.openmc.core.hooks.ProtocolLibHook;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.LoadIfEnable;
import fr.openmc.core.lifecycle.interfaces.NotLoadInUnitTest;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import org.bukkit.entity.Player;

import java.util.Set;

@Credit(developers = {"miseur"}, graphist = {"Tfloa"})
public class MainMenu extends Feature implements NotLoadInUnitTest, LoadIfEnable<ProtocolLibHook>, HasCommands {
    @Override
    public void init() {
        new MainMenuListener(OMCPlugin.getInstance());
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new MainMenuCommand()
        );
    }

    public static void openMainMenu(Player player) {
        PacketMenuLib.openMenu(new Page1(player), player);
    }
}
