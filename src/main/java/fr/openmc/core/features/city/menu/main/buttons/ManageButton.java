package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.menu.CityModifyMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ManageButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int slot) {
        Player player = menu.getOwner();
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

        contents.put(slot, new ItemBuilder(menu, Material.PAPER, itemMeta -> {
            itemMeta.itemName(Component.text("§d" + city.getName()));
            itemMeta.lore(getDynamicLore(city, player));
            itemMeta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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

        String mayorName = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getName() : "§7Aucun";
        NamedTextColor mayorColor = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getMayorColor() : NamedTextColor.DARK_GRAY;

        List<Component> lore;
        if (hasPermissionRenameCity || hasPermissionOwner) {
            lore = List.of(
                    Component.text("§7Propriétaire de la ville : " + CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER)).getName()),
                    Component.text("§7Maire de la ville §7: ").append(Component.text(mayorName).color(mayorColor).decoration(TextDecoration.ITALIC, false)),
                    Component.text("§7Membre(s) : §d" + city.getMembers().size() + "§7/§d" + MemberLimitRewards.getMemberLimit(city.getLevel())),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR MODIFIER LA VILLE")
            );
        } else {
            lore = List.of(
                    Component.text("§7Propriétaire de la ville : " + CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER)).getName()),
                    Component.text("§dMaire de la ville §7: ").append(Component.text(mayorName).color(mayorColor).decoration(TextDecoration.ITALIC, false)),
                    Component.text("§7Membre(s) : §d" + city.getMembers().size() + "§7/§d" + MemberLimitRewards.getMemberLimit(city.getLevel()))
            );
        }
        return lore;
    }
}
