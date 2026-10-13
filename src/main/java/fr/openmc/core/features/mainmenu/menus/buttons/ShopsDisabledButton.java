package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class ShopsDisabledButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(ButtonItems.name("feature.mainmenu.item.shops_disabled"));
    }

    @Override
    public void open(OMCPlayer player) {
    }
}