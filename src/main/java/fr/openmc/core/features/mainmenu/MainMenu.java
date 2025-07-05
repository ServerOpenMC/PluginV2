package fr.openmc.core.features.mainmenu;

import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.listeners.PacketListener;
import fr.openmc.core.features.mainmenu.menus.Page1;
import org.bukkit.entity.Player;

public class MainMenu {

    private final OMCPlugin plugin;

    public MainMenu(OMCPlugin plugin) {
        this.plugin = plugin;
        new PacketListener(plugin);
    }

    public static void openMainMenu(Player player) {
        PacketMenuLib.openMenu(new Page1(player), player);
    }
}
