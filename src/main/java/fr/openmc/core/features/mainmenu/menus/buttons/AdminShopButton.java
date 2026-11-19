package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class AdminShopButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(32, 33, 34, 35,
                41, 42, 43, 44,
                50, 51, 52, 53);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                ButtonItems.name("feature.mainmenu.item.adminshop.name"),
                ButtonItems.gray("feature.mainmenu.item.adminshop.command")
        );
    }

    @Override
    public void open(OMCPlayer player) {
        AdminShopManager.openMainMenu(player);
    }
}
