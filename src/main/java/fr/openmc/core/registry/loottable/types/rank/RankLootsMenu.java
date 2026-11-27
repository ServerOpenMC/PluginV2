package fr.openmc.core.registry.loottable.types.rank;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankLootsMenu extends PaginatedMenu {

    private final Component name;
    private final List<RankLoot> ranks;

    public RankLootsMenu(Player owner, Component name, List<RankLoot> ranks) {
        super(owner);
        this.name = name;
        this.ranks = ranks;
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

        for (RankLoot rank : ranks) {
            items.add(new ItemMenuBuilder(this, getRankMaterial(rank), meta -> {
                meta.displayName(getRankTitle(rank).decoration(TextDecoration.ITALIC, false));

                meta.lore(TranslationManager.translationLore("registries.menu.sub_loots.click_here"));
            }).setOnClick(_ -> rank.loots().openMenu(getOwner())));
        }

        return items;
    }

    private Component getRankTitle(RankLoot rank) {
        if (rank.startRank() < 0)
            return TranslationManager.translation("registries.menu.rank.fallback");

        if (rank.startRank() == rank.endRank())
            return TranslationManager.translation("registries.menu.rank.single",
                    Component.text(rank.startRank()));

        return TranslationManager.translation("registries.menu.rank.range",
                Component.text(rank.startRank()),
                Component.text(rank.endRank()));
    }

    private Material getRankMaterial(RankLoot rank) {
        if (rank.startRank() < 0) return Material.CHEST;
        if (rank.startRank() != rank.endRank()) return Material.PAPER;

        return switch (rank.startRank()) {
            case 1 -> Material.GOLD_INGOT;
            case 2 -> Material.IRON_INGOT;
            case 3 -> Material.COPPER_INGOT;
            default -> Material.PAPER;
        };
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
        return ranks.size();
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
    public void onClose(InventoryCloseEvent event) {}
}