package fr.openmc.core.features.city.menu.list;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
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

public class CityPlayerListMenu extends PaginatedMenu {

    private final City city;

    public CityPlayerListMenu(Player owner, City city) {
        super(owner);
        this.city = city;
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
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (UUID uuid : city.getMembers()) {
            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

            if (playerOffline == null) continue;

            String title = city.getRankName(uuid) + " ";

            items.add(new ItemMenuBuilder(this, SkullUtils.getPlayerSkull(uuid), itemMeta -> itemMeta.displayName(Component.text(title + playerOffline.getName()).decoration(TextDecoration.ITALIC, false))));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(45, new ItemMenuBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(TranslationManager.translationLore("feature.city.menus.list.members.back_lore"));
        }, true));
        map.put(49, new ItemMenuBuilder(this, Objects.requireNonNull(OMCRegistry.CUSTOM_ITEMS.get("_iainternal:icon_cancel")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.close"))).setCloseButton());
        map.put(48, new ItemMenuBuilder(this, Objects.requireNonNull(OMCRegistry.CUSTOM_ITEMS.get("_iainternal:icon_back_orange")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
        map.put(50, new ItemMenuBuilder(this, Objects.requireNonNull(OMCRegistry.CUSTOM_ITEMS.get("_iainternal:icon_next_orange")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.list.members.name", Component.text(city.getName()));
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
