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
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DreamItemRegistry extends SubRegistry<String, DreamItem> {
    public final DreamItem DOMINATION_ORB = register(new DominationOrb());
    public final DreamItem SOUL_ORB = register(new SoulOrb());
    public final DreamItem MUD_ORB = register(new MudOrb());
    public final DreamItem CLOUD_ORB = register(new CloudOrb());
    public final DreamItem GLACITE_ORB = register(new GlaciteOrb());
    public final DreamItem SINGULARITY = register(new Singularity());

    public final DreamItem CORRUPTED_STRING = register(new CorruptedString());
    public final DreamItem CREAKING_HEART = register(new CreakingHeart());
    public final DreamItem SOUL = register(new Soul());
    public final DreamItem CLOUD_KEY = register(new CloudKey());

    public final DreamItem CORRUPTED_SCULK = register(new CorruptedSculk());
    public final DreamItem OLD_PALE_OAK_WOOD = register(new OldPaleOakWood());
    public final DreamItem GLACITE = register(new Glacite());
    public final DreamItem BURN_COAL = register(new BurnCoal());
    public final DreamItem HARD_STONE = register(new HardStone());
    public final DreamItem CRAFTING_TABLE = register(new CraftingTable());
    public final DreamItem ETERNAL_CAMPFIRE = register(new EternalCampFire());
    public final DreamItem EWENITE = register(new Ewenite());
    public final DreamItem EWENITE_BLOCK = register(new EweniteBlock());

    public final DreamItem SOMNIFERE = register(new Somnifere());
    public final DreamItem CHIPS_AYWEN = register(new ChipsAywen());
    public final DreamItem CHIPS_DIHYDROGENE = register(new ChipsDihydrogene());
    public final DreamItem CHIPS_JIMMY = register(new ChipsJimmy());
    public final DreamItem CHIPS_LAIT_2_MARGOUTA = register(new ChipsLait2Margouta());
    public final DreamItem CHIPS_NATURE = register(new ChipsNature());
    public final DreamItem CHIPS_SANS_PLOMB = register(new ChipsSansPlomb());
    public final DreamItem CHIPS_TERRE = register(new ChipsTerre());
    public final DreamItem COCKED_POISSONION = register(new CokkedPoissonion());
    public final DreamItem POISSONION = register(new Poissonion());
    public final DreamItem MOON_FISH = register(new MoonFish());
    public final DreamItem SUN_FISH = register(new SunFish());
    public final DreamItem DOCKER_FISH = register(new DockerFish());

    public final DreamItem OLD_CREAKING_HELMET = register(new OldCreakingHelmet());
    public final DreamItem OLD_CREAKING_CHESTPLATE = register(new OldCreakingChestplate());
    public final DreamItem OLD_CREAKING_LEGGINGS = register(new OldCreakingLeggings());
    public final DreamItem OLD_CREAKING_BOOTS = register(new OldCreakingBoots());

    public final DreamItem SOUL_HELMET = register(new SoulHelmet());
    public final DreamItem SOUL_CHESTPLATE = register(new SoulChestplate());
    public final DreamItem SOUL_LEGGINGS = register(new SoulLeggings());
    public final DreamItem SOUL_BOOTS = register(new SoulBoots());

    public final DreamItem CLOUD_HELMET = register(new CloudHelmet());
    public final DreamItem CLOUD_CHESTPLATE = register(new CloudChestplate());
    public final DreamItem CLOUD_LEGGINGS = register(new CloudLeggings());
    public final DreamItem CLOUD_BOOTS = register(new CloudBoots());

    public final DreamItem COLD_HELMET = register(new ColdHelmet());
    public final DreamItem COLD_CHESTPLATE = register(new ColdChestplate());
    public final DreamItem COLD_LEGGINGS = register(new ColdLeggings());
    public final DreamItem COLD_BOOTS = register(new ColdBoots());

    public final DreamItem DREAM_HELMET = register(new DreamHelmet());
    public final DreamItem DREAM_CHESTPLATE = register(new DreamChestplate());
    public final DreamItem DREAM_LEGGINGS = register(new DreamLeggings());
    public final DreamItem DREAM_BOOTS = register(new DreamBoots());

    public final DreamItem PYJAMA_HELMET = register(new PyjamaHelmet());
    public final DreamItem PYJAMA_CHESTPLATE = register(new PyjamaChestplate());
    public final DreamItem PYJAMA_LEGGINGS = register(new PyjamaLeggings());
    public final DreamItem PYJAMA_BOOTS = register(new PyjamaBoots());

    public final DreamItem OLD_CREAKING_AXE = register(new OldCreakingAxe());
    public final DreamItem SOUL_AXE = register(new SoulAxe());
    public final DreamItem CLOUD_FISHING_ROD = register(new CloudFishingRod());
    public final DreamItem METEO_WAND = register(new MeteoWand());
    public final DreamItem METAL_DETECTOR = register(new MetalDetector(OMCRegistry.FEATURES.DREAM.get().METAL_DETECTOR));
    public final DreamItem CRYSTALIZED_PICKAXE = register(new CrystalizedPickaxe());
    public final DreamItem MECHANIC_PICKAXE = register(new MecanicPickaxe());

    @Override
    public CustomItemRegistry getParentRegistry() {
        return OMCRegistry.CUSTOM_ITEMS;
    }

    private HashMap<String, DreamItem> DREAM_ITEM_BY_NAME_REGISTRY = null;

    @Override
    public void postInit() {
        CommandsManager.getHandler().register(
                new DreamItemCommand()
        );

        OMCPlugin.registerEvents(
                DreamItemConvertorListener::new,
                DreamItemDropsListener::new
        );
    }

    public Map<String, DreamItem> getBootstrapRegistry() {
        if (DREAM_ITEM_BY_NAME_REGISTRY == null) {
            DREAM_ITEM_BY_NAME_REGISTRY = new HashMap<>();

            for (DreamItem item : getRegistry()) {
                DREAM_ITEM_BY_NAME_REGISTRY.put(item.getId(), item);
            }
        }

        return DREAM_ITEM_BY_NAME_REGISTRY;
    }

    @Nullable
    public DreamItem getByName(String name) {
        if (!name.startsWith("omc_dream:")) name = "omc_dream:" + name;

        Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(name);
        if (ci.isEmpty()) return null;
        if (!(ci.get() instanceof DreamItem di)) return null;

        return di;
    }

    @Nullable
    public DreamItem getByItemStack(ItemStack stack) {
        if (stack == null) return null;
        Optional<CustomItem> ci = OMCRegistry.CUSTOM_ITEMS.get(stack);

        if (ci.isEmpty()) return null;
        if (!(ci.get() instanceof DreamItem di)) return null;

        return di;
    }
}
