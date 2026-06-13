package fr.openmc.core.features.city.sub.war.menu.selection;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.MoreInfoMenu;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            items.add(new ItemMenuBuilder(this, Material.IRON_SWORD, meta -> {
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
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(49, ItemMenuTemplate.BTN_CANCEL.apply(this));
        map.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        map.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));
        List<Component> loreInfo = TranslationManager.translationLore("feature.city.war.menu.more_info.lore");

        map.put(53, new ItemMenuBuilder(this, Material.BOOK, itemMeta -> {
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
