package fr.openmc.core.features.mainmenu.menus.buttons;


import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.dimopener.commands.DimensionCommands;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class DimensionButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(27, 28, 29, 30, 31,
                36, 37, 38, 39, 40,
                45, 46, 47, 48, 49);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                TranslationManager.translation("feature.mainmenu.item.dimension_opener").color(NamedTextColor.DARK_GREEN),
                ButtonItems.gray("feature.mainmenu.item.dimension_opener.command")
        );
    }

    @Override
    public void open(OMCPlayer player) {
        DimensionCommands.openMenu(player);
    }
}

