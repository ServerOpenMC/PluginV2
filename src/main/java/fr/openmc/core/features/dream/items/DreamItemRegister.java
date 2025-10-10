package fr.openmc.core.features.dream.items;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.dream.items.armors.cloud.CloudBoots;
import fr.openmc.core.features.dream.items.armors.cloud.CloudChestplate;
import fr.openmc.core.features.dream.items.armors.cloud.CloudHelmet;
import fr.openmc.core.features.dream.items.armors.cloud.CloudLeggings;
import fr.openmc.core.features.dream.items.armors.creaking.OldCreakingBoots;
import fr.openmc.core.features.dream.items.armors.creaking.OldCreakingChestplate;
import fr.openmc.core.features.dream.items.armors.creaking.OldCreakingHelmet;
import fr.openmc.core.features.dream.items.armors.creaking.OldCreakingLeggings;
import fr.openmc.core.features.dream.items.armors.soul.SoulBoots;
import fr.openmc.core.features.dream.items.armors.soul.SoulChestplate;
import fr.openmc.core.features.dream.items.armors.soul.SoulHelmet;
import fr.openmc.core.features.dream.items.armors.soul.SoulLeggings;
import fr.openmc.core.features.dream.items.blocks.CorruptedSculk;
import fr.openmc.core.features.dream.items.blocks.OldPaleOakWood;
import fr.openmc.core.features.dream.items.loots.CorruptedString;
import fr.openmc.core.features.dream.items.loots.CreakingHeart;
import fr.openmc.core.features.dream.items.loots.Soul;
import fr.openmc.core.features.dream.items.orb.*;
import fr.openmc.core.features.dream.items.tools.OldCreakingAxe;
import fr.openmc.core.features.dream.items.tools.SoulAxe;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.autocomplete.SuggestionProvider;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class DreamItemRegister {
    static final HashMap<String, DreamItem> dreamItems = new HashMap<>();
    static final String customNameKey = "dream_item";

    public static void init() {
        // # ORBES
        registerDreamItem(new DominationOrb("omc_dream:domination_orb"));
        registerDreamItem(new SoulOrb("omc_dream:ame_orb"));
        registerDreamItem(new MudOrb("omc_dream:mud_orb"));
        registerDreamItem(new CloudOrb("omc_dream:cloud_orb"));
        registerDreamItem(new GlaciteOrb("omc_dream:glacite_orb"));

        // # BLOCS
        registerDreamItem(new CorruptedSculk("omc_dream:corrupted_sculk"));
        registerDreamItem(new OldPaleOakWood("omc_dream:old_pale_oak"));

        // # DROPS
        registerDreamItem(new CorruptedString("omc_dream:corrupted_string"));
        registerDreamItem(new CreakingHeart("omc_dream:creaking_heart"));
        registerDreamItem(new Soul("omc_dream:soul"));
        registerDreamItem(new Soul("omc_dream:cloud_key"));

        // # ARMURES
        registerDreamItem(new OldCreakingHelmet("omc_dream:old_creaking_helmet"));
        registerDreamItem(new OldCreakingChestplate("omc_dream:old_creaking_chestplate"));
        registerDreamItem(new OldCreakingLeggings("omc_dream:old_creaking_leggings"));
        registerDreamItem(new OldCreakingBoots("omc_dream:old_creaking_boots"));

        registerDreamItem(new SoulHelmet("omc_dream:soul_helmet"));
        registerDreamItem(new SoulChestplate("omc_dream:soul_chestplate"));
        registerDreamItem(new SoulLeggings("omc_dream:soul_leggings"));
        registerDreamItem(new SoulBoots("omc_dream:soul_boots"));

        registerDreamItem(new CloudHelmet("omc_dream:cloud_helmet"));
        registerDreamItem(new CloudChestplate("omc_dream:cloud_chestplate"));
        registerDreamItem(new CloudLeggings("omc_dream:cloud_leggings"));
        registerDreamItem(new CloudBoots("omc_dream:cloud_boots"));

        // # WEAPONS
        registerDreamItem(new SoulAxe("omc_dream:soul_axe"));
        registerDreamItem(new OldCreakingAxe("omc_dream:old_creaking_axe"));

        registerDreamItem(new OldCreakingAxe("omc_dream:cloud_fishing_rod"));

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("dream_item", SuggestionProvider.of(getNames()));
        CommandsManager.getHandler().register(
                new DreamItemCommand()
        );
    }

    public static void register(String name, DreamItem item) {
        if (!name.matches("[a-zA-Z0-9_:]+")) {
            throw new IllegalArgumentException("Custom item name dont match regex \"[a-zA-Z0-9_:]+\"");
        }

        dreamItems.put(name, item);
    }

    public static void registerDreamItem(DreamItem item) {
        register(item.getName(), item);
    }

    @Nullable
    public static DreamItem getByName(String name) {
        return dreamItems.get(name);
    }

    @Nullable
    public static DreamItem getByItemStack(ItemStack stack) {
        String name = ItemUtils.getTag(stack, customNameKey);

        return name == null ? null : getByName(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(dreamItems.keySet());
    }
}
