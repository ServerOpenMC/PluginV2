package fr.openmc.core.features.city.sub.mayor.perks;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@Getter
public enum Perks {
    FOU_DE_RAGE(
            1,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "feature.city.mayor.perk.fou_de_rage.name",
            "feature.city.mayor.perk.fou_de_rage.lore",
            ItemStack.of(Material.BLAZE_POWDER)
    ),
    IMPOT(
            2,
            PerkType.EVENT,
            PerkCategory.ECONOMIC,
            3 * 24 * 60 * 60 * 1000L, // 3 jours
            "feature.city.mayor.perk.impot.name",
            "feature.city.mayor.perk.impot.lore",
            ItemStack.of(Material.GOLD_BLOCK)
    ),
    MINER(
            3,
            PerkType.BASIC,
            PerkCategory.AGRICULTURAL,
            0,
            "feature.city.mayor.perk.miner.name",
            "feature.city.mayor.perk.miner.lore",
            ItemStack.of(Material.GOLDEN_PICKAXE),
            DataComponentTypes.ATTRIBUTE_MODIFIERS
    ),
    FRUIT_DEMON(
            4,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "feature.city.mayor.perk.demon_fruit.name",
            "feature.city.mayor.perk.demon_fruit.lore",
            ItemStack.of(Material.CHORUS_FRUIT)
    ),
    BUSINESS_MAN(
            5,
            PerkType.BASIC,
            PerkCategory.ECONOMIC,
            0,
            "feature.city.mayor.perk.business_man.name",
            "feature.city.mayor.perk.business_man.lore",
            ItemStack.of(Material.DIAMOND)
    ),
    IRON_BLOOD(
            6,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "feature.city.mayor.perk.iron_man.name",
            "feature.city.mayor.perk.iron_man.lore",
            ItemStack.of(Material.IRON_BLOCK)
    ),
    CITY_HUNTER(
            7,
            PerkType.BASIC,
            PerkCategory.STRATEGY,
            0,
            "feature.city.mayor.perk.city_hunter.name",
            "feature.city.mayor.perk.city_hunter.lore",
            ItemStack.of(Material.BOW)
    ),
    AYWENITER(
            8,
            PerkType.BASIC,
            PerkCategory.AGRICULTURAL,
            0,
            "feature.city.mayor.perk.ayweniter.name",
            "feature.city.mayor.perk.ayweniter.lore",
            CustomItemRegistry.getByName("omc_items:aywenite").getBest()
    ),
    GPS_TRACKER(
            9,
            PerkType.BASIC,
            PerkCategory.STRATEGY,
            0,
            "feature.city.mayor.perk.gps_tracker.name",
            "feature.city.mayor.perk.gps_tracker.lore",
            ItemStack.of(Material.COMPASS)
    ),
    SYMBIOSIS(
            10,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "feature.city.mayor.perk.symbiosis.name",
            "feature.city.mayor.perk.symbiosis.lore",
            ItemStack.of(Material.SCULK_CATALYST)
    ),
    AGRICULTURAL_ESSOR(
            11,
            PerkType.EVENT,
            PerkCategory.AGRICULTURAL,
            24 * 60 * 60 * 1000L, // 1 jour
            "feature.city.mayor.perk.agricultural_essor.name",
            "feature.city.mayor.perk.agricultural_essor.lore",
            ItemStack.of(Material.NETHERITE_HOE),
            DataComponentTypes.ATTRIBUTE_MODIFIERS
    ),
    MINERAL_RUSH(
            12,
            PerkType.EVENT,
            PerkCategory.AGRICULTURAL,
            24 * 60 * 60 * 1000L, // 1 jour
            "feature.city.mayor.perk.mineral_rush.name",
            "feature.city.mayor.perk.mineral_rush.lore",
            ItemStack.of(Material.DIAMOND_PICKAXE),
            DataComponentTypes.ATTRIBUTE_MODIFIERS
    ),
    MILITARY_DISSUASION(
            13,
            PerkType.EVENT,
            PerkCategory.STRATEGY,
            25 * 60 * 1000L, // 25 minutes
            "feature.city.mayor.perk.military_dissuasion.name",
            "feature.city.mayor.perk.military_dissuasion.lore",
            ItemStack.of(Material.IRON_GOLEM_SPAWN_EGG)
    ),
    IDYLLIC_RAIN(
            14,
            PerkType.EVENT,
            PerkCategory.ECONOMIC,
            24 * 60 * 60 * 1000L, // 1 jour
            "feature.city.mayor.perk.idyllic_rain.name",
            "feature.city.mayor.perk.idyllic_rain.lore",
            ItemStack.of(Material.GHAST_TEAR)
    ),
    MASCOTS_FRIENDLY(
            15,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "feature.city.mayor.perk.mascots_friendly.name",
            "feature.city.mayor.perk.mascots_friendly.lore",
            ItemStack.of(Material.SADDLE)
    ),
    GREAT_SLEEPER(
            16,
            PerkType.BASIC,
            PerkCategory.DREAM,
            0,
            "feature.city.mayor.perk.great_sleeper.name",
            "feature.city.mayor.perk.great_sleeper.lore",
            ItemStack.of(Material.WHITE_BED)
    ),
    GREAT_DREAM(
            17,
            PerkType.BASIC,
            PerkCategory.DREAM,
            0,
            "feature.city.mayor.perk.great_dream.name",
            "feature.city.mayor.perk.great_dream.lore",
            DreamItemRegistry.getByName("somnifere").getBest()
    ),
    CHAOS_DREAM(
            18,
            PerkType.EVENT,
            PerkCategory.DREAM,
            24 * 60 * 60 * 1000L, // 1 jour
            "feature.city.mayor.perk.chaos_dream.name",
            "feature.city.mayor.perk.chaos_dream.lore",
            DreamItemRegistry.getByName("singularity").getBest()
    )
    ;

    private final int id;
    private final PerkType type;
    private final PerkCategory category;
    private final long cooldown;
    private final String nameKey;
    private final String loreKey;
    private final ItemStack itemStack;
    private final DataComponentType[] toHide;

    Perks(int id, PerkType type, PerkCategory category, long cooldown, @NotNull String nameKey, String loreKey, ItemStack itemStack, DataComponentType... toHide) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.cooldown = cooldown;
        this.nameKey = nameKey;
        this.loreKey = loreKey;
        this.itemStack = itemStack;
        this.toHide = toHide;
    }
}
