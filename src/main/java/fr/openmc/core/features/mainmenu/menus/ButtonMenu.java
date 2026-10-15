package fr.openmc.core.features.mainmenu.menus;

import fr.openmc.api.omcplayer.OMCPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface ButtonMenu {

    Set<Integer> slots();

    ItemStack item(OMCPlayer player);

    void open(OMCPlayer player);

    default ItemStack itemAt(OMCPlayer player, int slot) {
        return item(player);
    }

    default boolean closesMenu() {
        return false;
    }

    default boolean runNextTick() {
        return true;
    }

}
