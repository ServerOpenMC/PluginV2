package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CompanyDisabledButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(9, 10, 11, 12, 13, 14, 15, 16, 17);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(ButtonItems.name("feature.mainmenu.item.company_disabled"));
    }

    @Override
    public void open(OMCPlayer player) {
    }
}