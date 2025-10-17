package fr.openmc.core.features.credits;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CreditsMenu extends PaginatedMenu {

    public CreditsMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
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
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Credits credit : Credits.values()) {
            List<Component> lore = new ArrayList<>();

            lore.add(Component.text("§7Développeur(s) : §9" + String.join(", ", credit.getDeveloppers())));
            if (!credit.getGraphists().isEmpty()) {
                lore.add(Component.text("§7Graphiste(s) : §6" + String.join(", ", credit.getGraphists())));
            }
            if (!credit.getBuilders().isEmpty()) {
                lore.add(Component.text("§7Builder(s) : §a" + String.join(", ", credit.getBuilders())));
            }

            ItemBuilder item = new ItemBuilder(this, credit.getIcon(), itemMeta -> {
                itemMeta.displayName(Component.text("§e" + credit.getFeatureName())
                        .decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(lore);
            }).hide(ItemUtils.getDataComponentType());

            items.add(item);
        }

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();

        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
        map.put(49, new ItemBuilder(this, Material.BARRIER, meta -> {
            meta.displayName(Component.text("§cFermer le menu"));
        }).setOnClick(e -> getOwner().closeInventory()));

        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Crédits du serveur";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
