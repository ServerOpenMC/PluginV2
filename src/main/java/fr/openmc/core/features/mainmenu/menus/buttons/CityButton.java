package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CityButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        City city = player.city().getCity();

        Component name = (city != null)
                ? TranslationManager.translation("feature.mainmenu.item.city.name",
                        Component.text(city.getName()).color(NamedTextColor.YELLOW))
                .color(NamedTextColor.YELLOW)
                : TranslationManager.translation("feature.mainmenu.item.city.no_city")
                .color(NamedTextColor.GRAY);

        return ButtonItems.hitbox(name, ButtonItems.hint("feature.mainmenu.item.city.lore"));
    }

    @Override
    public void open(OMCPlayer player) {
        CityCommands.mainCommand(player);
    }
}
