package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.homes.command.TpHomeCommand;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class HomeButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(72, 73, 74, 75, 76);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(
                ButtonItems.name("feature.mainmenu.item.home.name"),
                ButtonItems.hint("feature.mainmenu.item.home.command")
        );
    }

    @Override
    public void open(OMCPlayer player) {
        TpHomeCommand.home(player, null);
    }
}
