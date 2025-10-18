package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.menu.playerlist.CityPlayerListMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class MembersButton {
    public static void init(Menu menu, Map<Integer, ItemBuilder> contents, City city, int slot) {
        Player player = menu.getOwner();

        contents.put(slot, new ItemBuilder(menu, Material.PAPER, itemMeta -> {
            itemMeta.displayName(Component.text("§dListe des Membres"));
            itemMeta.lore(List.of(
                    Component.text("§7Il y a actuellement §d" + city.getMembers().size() + "§7 membre(s) dans votre ville"),
                    Component.text("§7Vous avez une limite de membre de §d" + MemberLimitRewards.getMemberLimit(city.getLevel()) + "§7 membre(s)"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR VOIR LA LISTE DES JOUEURS")
            ));
            itemMeta.setItemModel(NamespacedKey.minecraft("air"));
        }).setOnClick(inventoryClickEvent ->
                new CityPlayerListMenu(player).open()
        ));
    }
}
