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
import java.util.*;

public class DreamItemRegistry {
    // * Registre des DreamItems accessible durant le bootstrap
    public static Set<CustomItem> DREAM_ITEM_REGISTRY = new HashSet<>(List.of(
            new DominationOrb(),
            new SoulOrb(),
            new MudOrb(),
            new CloudOrb(),
            new GlaciteOrb(),
            new Singularity(),

            new CorruptedString(),
            new CreakingHeart(),
            new Soul(),
            new CloudKey(),

            new CorruptedSculk(),
            new OldPaleOakWood(),
            new Glacite(),
            new BurnCoal(),
            new HardStone(),
            new CraftingTable(),
            new EternalCampFire(),
            new Ewenite(),

            new Somnifere(),
            new ChipsAywen(),
            new ChipsDihydrogene(),
            new ChipsJimmy(),
            new ChipsLait2Margouta(),
            new ChipsNature(),
            new ChipsSansPlomb(),
            new ChipsTerre(),
            new CokkedPoissonion(),
            new Poissonion(),
            new MoonFish(),
            new SunFish(),
            new DockerFish(),

            new OldCreakingHelmet(),
            new OldCreakingChestplate(),
            new OldCreakingLeggings(),
            new OldCreakingBoots(),

            new SoulHelmet(),
            new SoulChestplate(),
            new SoulLeggings(),
            new SoulBoots(),

            new CloudHelmet(),
            new CloudChestplate(),
            new CloudLeggings(),
            new CloudBoots(),

            new ColdHelmet(),
            new ColdChestplate(),
            new ColdLeggings(),
            new ColdBoots(),

            new DreamHelmet(),
            new DreamChestplate(),
            new DreamLeggings(),
            new DreamBoots(),

            new PyjamaHelmet(),
            new PyjamaChestplate(),
            new PyjamaLeggings(),
            new PyjamaBoots(),

            new OldCreakingAxe(),
            new SoulAxe(),
            new CloudFishingRod(),
            new MeteoWand(),
            new MetalDetector(),
            new CrystalizedPickaxe(),
            new MecanicPickaxe()
    ));

    // * Registre des DreamItems accesible via leur id, tres utilisé durant le bootstrap
    private static Map<String, DreamItem> DREAM_ITEM_BY_NAME_REGISTRY;

    /**
     * Charge la classe durant le runtime
     */
    public static void init() {
        CustomItemRegistry.registerItems(DREAM_ITEM_REGISTRY);

        CommandsManager.getHandler().register(
                new DreamItemCommand()
        );

        OMCPlugin.registerEvents(
                new DreamItemConvertorListener(),
                new DreamItemDropsListener()
        );
    }

    public static Map<String, DreamItem> getBootstrapRegistry() {
        if (DREAM_ITEM_BY_NAME_REGISTRY == null) {
            Map<String, DreamItem> dreamItemByName = new HashMap<>();
            for (CustomItem item : DREAM_ITEM_REGISTRY) {
                if (!(item instanceof DreamItem d)) continue;

                dreamItemByName.put(item.getId(), d);
            }
            DREAM_ITEM_BY_NAME_REGISTRY = dreamItemByName;
        }
        return DREAM_ITEM_BY_NAME_REGISTRY;
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
        if (stack == null) return null;
        CustomItem ci = CustomItemRegistry.getByItemStack(stack);

        if (ci == null) return null;
        if (!(ci instanceof DreamItem di)) return null;

        return di;
    }
}
