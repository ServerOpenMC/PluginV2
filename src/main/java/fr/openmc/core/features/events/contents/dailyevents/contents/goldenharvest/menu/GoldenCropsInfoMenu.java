package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
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

public class GoldenCropsInfoMenu extends PaginatedMenu {

    public GoldenCropsInfoMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.dailyevents.golden_harvest.menu.info.golden_crops.name"
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

        for (GoldenCropInfo goldenCropInfo : generateGoldenCropInfos()) {
            items.add(new ItemMenuBuilder(
                    this,
                    goldenCropInfo.goldenItem(),
                    itemMeta -> {
                        itemMeta.displayName(goldenCropInfo.goldenItem().getBest().getItemMeta().displayName());

                        List<Component> lore = new ArrayList<>();

                        if (!goldenCropInfo.breakSources().isEmpty()) {
                            lore.add(TranslationManager.translation(
                                    "feature.dailyevents.golden_harvest.menu.info.golden_crops.lore.breaking"
                            ));

                            for (KeyBlock source : goldenCropInfo.breakSources()) {
                                lore.add(
                                        TranslationManager.translation(
                                                "feature.dailyevents.golden_harvest.menu.info.golden_crops.lore.source",
                                                getKeyBlockName(source)
                                        )
                                );
                            }
                        }

                        if (!goldenCropInfo.breakSources().isEmpty()
                                && !goldenCropInfo.growSources().isEmpty()) {
                            lore.add(Component.empty());
                        }

                        if (!goldenCropInfo.growSources().isEmpty()) {
                            lore.add(TranslationManager.translation(
                                    "feature.dailyevents.golden_harvest.menu.info.golden_crops.lore.growing"
                            ));

                            for (KeyBlock source : goldenCropInfo.growSources()) {
                                lore.add(
                                        TranslationManager.translation(
                                                "feature.dailyevents.golden_harvest.menu.info.golden_crops.lore.source",
                                                getKeyBlockName(source)
                                        )
                                );
                            }
                        }

                        itemMeta.lore(lore);
                        itemMeta.setEnchantmentGlintOverride(isActive);
                    }
            ).hide(ItemUtils.getDataComponentType()));
        }

        return items;
    }

    @Override
    public int getSizeOfItems() {
        return generateGoldenCropInfos().size();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> buttons = new HashMap<>();

        buttons.put(30, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        buttons.put(27, new ItemMenuBuilder(this, Material.ARROW, true));
        buttons.put(32, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));

        return buttons;
    }

    private List<GoldenCropInfo> generateGoldenCropInfos() {
        Map<CustomItem, GoldenCropInfoBuilder> goldenCrops = new LinkedHashMap<>();

        GoldenHarvestManager.getGoldenCropsOnBreakMapping().forEach((source, loot) -> {
            CustomItem goldenItem = OMCRegistry.CUSTOM_ITEMS.get(loot.getRepresentativeItem()).orElse(null);

            if (goldenItem == null) return;

            goldenCrops.computeIfAbsent(
                    goldenItem,
                    GoldenCropInfoBuilder::new
            ).addBreakSource(source);
        });

        GoldenHarvestManager.getGoldenCropsOnGrowMapping().forEach((source, result) -> {
            CustomItem goldenItem = result.getCustomItem();

            if (goldenItem == null) return;

            goldenCrops.computeIfAbsent(
                    goldenItem,
                    GoldenCropInfoBuilder::new
            ).addGrowSource(source);
        });

        return goldenCrops.values()
                .stream()
                .map(GoldenCropInfoBuilder::build)
                .toList();
    }

    private Component getKeyBlockName(KeyBlock keyBlock) {
        if (keyBlock.isCustom()) {
            CustomItem customItem = keyBlock.getCustomItem();

            if (customItem != null) {
                return customItem.getBest().getItemMeta().displayName();
            }
        }

        BlockType blockType = keyBlock.getBlockType();

        if (blockType != null) {
            return Component.translatable(blockType.translationKey())
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false);
        }

        return TranslationManager.translation(
                "feature.dailyevents.golden_harvest.menu.info.golden_crops.unknown_source"
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

    private record GoldenCropInfo(CustomItem goldenItem, List<KeyBlock> breakSources, List<KeyBlock> growSources) {
    }

    private static final class GoldenCropInfoBuilder {

        private final CustomItem goldenItem;
        private final List<KeyBlock> breakSources = new ArrayList<>();
        private final List<KeyBlock> growSources = new ArrayList<>();

        private GoldenCropInfoBuilder(CustomItem goldenItem) {
            this.goldenItem = goldenItem;
        }

        private void addBreakSource(KeyBlock source) {
            breakSources.add(source);
        }

        private void addGrowSource(KeyBlock source) {
            growSources.add(source);
        }

        private GoldenCropInfo build() {
            return new GoldenCropInfo(
                    goldenItem,
                    List.copyOf(breakSources),
                    List.copyOf(growSources)
            );
        }
    }
}