package fr.openmc.core.features.dream.registries;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
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
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class DreamItemRegistry {
    public static final DreamItem DOMINATION_ORB = create(new DominationOrb());
    public static final DreamItem SOUL_ORB = create(new SoulOrb());
    public static final DreamItem MUD_ORB = create(new MudOrb());
    public static final DreamItem CLOUD_ORB = create(new CloudOrb());
    public static final DreamItem GLACITE_ORB = create(new GlaciteOrb());
    public static final DreamItem SINGULARITY = create(new Singularity());

    public static final DreamItem CORRUPTED_STRING = create(new CorruptedString());
    public static final DreamItem CREAKING_HEART = create(new CreakingHeart());
    public static final DreamItem SOUL = create(new Soul());
    public static final DreamItem CLOUD_KEY = create(new CloudKey());

    public static final DreamItem CORRUPTED_SCULK = create(new CorruptedSculk());
    public static final DreamItem OLD_PALE_OAK_WOOD = create(new OldPaleOakWood());
    public static final DreamItem GLACITE = create(new Glacite());
    public static final DreamItem BURN_COAL = create(new BurnCoal());
    public static final DreamItem HARD_STONE = create(new HardStone());
    public static final DreamItem CRAFTING_TABLE = create(new CraftingTable());
    public static final DreamItem ETERNAL_CAMPFIRE = create(new EternalCampFire());
    public static final DreamItem EWENITE = create(new Ewenite());

    public static final DreamItem SOMNIFERE = create(new Somnifere());
    public static final DreamItem CHIPS_AYWEN = create(new ChipsAywen());
    public static final DreamItem CHIPS_DIHYDROGENE = create(new ChipsDihydrogene());
    public static final DreamItem CHIPS_JIMMY = create(new ChipsJimmy());
    public static final DreamItem CHIPS_LAIT_2_MARGOUTA = create(new ChipsLait2Margouta());
    public static final DreamItem CHIPS_NATURE = create(new ChipsNature());
    public static final DreamItem CHIPS_SANS_PLOMB = create(new ChipsSansPlomb());
    public static final DreamItem CHIPS_TERRE = create(new ChipsTerre());
    public static final DreamItem COCKED_POISSONION = create(new CokkedPoissonion());
    public static final DreamItem POISSONION = create(new Poissonion());
    public static final DreamItem MOON_FISH = create(new MoonFish());
    public static final DreamItem SUN_FISH = create(new SunFish());
    public static final DreamItem DOCKER_FISH = create(new DockerFish());

    public static final DreamItem OLD_CREAKING_HELMET = create(new OldCreakingHelmet());
    public static final DreamItem OLD_CREAKING_CHESTPLATE = create(new OldCreakingChestplate());
    public static final DreamItem OLD_CREAKING_LEGGINGS = create(new OldCreakingLeggings());
    public static final DreamItem OLD_CREAKING_BOOTS = create(new OldCreakingBoots());

    public static final DreamItem SOUL_HELMET = create(new SoulHelmet());
    public static final DreamItem SOUL_CHESTPLATE = create(new SoulChestplate());
    public static final DreamItem SOUL_LEGGINGS = create(new SoulLeggings());
    public static final DreamItem SOUL_BOOTS = create(new SoulBoots());

    public static final DreamItem CLOUD_HELMET = create(new CloudHelmet());
    public static final DreamItem CLOUD_CHESTPLATE = create(new CloudChestplate());
    public static final DreamItem CLOUD_LEGGINGS = create(new CloudLeggings());
    public static final DreamItem CLOUD_BOOTS = create(new CloudBoots());

    public static final DreamItem COLD_HELMET = create(new ColdHelmet());
    public static final DreamItem COLD_CHESTPLATE = create(new ColdChestplate());
    public static final DreamItem COLD_LEGGINGS = create(new ColdLeggings());
    public static final DreamItem COLD_BOOTS = create(new ColdBoots());

    public static final DreamItem DREAM_HELMET = create(new DreamHelmet());
    public static final DreamItem DREAM_CHESTPLATE = create(new DreamChestplate());
    public static final DreamItem DREAM_LEGGINGS = create(new DreamLeggings());
    public static final DreamItem DREAM_BOOTS = create(new DreamBoots());

    public static final DreamItem PYJAMA_HELMET = create(new PyjamaHelmet());
    public static final DreamItem PYJAMA_CHESTPLATE = create(new PyjamaChestplate());
    public static final DreamItem PYJAMA_LEGGINGS = create(new PyjamaLeggings());
    public static final DreamItem PYJAMA_BOOTS = create(new PyjamaBoots());

    public static final DreamItem OLD_CREAKING_AXE = create(new OldCreakingAxe());
    public static final DreamItem SOUL_AXE = create(new SoulAxe());
    public static final DreamItem CLOUD_FISHING_ROD = create(new CloudFishingRod());
    public static final DreamItem METEO_WAND = create(new MeteoWand());
    public static final DreamItem METAL_DETECTOR = create(new MetalDetector());
    public static final DreamItem CRYSTALIZED_PICKAXE = create(new CrystalizedPickaxe());
    public static final DreamItem MECHANIC_PICKAXE = create(new MecanicPickaxe());

    // * Registre des DreamItems accessible durant le bootstrap
    public static Set<CustomItem> DREAM_ITEM_REGISTRY;

    // * Registre des DreamItems accesible via leur id, tres utilisé durant le bootstrap
    private static Map<String, DreamItem> DREAM_ITEM_BY_NAME_REGISTRY;

    private static DreamItem create(DreamItem item) {
        if (DREAM_ITEM_REGISTRY == null)
            DREAM_ITEM_REGISTRY = new HashSet<>();

        DREAM_ITEM_REGISTRY.add(item);
        return item;
    }
    /**
     * Charge la classe durant le runtime
     */
    public static void init() {
        OMCRegistry.CUSTOM_ITEMS.register(DREAM_ITEM_REGISTRY);

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

        Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(name);
        if (ci.isEmpty()) return null;
        if (!(ci.get() instanceof DreamItem di)) return null;

        return di;
    }

    @Nullable
    public static DreamItem getByItemStack(ItemStack stack) {
        if (stack == null) return null;
        Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(stack);

        if (ci.isEmpty()) return null;
        if (!(ci.get() instanceof DreamItem di)) return null;

        return di;
    }
}
