package fr.openmc.core.features.dream.registries;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.commands.DreamItemCommand;
import fr.openmc.core.features.dream.listeners.registry.DreamItemConvertorListener;
import fr.openmc.core.features.dream.listeners.registry.DreamItemDropsListener;
import fr.openmc.core.features.dream.listeners.registry.DreamItemInteractListener;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.items.armors.cloud.CloudBoots;
import fr.openmc.core.features.dream.registries.items.armors.cloud.CloudChestplate;
import fr.openmc.core.features.dream.registries.items.armors.cloud.CloudHelmet;
import fr.openmc.core.features.dream.registries.items.armors.cloud.CloudLeggings;
import fr.openmc.core.features.dream.registries.items.armors.cold.ColdBoots;
import fr.openmc.core.features.dream.registries.items.armors.cold.ColdChestplate;
import fr.openmc.core.features.dream.registries.items.armors.cold.ColdHelmet;
import fr.openmc.core.features.dream.registries.items.armors.cold.ColdLeggings;
import fr.openmc.core.features.dream.registries.items.armors.creaking.OldCreakingBoots;
import fr.openmc.core.features.dream.registries.items.armors.creaking.OldCreakingChestplate;
import fr.openmc.core.features.dream.registries.items.armors.creaking.OldCreakingHelmet;
import fr.openmc.core.features.dream.registries.items.armors.creaking.OldCreakingLeggings;
import fr.openmc.core.features.dream.registries.items.armors.dream.DreamBoots;
import fr.openmc.core.features.dream.registries.items.armors.dream.DreamChestplate;
import fr.openmc.core.features.dream.registries.items.armors.dream.DreamHelmet;
import fr.openmc.core.features.dream.registries.items.armors.dream.DreamLeggings;
import fr.openmc.core.features.dream.registries.items.armors.pyjama.PyjamaBoots;
import fr.openmc.core.features.dream.registries.items.armors.pyjama.PyjamaChestplate;
import fr.openmc.core.features.dream.registries.items.armors.pyjama.PyjamaHelmet;
import fr.openmc.core.features.dream.registries.items.armors.pyjama.PyjamaLeggings;
import fr.openmc.core.features.dream.registries.items.armors.soul.SoulBoots;
import fr.openmc.core.features.dream.registries.items.armors.soul.SoulChestplate;
import fr.openmc.core.features.dream.registries.items.armors.soul.SoulHelmet;
import fr.openmc.core.features.dream.registries.items.armors.soul.SoulLeggings;
import fr.openmc.core.features.dream.registries.items.blocks.*;
import fr.openmc.core.features.dream.registries.items.consumable.*;
import fr.openmc.core.features.dream.registries.items.fishes.*;
import fr.openmc.core.features.dream.registries.items.loots.*;
import fr.openmc.core.features.dream.registries.items.orb.*;
import fr.openmc.core.features.dream.registries.items.tools.*;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class DreamItemRegistry {
    static final HashMap<String, DreamItem> dreamItems = new HashMap<>();
    public static final String CUSTOM_NAME_KEY = "dream_item";

    public static void init() {
        // # ORBES
        registerDreamItem(new DominationOrb("omc_dream:domination_orb"));
        registerDreamItem(new SoulOrb("omc_dream:ame_orb"));
        registerDreamItem(new MudOrb("omc_dream:mud_orb"));
        registerDreamItem(new CloudOrb("omc_dream:cloud_orb"));
        registerDreamItem(new GlaciteOrb("omc_dream:glacite_orb"));
        registerDreamItem(new Singularity("omc_dream:singularity"));

        // # DROPS
        registerDreamItem(new CorruptedString("omc_dream:corrupted_string"));
        registerDreamItem(new CreakingHeart("omc_dream:creaking_heart"));
        registerDreamItem(new Soul("omc_dream:soul"));
        registerDreamItem(new Soul("omc_dream:cloud_key"));
        registerDreamItem(new CorruptedSculk("omc_dream:corrupted_sculk"));
        registerDreamItem(new OldPaleOakWood("omc_dream:old_pale_oak"));
        registerDreamItem(new Glacite("omc_dream:glacite"));
        registerDreamItem(new BurnCoal("omc_dream:coal_burn"));
        registerDreamItem(new HardStone("omc_dream:hard_stone"));
        registerDreamItem(new CraftingTable("omc_dream:crafting_table"));
        registerDreamItem(new EternalCampFire("omc_dream:eternal_campfire"));
        registerDreamItem(new Ewenite("omc_dream:ewenite"));

        // # CONSUMABLES
        registerDreamItem(new Somnifere("omc_dream:somnifere"));
        registerDreamItem(new ChipsAywen("omc_dream:chips_aywen"));
        registerDreamItem(new ChipsDihydrogene("omc_dream:chips_dihydrogene"));
        registerDreamItem(new ChipsJimmy("omc_dream:chips_jimmy"));
        registerDreamItem(new ChipsLait2Margouta("omc_dream:lait_2_margouta"));
        registerDreamItem(new ChipsNature("omc_dream:chips_nature"));
        registerDreamItem(new ChipsSansPlomb("chips_sans_plomb"));
        registerDreamItem(new ChipsTerre("omc_dream:chips_terre"));

        // # FISHES
        registerDreamItem(new CokkedPoissonion("omc_dream:cooked_poissonion"));
        registerDreamItem(new Poissonion("omc_dream:poissonion"));
        registerDreamItem(new MoonFish("omc_dream:moon_fish"));
        registerDreamItem(new SunFish("omc_dream:sun_fish"));
        registerDreamItem(new DockerFish("omc_dream:dockerfish"));

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

        registerDreamItem(new ColdHelmet("omc_dream:cold_helmet"));
        registerDreamItem(new ColdChestplate("omc_dream:cold_chestplate"));
        registerDreamItem(new ColdLeggings("omc_dream:cold_leggings"));
        registerDreamItem(new ColdBoots("omc_dream:cold_boots"));

        registerDreamItem(new DreamHelmet("omc_dream:dream_helmet"));
        registerDreamItem(new DreamChestplate("omc_dream:dream_chestplate"));
        registerDreamItem(new DreamLeggings("omc_dream:dream_leggings"));
        registerDreamItem(new DreamBoots("omc_dream:dream_boots"));

        registerDreamItem(new PyjamaHelmet("omc_dream:pyjama_helmet"));
        registerDreamItem(new PyjamaChestplate("omc_dream:pyjama_chestplate"));
        registerDreamItem(new PyjamaLeggings("omc_dream:pyjama_leggings"));
        registerDreamItem(new PyjamaBoots("omc_dream:pyjama_boots"));

        // # TOOLS
        registerDreamItem(new CrystalizedPickaxe("omc_dream:crystalized_pickaxe"));
        registerDreamItem(new MecanicPickaxe("omc_dream:mecanic_pickaxe"));
        registerDreamItem(new SoulAxe("omc_dream:soul_axe"));
        registerDreamItem(new OldCreakingAxe("omc_dream:old_creaking_axe"));
        registerDreamItem(new OldCreakingAxe("omc_dream:cloud_fishing_rod"));
        registerDreamItem(new MeteoWand("omc_dream:meteo_wand"));
        registerDreamItem(new MetalDetector("omc_dream:metal_detector"));

        CommandsManager.getHandler().register(
                new DreamItemCommand()
        );

        OMCPlugin.registerEvents(
                new DreamItemConvertorListener(),
                new DreamItemInteractListener(),
                new DreamItemDropsListener()
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
        String name = ItemUtils.getTag(stack, CUSTOM_NAME_KEY);

        return name == null ? null : getByName(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(dreamItems.keySet());
    }
}
