package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.menu.playerlist.CityPlayerListMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Map;

public class MembersButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int slot) {
        Player player = menu.getOwner();

        contents.put(slot, new ItemBuilder(menu, Material.PAPER, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.main.members.title"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.city.menus.main.members.lore",
                    Component.text(city.getMembers().size()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(MemberLimitRewards.getMemberLimit(city.getLevel())).color(NamedTextColor.LIGHT_PURPLE)
            ));
            itemMeta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent ->
                new CityPlayerListMenu(player).open()
        ));
    }
}
