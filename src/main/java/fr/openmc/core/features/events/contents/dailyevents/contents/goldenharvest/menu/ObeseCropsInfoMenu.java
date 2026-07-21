package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class ObeseCropsInfoMenu extends PaginatedMenu {

    private final List<ObeseCropInfo> obeseCropInfos;

    public ObeseCropsInfoMenu(Player owner) {
        super(owner);
        this.obeseCropInfos = generateObeseCropInfos();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.dailyevents.golden_harvest.menu.info.obese_crops.name"
        );
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        boolean isActive = DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof GoldenHarvestEvent;

        for (ObeseCropInfo obeseCropInfo : obeseCropInfos) {
            items.add(new ItemMenuBuilder(
                    this,
                    obeseCropInfo.obeseItem(),
                    itemMeta -> {
                        itemMeta.displayName(obeseCropInfo.obeseItem().getBest().getItemMeta().displayName());

                        List<Component> lore = new ArrayList<>();

                        lore.add(TranslationManager.translation(
                                "feature.dailyevents.golden_harvest.menu.info.obese_crops.lore.growing"
                        ));

                        lore.add(Component.empty());

                        lore.add(TranslationManager.translation(
                                "feature.dailyevents.golden_harvest.menu.info.obese_crops.lore.obtainable_on"
                        ));

                        for (ObeseCropSource source : obeseCropInfo.sources()) {
                            lore.add(TranslationManager.translation(
                                    "feature.dailyevents.golden_harvest.menu.info.obese_crops.lore.source",
                                    getKeyBlockName(source.source())
                            ));
                        }

                        lore.add(Component.empty());

                        lore.add(TranslationManager.translation(
                                "feature.dailyevents.golden_harvest.menu.info.obese_crops.lore.chance",
                                Component.text(String.format("%.2f", obeseCropInfo.effectiveChance() * 100) + "%", NamedTextColor.AQUA)
                        ));

                        itemMeta.lore(lore);
                        itemMeta.setEnchantmentGlintOverride(isActive);
                    }
            ).hide(ItemUtils.getDataComponentType()));
        }

        return items;
    }

    @Override
    public int getSizeOfItems() {
        return obeseCropInfos.size();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> buttons = new HashMap<>();

        buttons.put(30, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        buttons.put(27, new ItemMenuBuilder(this, Material.ARROW, true));
        buttons.put(32, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));

        return buttons;
    }

    private List<ObeseCropInfo> generateObeseCropInfos() {
        Map<CustomItem, ObeseCropInfoBuilder> obeseCrops = new LinkedHashMap<>();

        GoldenHarvestManager.getObeseCropsMapping().forEach((source, results) -> {
            List<Map.Entry<Double, CustomItem>> sortedResults = results.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .toList();

            double previousThreshold = 0D;

            for (Map.Entry<Double, CustomItem> result : sortedResults) {
                double threshold = result.getKey();
                CustomItem obeseItem = result.getValue();

                double selectionChance = Math.max(
                        0D,
                        threshold - previousThreshold
                );

                double effectiveChance =
                        GoldenHarvestManager.OBESE_CROP_CHANCE * selectionChance;

                obeseCrops.computeIfAbsent(
                        obeseItem,
                        ObeseCropInfoBuilder::new
                ).addSource(source, selectionChance, effectiveChance);

                previousThreshold = threshold;
            }
        });

        return obeseCrops.values()
                .stream()
                .map(ObeseCropInfoBuilder::build)
                .toList();
    }

    private Component getKeyBlockName(KeyBlock keyBlock) {
        if (keyBlock.isCustom()) {
            CustomItem customItem = keyBlock.getCustomItem();

            if (customItem != null) return customItem.getBest().getItemMeta().displayName();

        }

        BlockType blockType = keyBlock.getBlockType();

        if (blockType != null)
            return Component.translatable(blockType.translationKey())
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false);


        return TranslationManager.translation(
                "feature.dailyevents.golden_harvest.menu.info.obese_crops.unknown_source"
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private record ObeseCropInfo(CustomItem obeseItem, List<ObeseCropSource> sources, double effectiveChance) {
    }

    private record ObeseCropSource(KeyBlock source, double selectionChance, double effectiveChance) {
    }

    private static final class ObeseCropInfoBuilder {
        private final CustomItem obeseItem;
        private final List<ObeseCropSource> sources = new ArrayList<>();

        private ObeseCropInfoBuilder(CustomItem obeseItem) {
            this.obeseItem = obeseItem;
        }

        private void addSource(KeyBlock source, double selectionChance, double effectiveChance) {
            sources.add(new ObeseCropSource(
                    source,
                    selectionChance,
                    effectiveChance
            ));
        }

        private ObeseCropInfo build() {
            double effectiveChance = sources.stream()
                    .mapToDouble(ObeseCropSource::effectiveChance)
                    .max()
                    .orElse(0D);

            return new ObeseCropInfo(
                    obeseItem,
                    List.copyOf(sources),
                    effectiveChance
            );
        }
    }
}
