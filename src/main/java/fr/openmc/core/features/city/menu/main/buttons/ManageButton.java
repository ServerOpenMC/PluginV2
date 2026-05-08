package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.menu.CityModifyMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ManageButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int slot) {
        Player player = menu.getOwner();
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

        contents.put(slot, new ItemBuilder(menu, Material.PAPER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation(
                    "feature.city.menus.main.manage.title",
                    Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
            ));
            itemMeta.lore(getDynamicLore(city, player));
            itemMeta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (hasPermissionOwner) {
                new CityModifyMenu(player).open();
            }
        }));
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CityPermission.RENAME);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

        Component mayorName = (city.getMayor() != null && city.getMayor().getName() != null)
                ? city.getMayor().getName()
                : TranslationManager.translation("messages.menus.none");
        NamedTextColor mayorColor = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getMayorColor() : NamedTextColor.DARK_GRAY;
        UUID ownerUUID = city.getPlayerWithPermission(CityPermission.OWNER);

        List<Component> lore;
        if (hasPermissionRenameCity || hasPermissionOwner) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.manage.lore.edit",
                    PlayerNameCache.name(ownerUUID).color(NamedTextColor.GRAY),
                    mayorName.color(mayorColor).decoration(TextDecoration.ITALIC, false),
                    Component.text(city.getMembers().size()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(MemberLimitRewards.getMemberLimit(city.getLevel())).color(NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.main.manage.lore.view",
                    PlayerNameCache.name(ownerUUID).color(NamedTextColor.GRAY),
                    mayorName.color(mayorColor).decoration(TextDecoration.ITALIC, false),
                    Component.text(city.getMembers().size()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(MemberLimitRewards.getMemberLimit(city.getLevel())).color(NamedTextColor.LIGHT_PURPLE)
            );
        }
        return lore;
    }
}
