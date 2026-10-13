package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.features.settings.command.SettingsCommand;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class SettingsButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(82, 83, 84);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(
                ButtonItems.name("feature.mainmenu.item.settings.name"),
                ButtonItems.hint("feature.mainmenu.item.settings.command")
        );
    }

    @Override
    public void open(OMCPlayer player) {
        SettingsCommand.settings(player);
    }
}
