package fr.openmc.core.features.city.sub.milestone.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.features.city.sub.milestone.CityRewards;
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CityMilestoneMenu extends Menu {

    private final City city;

    public CityMilestoneMenu(Player owner, City city) {
        super(owner);
        this.city = city;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Levels";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        int currentLevel = city.getLevel();

        int[] levelSlots = {0, 27, 29, 2, 4, 31, 33, 6, 8, 53};

        int[][] pathSlots = {
                {9, 18},
                {28},
                {11, 20},
                {3},
                {13, 22},
                {32},
                {15, 24},
                {7},
                {17, 26, 35, 44}
        };

        CityLevels[] levels = CityLevels.values();
        for (int i = 0; i < levels.length; i++) {
            CityLevels level = levels[i];
            int slot = levelSlots[i];
            boolean completed = i < currentLevel;
            boolean active = i == currentLevel;

            Supplier<ItemBuilder> upgradeItemSupplier = () -> getGenerateItemLevel(this, level, city, completed, active)
                    .setOnClick(e -> {
                        if (!active) return;

                        if (DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:upgrade-level") > 0) return;

                        if (level.isCompleted(city) && DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:upgrade-level") == 0) {
                            level.runUpgradeTime(city);
                            new CityMilestoneMenu(player, city).open();
                            return;
                        }

                        new LevelMilestoneMenu(player, city, level).open();
                    });

            if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:upgrade-level") && active) {
                MenuUtils.runDynamicItem(player, this, slot, upgradeItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                inventory.put(slot, new ItemBuilder(this, upgradeItemSupplier.get()));
            }

            if (i < levels.length - 1) {
                boolean unlocked = i < currentLevel;
                Material glass = unlocked ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;

                for (int pathSlot : pathSlots[i]) {
                    inventory.put(pathSlot, new ItemBuilder(this, glass, meta -> meta.displayName(Component.empty())).hideTooltip(true));
                }
            }
        }

        inventory.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.itemName(Component.text("§aRetour")), true));

        inventory.put(49, new ItemBuilder(this, Material.BARRIER, meta ->
                meta.displayName(Component.text("§cFermer"))).setCloseButton());

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    /**
     * Génère un item représentatif d'un niveau de ville dans le menu.
     *
     * <p>Selon l'état du niveau (complété, actif ou non), l'item est construit avec un matériau spécifique,
     * un affichage de nom coloré et une description (lore) détaillant les conditions requises, les récompenses
     * et l'état d'activation.</p>
     *
     * @param menu      le menu auquel appartient l'item
     * @param level     le niveau de la ville à représenter
     * @param city      la ville concernée
     * @param completed indique si le niveau est déjà complété
     * @param active    indique si le niveau est actuellement actif
     * @return un {@code ItemBuilder} configuré pour le niveau
     */
    public static ItemBuilder getGenerateItemLevel(Menu menu, CityLevels level, City city, boolean completed, boolean active) {
        ItemBuilder itemBuilder = new ItemBuilder(menu,
                completed ? Material.EMERALD_BLOCK : active ? Material.IRON_BLOCK : Material.COAL_BLOCK,
                meta -> {
                    meta.displayName(level.getName()
                            .color(completed ? NamedTextColor.GREEN : active ? NamedTextColor.YELLOW : NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                    meta.setEnchantmentGlintOverride(active);
                });

        List<Component> lore = new ArrayList<>();
        lore.add(level.getDescription().color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC));
        lore.add(Component.empty());
        lore.add(Component.text("§3§lRequis :"));

        for (CityRequirement requirement : level.getRequirements()) {
            lore.add(Component.text((city.getLevel() < level.ordinal() ? "§l¤ " : requirement.isDone(city, level) ? "§l✔ " : "§l✖ "))
                    .append(requirement.getName(city, level))
                    .color(city.getLevel() < level.ordinal() ? NamedTextColor.DARK_GRAY : requirement.isDone(city, level) ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());
        lore.add(Component.text("§6§lRécompenses :"));

        for (CityRewards reward : level.getRewards()) {
            lore.add(Component.text(" ").append(reward.getName()).decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());
        if (completed) {
            lore.add(Component.text("§a§lDÉBLOQUÉ"));
        } else {
            lore.add(Component.empty());
            if (DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:upgrade-level") != 0 && city.getLevel() + 1 == level.ordinal() + 1) {
                lore.add(Component.text("§fIl reste §3" +
                        DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:upgrade-level")) +
                        " §fde débloquage"));
            } else {
                lore.add(Component.text("§3" +
                        DateUtils.convertSecondToTime(level.getUpgradeTime()) +
                        " §fde débloquage"));
            }
        }

        if (active && DynamicCooldownManager.isReady(city.getUniqueId(), "city:upgrade-level") && level.isCompleted(city)) {
            lore.add(Component.text("§e§lCLIQUEZ ICI POUR LANCER l'AMÉLIORATION"));
        } else if (active && DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:upgrade-level") == 0) {
            lore.add(Component.text("§e§lCLIQUEZ ICI POUR CONTRIBUER"));
        }

        itemBuilder.lore(lore);

        return itemBuilder;
    }
}