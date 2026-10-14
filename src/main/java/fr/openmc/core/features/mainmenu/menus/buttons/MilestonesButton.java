package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.features.milestones.menus.MainMilestonesMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MilestonesButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(31, 32, 33, 34, 35, 40, 41, 42, 43, 44, 49, 50, 51, 52, 53);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                ButtonItems.name("feature.mainmenu.item.milestones.name"),
                ButtonItems.white("feature.mainmenu.item.milestones.lore"),
                ButtonItems.gray("feature.mainmenu.item.milestones.command"));
    }

    @Override
    public void open(OMCPlayer player) {
        new MainMilestonesMenu(player).open();
    }
}
