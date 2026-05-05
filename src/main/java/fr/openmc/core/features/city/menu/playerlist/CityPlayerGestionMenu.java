package fr.openmc.core.features.city.menu.playerlist;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityKickAction;
import fr.openmc.core.features.city.conditions.CityKickCondition;
import fr.openmc.core.features.city.menu.CityPermsMenu;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityPlayerGestionMenu extends Menu {

    private final OfflinePlayer playerTarget;

    public CityPlayerGestionMenu(Player owner, OfflinePlayer player) {
        super(owner);
        this.playerTarget = player;
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.members.manage.name");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionKick = city.hasPermission(player.getUniqueId(), CityPermission.KICK);
        boolean hasPermissionPerms = city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_PERMS);

        List<Component> loreKick;

        if (hasPermissionKick) {
            if (player.getUniqueId().equals(playerTarget.getUniqueId())) {
                loreKick = TranslationManager.translationLore("feature.city.menus.members.manage.kick.self");
            } else if (city.hasPermission(playerTarget.getUniqueId(), CityPermission.OWNER)) {
                loreKick = TranslationManager.translationLore("feature.city.menus.members.manage.kick.owner");
            } else {
                loreKick = TranslationManager.translationLore(
                        "feature.city.menus.members.manage.kick.info",
                        PlayerNameCache.name(playerTarget.getUniqueId()).color(NamedTextColor.GRAY)
                );
            }
        } else {
            loreKick = List.of(
                    TranslationManager.translation("messages.global.cannot_do_this")
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.OAK_DOOR, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.menus.members.manage.kick.title", Component.text(playerTarget.getName()).color(NamedTextColor.RED)));
            itemMeta.lore(loreKick);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityKickCondition.canCityKickPlayer(city, player, playerTarget))
                return;

            ConfirmMenu menu = new ConfirmMenu(
                    player,
                    () -> {
                        player.closeInventory();
                        CityKickAction.startKick(player, playerTarget);
                    },
                    player::closeInventory,
                    List.of(TranslationManager.translation("feature.city.menus.members.kick.confirm", Component.text(playerTarget.getName()))),
                    List.of(TranslationManager.translation("feature.city.menus.members.kick.deny", Component.text(playerTarget.getName()))));
            menu.open();
        }));


        List<Component> lorePlayerTarget = TranslationManager.translationLore("feature.city.menus.members.manage.target.lore");

        inventory.put(13, new ItemBuilder(this, SkullUtils.getPlayerSkull(playerTarget.getUniqueId()), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.members.manage.target.title", Component.text(playerTarget.getName()).color(NamedTextColor.YELLOW)));
            itemMeta.lore(lorePlayerTarget);
        }));

        List<Component> lorePermission;

        if (hasPermissionPerms) {
            lorePermission = TranslationManager.translationLore("feature.city.menus.members.manage.perms.lore");
        } else {
            lorePermission = List.of(
                    TranslationManager.translation("messages.global.cannot_do_this")
            );
        }

        inventory.put(15, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.menus.members.manage.perms.title"));
            itemMeta.lore(lorePermission);
        }).setOnClick(inventoryClickEvent ->
                new CityPermsMenu(player, playerTarget.getUniqueId(), true).open()
        ));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(TranslationManager.translationLore("feature.city.menus.members.manage.back_lore"));
        }, true));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
