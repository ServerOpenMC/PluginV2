package fr.openmc.core.features.dream.registries;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.commands.DreamItemCommand;
import fr.openmc.core.features.dream.listeners.registry.DreamItemConvertorListener;
import fr.openmc.core.features.dream.listeners.registry.DreamItemDropsListener;
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
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class DreamItemRegistry {
    public static void init() {
        // # ORBES
        CustomItemRegistry.registerItems(
                new DominationOrb("omc_dream:domination_orb"),
                new SoulOrb("omc_dream:ame_orb"),
                new MudOrb("omc_dream:mud_orb"),
                new CloudOrb("omc_dream:cloud_orb"),
                new GlaciteOrb("omc_dream:glacite_orb"),
                new Singularity("omc_dream:singularity")
        );

        // # DROPS
        CustomItemRegistry.registerItems(
                new CorruptedString("omc_dream:corrupted_string"),
                new CreakingHeart("omc_dream:creaking_heart"),
                new Soul("omc_dream:soul"),
                new CloudOrb("omc_dream:cloud_orb"),
                new CloudKey("omc_dream:cloud_key"),
                new CorruptedSculk("omc_dream:corrupted_sculk"),
                new OldPaleOakWood("omc_dream:old_pale_oak"),
                new Glacite("omc_dream:glacite"),
                new BurnCoal("omc_dream:coal_burn"),
                new HardStone("omc_dream:hard_stone"),
                new CraftingTable("omc_dream:crafting_table"),
                new EternalCampFire("omc_dream:eternal_campfire"),
                new Ewenite("omc_dream:ewenite")
        );

        // # CONSUMABLES
        CustomItemRegistry.registerItems(
                new Somnifere("omc_dream:somnifere"),
                new ChipsAywen("omc_dream:chips_aywen"),
                new ChipsDihydrogene("omc_dream:chips_dihydrogene"),
                new ChipsJimmy("omc_dream:chips_jimmy"),
                new ChipsLait2Margouta("omc_dream:chips_lait_2_margouta"),
                new ChipsNature("omc_dream:chips_nature"),
                new ChipsSansPlomb("omc_dream:chips_sans_plomb"),
                new ChipsTerre("omc_dream:chips_terre")
        );

        // # FISHES
        CustomItemRegistry.registerItems(
                new CokkedPoissonion("omc_dream:cooked_poissonion"),
                new Poissonion("omc_dream:poissonion"),
                new MoonFish("omc_dream:moon_fish"),
                new SunFish("omc_dream:sun_fish"),
                new DockerFish("omc_dream:dockerfish")
        );

        // # ARMURES
        CustomItemRegistry.registerItems(
                new OldCreakingHelmet("omc_dream:old_creaking_helmet"),
                new OldCreakingChestplate("omc_dream:old_creaking_chestplate"),
                new OldCreakingLeggings("omc_dream:old_creaking_leggings"),
                new OldCreakingBoots("omc_dream:old_creaking_boots"),

                new SoulHelmet("omc_dream:soul_helmet"),
                new SoulChestplate("omc_dream:soul_chestplate"),
                new SoulLeggings("omc_dream:soul_leggings"),
                new SoulBoots("omc_dream:soul_boots"),

                new CloudHelmet("omc_dream:cloud_helmet"),
                new CloudChestplate("omc_dream:cloud_chestplate"),
                new CloudLeggings("omc_dream:cloud_leggings"),
                new CloudBoots("omc_dream:cloud_boots"),

                new ColdHelmet("omc_dream:cold_helmet"),
                new ColdChestplate("omc_dream:cold_chestplate"),
                new ColdLeggings("omc_dream:cold_leggings"),
                new ColdBoots("omc_dream:cold_boots"),

                new DreamHelmet("omc_dream:dream_helmet"),
                new DreamChestplate("omc_dream:dream_chestplate"),
                new DreamLeggings("omc_dream:dream_leggings"),
                new DreamBoots("omc_dream:dream_boots"),

                new PyjamaHelmet("omc_dream:pyjama_helmet"),
                new PyjamaChestplate("omc_dream:pyjama_chestplate"),
                new PyjamaLeggings("omc_dream:pyjama_leggings"),
                new PyjamaBoots("omc_dream:pyjama_boots")
        );

        // # TOOLS
        CustomItemRegistry.registerItems(
                new OldCreakingAxe("omc_dream:old_creaking_axe"),
                new SoulAxe("omc_dream:soul_axe"),
                new CloudFishingRod("omc_dream:cloud_fishing_rod"),
                new MeteoWand("omc_dream:meteo_wand"),
                new MetalDetector("omc_dream:metal_detector"),
                new CrystalizedPickaxe("omc_dream:crystallized_pickaxe"),
                new MecanicPickaxe("omc_dream:mecanic_pickaxe")
        );

        CommandsManager.getHandler().register(
                new DreamItemCommand()
        );

        OMCPlugin.registerEvents(
                new DreamItemConvertorListener(),
                new DreamItemDropsListener()
        );
    }

    @Nullable
    public static DreamItem getByName(String name) {
        if (!name.startsWith("omc_dream:")) name = "omc_dream:" + name;

        CustomItem ci = CustomItemRegistry.getByName(name);
        if (ci == null) return null;
        if (!(ci instanceof DreamItem di)) return null;

        return di;
    }

    @Nullable
    public static DreamItem getByItemStack(ItemStack stack) {
        CustomItem ci = CustomItemRegistry.getByItemStack(stack);

        if (ci == null) return null;
        if (!(ci instanceof DreamItem di)) return null;

        return di;
    }
}
