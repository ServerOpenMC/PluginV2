package fr.openmc.core.features.city.sub.war.menu.selection;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.MoreInfoMenu;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WarChooseSizeMenu extends PaginatedMenu {

    private final City cityAttack;
    private final City cityLaunch;
    private final int maxSize;

    public WarChooseSizeMenu(Player player, City cityLaunch, City cityAttack, int maxSize) {
        super(player);
        this.cityAttack = cityAttack;
        this.cityLaunch = cityLaunch;
        this.maxSize = maxSize;
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

        for (int i = 1; i <= maxSize; i++) {
            int count = i;

            items.add(new ItemBuilder(this, Material.IRON_SWORD, meta -> {
                meta.displayName(TranslationManager.translation(
                        "feature.city.war.menu.size.title",
                        Component.text(count).color(NamedTextColor.RED)
                ).color(NamedTextColor.RED));
                meta.lore(TranslationManager.translationLore(
                        "feature.city.war.menu.size.lore",
                        Component.text(count).color(NamedTextColor.GRAY)
                ));
            }).setOnClick(event -> {
                WarActions.preFinishLaunchWar(getOwner(), cityLaunch, cityAttack, count);
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
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_cancel")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.close"))).setCloseButton());
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());

        List<Component> loreInfo = TranslationManager.translationLore("feature.city.war.menu.more_info.lore");

        map.put(53, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.war.menu.more_info.title"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));

        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.war.menu.size.menu_title");
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
