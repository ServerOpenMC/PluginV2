package fr.openmc.core.features.city.sub.milestone.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.features.city.sub.milestone.CityRewards;
import fr.openmc.core.features.city.sub.milestone.requirements.ItemDepositRequirement;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import java.util.stream.IntStream;

public class LevelMilestoneMenu extends PaginatedMenu {

    private final City city;
    private final CityLevels level;

    public LevelMilestoneMenu(Player owner, City city, CityLevels level) {
        super(owner);
        this.city = city;
        this.level = level;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Levels";
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return level.getRequirements().size();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {

    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return IntStream.rangeClosed(0, 53)
                .filter(i -> i < 19 || i > 25)
                .boxed()
                .toList();
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (CityRequirement requirement : level.getRequirements()) {
            List<Component> loreRequirement = new ArrayList<>();

            if (requirement instanceof ItemDepositRequirement && !requirement.isDone(city, level)) {
                loreRequirement.add(Component.text("§e§lCLIQUE POUR DÉPOSER UN"));
                loreRequirement.add(Component.text("§e§lSHIFT-CLIQUE POUR TOUT DÉPOSER"));
            }

            items.add(new ItemBuilder(this, requirement.getIcon(city), meta -> {
                meta.displayName(Component.text((requirement.isDone(city, level) ? "§l✔ " : "§l✖ "))
                        .append(requirement.getName(city, level)).color(requirement.isDone(city, level) ? NamedTextColor.GREEN : NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                meta.lore(loreRequirement);
                meta.setEnchantmentGlintOverride(requirement.isDone(city, level));
            }).setOnClick(inventoryClickEvent -> {
                if (requirement instanceof ItemDepositRequirement r) {
                    r.runAction(this, city, inventoryClickEvent);
                }
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();

        boolean completed = level.ordinal() < city.getLevel();
        boolean active = level.ordinal() == city.getLevel();

        buttons.put(4, CityMilestoneMenu.getGenerateItemLevel(this, level, city, completed, active));

        List<Component> loreRewards = new ArrayList<>();

        for (CityRewards reward : level.getRewards()) {
            loreRewards.add(Component.text(" ").append(reward.getName()).decoration(TextDecoration.ITALIC, false));
        }

        buttons.put(31, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.itemName(Component.text("§6Récompenses"));
            itemMeta.lore(loreRewards);
        }));

        buttons.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
        }, true));

        buttons.put(49, new ItemBuilder(this, Material.BARRIER, meta ->
                meta.displayName(Component.text("§cFermer"))).setCloseButton());
        buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cPage précédente"));
        }).setPreviousPageButton());
        buttons.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
        }).setNextPageButton());

        return buttons;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

}