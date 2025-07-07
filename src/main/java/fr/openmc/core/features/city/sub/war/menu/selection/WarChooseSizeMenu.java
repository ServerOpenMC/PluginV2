package fr.openmc.core.features.city.sub.war.menu.selection;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.MoreInfoMenu;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
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
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 1; i <= maxSize; i++) {
            int count = i;

            items.add(new ItemBuilder(this, Material.IRON_SWORD, meta -> {
                meta.displayName(Component.text("§c" + count + " vs " + count));
                meta.lore(List.of(
                        Component.text("§7Affrontement entre " + count + " §7joueurs de chaque ville."),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ POUR CONTINUER")
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
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_cancel")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Guerres !"),
                Component.text("§7La préparation..., le combat, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN SAVOIR PLUS!")
        );

        map.put(53, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des guerres - Séléction";
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
