package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.commands.EventCommand;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class EventsButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(58, 59, 60, 61, 62,
                        67, 68, 69, 70, 71,
                        76, 77, 78, 79, 80);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                TranslationManager.translation("feature.mainmenu.item.events").color(NamedTextColor.RED),
                ButtonItems.gray("feature.mainmenu.item.events.command"));
    }

    @Override
    public void open(OMCPlayer player) {
        EventCommand.mainCommand(player);
    }
}