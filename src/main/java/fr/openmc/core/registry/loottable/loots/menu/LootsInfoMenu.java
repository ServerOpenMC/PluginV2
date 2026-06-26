package fr.openmc.core.registry.loottable.loots.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry.SeaCreatureLoot;
import fr.openmc.core.registry.loottable.loots.*;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class LootsInfoMenu extends PaginatedMenu {

    private final Component name;
    private final Collection<CustomLoot> loots;

    public LootsInfoMenu(Player owner, Component name, Collection<CustomLoot> loots) {
        super(owner);
        this.name = name;
        this.loots = loots;
    }

    @Override
    public Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NonNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (CustomLoot loot : loots) {
            ItemStack icon = (loot instanceof RepresentedItem represented)
                    ? represented.getRepresentativeItem()
                    : new ItemStack(Material.BARRIER);

            items.add(new ItemMenuBuilder(this, icon, meta -> {
                Component displayText = loot.getDisplayText();
                if (loot instanceof ItemLoot itemLoot)
                    displayText = itemLoot.getSimpleText();
                meta.displayName(displayText);

                List<Component> lore = new ArrayList<>();
                lore.add(TranslationManager.translation(
                        "registries.menu.chance",
                        Component.text(String.format("%.2f", loot.getChance() * 100) + "%", NamedTextColor.AQUA)
                ));

                if (loot instanceof TableLoot ||
                        loot instanceof LootboxLoot ||
                        loot instanceof SeaCreatureLoot) {
                    lore.add(TranslationManager.translation("registries.menu.sub_loots.click_here"));
                }

                meta.lore(lore);
            }).setOnClick(_ -> {
                if (loot instanceof TableLoot subLootTable) {
                    subLootTable.getLootTable().openMenu(getOwner());
                } else if (loot instanceof LootboxLoot lootboxLoot) {
                    lootboxLoot.getLootbox().openInfo(getOwner());
                } else if (loot instanceof SeaCreatureLoot seaCreatureLoot) {
                    seaCreatureLoot.showLoot(getOwner());
                }
            }));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public @NonNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public int getSizeOfItems() {
        return loots.size();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(27, new ItemMenuBuilder(this, Material.ARROW, true));

        map.put(30, ItemMenuTemplate.BTN_PREVIOUS_PAGE_WHITE.apply(this));
        map.put(31, ItemMenuTemplate.BTN_CANCEL.apply(this));
        map.put(32, ItemMenuTemplate.BTN_NEXT_PAGE_WHITE.apply(this));
        return map;
    }

    @Override
    public @NonNull Component getName() {
        return name;
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
