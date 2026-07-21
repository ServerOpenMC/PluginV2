package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.FixGoldenBlockListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.GoldenCropsListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.ObeseCropsListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.PlantationLootListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops.ObeseCropsRegistry;
import fr.openmc.core.hooks.itemsadder.behaviours.BehaviourUpBlock;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

//todo refaire le systeme de peeled en code java au lieu d'une config item adder qui marche jamais
public class GoldenHarvestManager extends Feature implements HasListeners {
    public static HashMap<KeyBlock, Map<Double, CustomItem>> OBESE_CROPS_MAPPING = null;
    public static final double OBESE_CROP_CHANCE = 0.05; // 10% d'avoir une crop obèse

    public static HashMap<KeyBlock, ItemLoot> GOLDEN_CROPS_ON_BREAK_MAPPING = null;
    public static HashMap<KeyBlock, KeyBlock> GOLDEN_CROPS_ON_GROW_MAPPING = null;
    public static final double GOLDEN_CROP_ON_CROP_CHANCE = 0.04; // 4% d'avoir une golden crosps sur des crops
    public static final double GOLDEN_CROP_ON_OBESE_CHANCE = 0.5; // 50% d'avoir une golden crosps sur des crops

    @Override
    public void init() {
        ObeseCropsRegistry.init();
    }

    @Override
    public void save() {
        ObeseCropsRegistry.save();
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new GoldenCropsListener(),
                new ObeseCropsListener(),
                new FixGoldenBlockListener(),
                new PlantationLootListener()
        );
    }

    /**
     * On génère un mapping pour savoir quels types de blocs peuvent se transformer en un autre block
     * qu'on genere seulement sur le moment afin d'éviter des erreurs
     * @return notre mapping
     */
    public static HashMap<KeyBlock, Map<Double, CustomItem>> getObeseCropsMapping() {
        if (OBESE_CROPS_MAPPING == null) {
            OBESE_CROPS_MAPPING = new HashMap<>(
                    Map.of(
                            KeyBlock.vanilla(BlockType.POTATOES), new TreeMap<>(Map.of(
                                    0.05, OMCRegistry.CUSTOM_ITEMS.OBESE_POISONOUS_POTATO,
                                    1d, OMCRegistry.CUSTOM_ITEMS.OBESE_POTATO)),
                            KeyBlock.vanilla(BlockType.CARROTS), Map.of(1d, OMCRegistry.CUSTOM_ITEMS.OBESE_CARROT),
                            KeyBlock.vanilla(BlockType.BEETROOTS), Map.of(1d, OMCRegistry.CUSTOM_ITEMS.OBESE_BEETROOT),
                            KeyBlock.vanilla(BlockType.NETHER_WART), Map.of(1d, OMCRegistry.CUSTOM_ITEMS.OBESE_NETHER_WART)
                    )
                    // todo: add obese onion, obese cabbage and more with ItemsAdder CropsApi
            );
        }
        return OBESE_CROPS_MAPPING;
    }

    /**
     * On génère un mapping pour savoir quels types de blocs donnent quel type de plantation dorée
     * qu'on genere seulement sur le moment afin d'éviter des erreurs
     * @return notre mapping
     */
    public static HashMap<KeyBlock, ItemLoot> getGoldenCropsOnBreakMapping() {
        if (GOLDEN_CROPS_ON_BREAK_MAPPING == null) {
            GOLDEN_CROPS_ON_BREAK_MAPPING = new HashMap<>(
                    Map.of(
                            KeyBlock.vanilla(BlockType.POTATOES),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_POTATO, 1, 1),
                            KeyBlock.vanilla(BlockType.WHEAT),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_WHEAT, 1, 1),
                            KeyBlock.vanilla(BlockType.CARROTS),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.REALLY_GOLDEN_CARROT, 1, 1),
                            KeyBlock.vanilla(BlockType.BEETROOTS),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_BEETROOT, 1, 1),

                            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_POTATO),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_POTATO, 1, 3, 6),
                            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_CARROT),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.REALLY_GOLDEN_CARROT, 3, 6),
                            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.OBESE_BEETROOT),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_BEETROOT, 1, 3, 6)
                    )
            );
        }
        return GOLDEN_CROPS_ON_BREAK_MAPPING;
    }

    /**
     * On génère un mapping pour savoir quels types de blocs donnent
     * quel type de plantation dorée lorsqu'ils ont fini de pousser
     * qu'on genere seulement sur le moment afin d'éviter des erreurs
     * @return notre mapping
     */
    public static HashMap<KeyBlock, KeyBlock> getGoldenCropsOnGrowMapping() {
        if (GOLDEN_CROPS_ON_GROW_MAPPING == null) {
            GOLDEN_CROPS_ON_GROW_MAPPING = new HashMap<>(
                    Map.of(
                            KeyBlock.vanilla(BlockType.MELON),
                            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.GOLDEN_MELON),
                            KeyBlock.vanilla(BlockType.PUMPKIN),
                            KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.GOLDEN_PUMPKIN)
                    )
            );
        }
        return GOLDEN_CROPS_ON_GROW_MAPPING;
    }

    public static void setObeseCrop(Block block) {
        BlockType type = block.getType().asBlockType();

        Map<Double, CustomItem> obeseCrops = getObeseCropsMapping().get(KeyBlock.vanilla(type));
        if (obeseCrops == null) return;

        double chance = ThreadLocalRandom.current().nextDouble();

        for (Map.Entry<Double, CustomItem> entry : obeseCrops.entrySet()) {
            if (chance <= entry.getKey()) {
                CustomBlock customBlock = entry.getValue().getCustomBlock();
                if (customBlock == null) return;
                customBlock.place(block.getLocation());
                BehaviourUpBlock.onPlace(customBlock);
                return;
            }
        }
    }
}
