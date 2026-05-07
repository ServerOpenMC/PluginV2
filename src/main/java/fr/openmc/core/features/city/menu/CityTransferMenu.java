package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityTransferAction;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityTransferMenu extends PaginatedMenu {

    public CityTransferMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

            for (UUID uuid : city.getMembers()) {
                if (uuid.equals(city.getPlayerWithPermission(CityPermission.OWNER))) {
                    continue;
                }

                OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

                String title = city.getRankName(uuid) + " ";

                items.add(new ItemBuilder(this, SkullUtils.getPlayerSkull(uuid), itemMeta -> {
                    itemMeta.displayName(Component.text(title + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.city.menus.transfer.item.lore",
                            Component.text(title + playerOffline.getName()).color(NamedTextColor.LIGHT_PURPLE)
                    ));
                }).setOnClick(inventoryClickEvent -> {
                    if (!hasPermissionOwner) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.player_isnt_owner"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    CityTransferAction.transfer(player, city, playerOffline);
                }));
            }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"));
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"));
        }).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.transfer.name");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
