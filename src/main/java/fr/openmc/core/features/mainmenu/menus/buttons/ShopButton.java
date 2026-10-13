package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class ShopButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(63, 64, 65, 66, 67);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(
                ButtonItems.name("feature.mainmenu.item.shop.name"),
                ButtonItems.hint("feature.mainmenu.item.shop.command")
        );
    }

    @Override
    public void open(OMCPlayer player) {
        AdminShopManager.openMainMenu(player);
    }
}
