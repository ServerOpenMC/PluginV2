package fr.openmc.core.features.mainmenu;

import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.listeners.PacketListener;
import fr.openmc.core.features.mainmenu.menus.Page1;
import fr.openmc.core.utils.init.Feature;
import fr.openmc.core.utils.init.LoadAfterItemsAdder;
import fr.openmc.core.utils.init.NotInUnitTest;
import org.bukkit.entity.Player;

public class MainMenu extends Feature implements NotInUnitTest, LoadAfterItemsAdder {
    @Override
    public void init() {
        new PacketListener(OMCPlugin.getInstance());
    }

    public static void openMainMenu(Player player) {
        PacketMenuLib.openMenu(new Page1(player), player);
    }

    @Override
    public void save() {
        //nothing to save
    }
}
