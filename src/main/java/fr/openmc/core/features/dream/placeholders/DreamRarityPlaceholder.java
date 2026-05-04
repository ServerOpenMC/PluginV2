package fr.openmc.core.features.dream.placeholders;

import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;

import java.util.Map;

public class DreamRarityPlaceholder implements IAPlaceholder {
    private static final String PLACEHOLDER_NAME = "dream_rarity";

    // todo: temporaire, juste pour faire la base des placeholders
    private static final Map<String, String> DREAM_RARITY_TOOLTIPS = Map.ofEntries(
            Map.entry("corrupted_sculk", tooltip("common")),
            Map.entry("old_pale_oak", tooltip("common")),
            Map.entry("corrupted_string", tooltip("common")),
            Map.entry("creaking_heart", tooltip("rare")),
            Map.entry("old_creaking_axe", tooltip("common")),
            Map.entry("soul_axe", tooltip("rare")),
            Map.entry("soul", tooltip("rare")),
            Map.entry("cloud_fishing_rod", tooltip("epic")),
            Map.entry("meteo_wand", tooltip("legendary")),
            Map.entry("cloud_key", tooltip("rare")),
            Map.entry("somnifere", tooltip("rare")),
            Map.entry("metal_detector", tooltip("epic")),
            Map.entry("ewenite", tooltip("onirique")),
            Map.entry("crystallized_pickaxe", tooltip("legendary")),
            Map.entry("mecanic_pickaxe", tooltip("legendary")),
            Map.entry("glacite", tooltip("epic")),
            Map.entry("coal_burn", tooltip("epic")),
            Map.entry("hard_stone", tooltip("common")),
            Map.entry("crafting_table", tooltip("common")),
            Map.entry("eternal_campfire", tooltip("epic")),
            Map.entry("moon_fish", tooltip("rare")),
            Map.entry("sun_fish", tooltip("rare")),
            Map.entry("poissonion", tooltip("rare")),
            Map.entry("cooked_poissonion", tooltip("rare")),
            Map.entry("dockerfish", tooltip("rare")),
            Map.entry("chips_aywen", tooltip("epic")),
            Map.entry("chips_dihydrogene", tooltip("epic")),
            Map.entry("chips_jimmy", tooltip("epic")),
            Map.entry("chips_lait_2_margouta", tooltip("onirique")),
            Map.entry("chips_nature", tooltip("epic")),
            Map.entry("chips_sans_plomb", tooltip("epic")),
            Map.entry("chips_terre", tooltip("epic")),
            Map.entry("domination_orb", tooltip("onirique")),
            Map.entry("ame_orb", tooltip("onirique")),
            Map.entry("mud_orb", tooltip("onirique")),
            Map.entry("cloud_orb", tooltip("onirique")),
            Map.entry("glacite_orb", tooltip("onirique")),
            Map.entry("singularity", tooltip("onirique"))
    );

    public String name() {
        return PLACEHOLDER_NAME;
    }

    public String resolve(String argument) {
        if (argument == null || argument.isBlank()) {
            return null;
        }

        String key = argument.trim();
        int separatorIndex = key.indexOf(':');
        if (separatorIndex >= 0) {
            key = key.substring(separatorIndex + 1);
        }

        return DREAM_RARITY_TOOLTIPS.get(key);
    }

    private static String tooltip(String rarity) {
        return "omc_tooltips:tooltip/" + rarity;
    }
}

