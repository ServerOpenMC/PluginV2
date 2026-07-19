package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.CropBreakListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.CropChangeStageListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.listeners.ObeseCropListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops.ObeseCropsRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

// todo: tester mécanique obese crops et golden crop
// todo: enchantment plantation
// todo: ajouter un sfx pour le golden crop
// todo: impl abondance armor
//  +5% chance de doubler les drops (par piece)
//	+1.5% de chance d'avoir les crops dorée (par piece)

public class GoldenHarvestManager extends Feature implements HasListeners {
    public static HashMap<KeyBlock, Map<Double, CustomItem>> OBESE_CROPS_MAPPING = null;
    public static final double OBESE_CROP_CHANCE = 0.06; // 6% d'avoir une crop obèse

    public static HashMap<KeyBlock, ItemLoot> GOLDEN_CROPS_MAPPING = null;
    public static final double GOLDEN_CROP_ON_CROP_CHANCE = 0.04; // 6% d'avoir une golden crosps sur des crops
    public static final double GOLDEN_CROP_ON_OBESE_CHANCE = 0.5; // 6% d'avoir une golden crosps sur des crops

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
                new CropBreakListener(),
                new CropChangeStageListener(),
                new ObeseCropListener()
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
                            KeyBlock.vanilla(BlockType.POTATOES), Map.of(
                                    0.05, OMCRegistry.CUSTOM_ITEMS.OBESE_POISONOUS_POTATO,
                                    0.95, OMCRegistry.CUSTOM_ITEMS.OBESE_POTATO),
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
    public static HashMap<KeyBlock, ItemLoot> getGoldenCropsMapping() {
        if (GOLDEN_CROPS_MAPPING == null) {
            GOLDEN_CROPS_MAPPING = new HashMap<>(
                    Map.of(
                            KeyBlock.vanilla(BlockType.POTATOES),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_POTATO, 1, 1),
                            KeyBlock.vanilla(BlockType.WHEAT),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_WHEAT, 1, 1),
                            KeyBlock.vanilla(BlockType.MELON),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_MELON, 1, 1),
                            KeyBlock.vanilla(BlockType.PUMPKIN),
                            new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GOLDEN_PUMPKIN, 1, 1),
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
        return GOLDEN_CROPS_MAPPING;
    }

    public static void setObeseCrop(Block block) {
        BlockType type = block.getType().asBlockType();

        Map<Double, CustomItem> obeseCrops = getObeseCropsMapping().get(type);
        if (obeseCrops == null) return;

        double chance = ThreadLocalRandom.current().nextDouble();

        obeseCrops.forEach((chanceValue, item) -> {
            if (chance <= chanceValue) {
                CustomBlock customBlock = item.getCustomBlock();
                if (customBlock == null) return;
                customBlock.place(block.getLocation());

                ObeseCropsRegistry.mark(block.getLocation());
            }
        });
    }
}
