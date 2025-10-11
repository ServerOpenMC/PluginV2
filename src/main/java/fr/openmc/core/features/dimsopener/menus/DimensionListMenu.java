package fr.openmc.core.features.dimsopener.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.dimsopener.DimensionStep;
import fr.openmc.core.features.dimsopener.DimsOpenerManager;
import fr.openmc.core.features.dimsopener.DimensionConfig;
import fr.openmc.core.features.dimsopener.model.DimensionProgress;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionListMenu extends PaginatedMenu {

    public DimensionListMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public @NotNull String getName() {
        return "§6§lDimensions" + (getNumberOfPages() > 1 ? " §7- Page " + (getPage() + 1) : "");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public int getSizeOfItems() {
        return 6;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<String, DimensionConfig> entry : DimsOpenerManager.getAllConfigs().entrySet()) {
            String dimKey = entry.getKey();
            DimensionConfig config = entry.getValue();
            DimensionProgress progress = DimsOpenerManager.getProgress(dimKey);

            if (progress == null) continue;

            List<Component> lore = new ArrayList<>();

            if (progress.isUnlocked()) {
                lore.add(Component.text("§a§lDÉVERROUILLÉE"));
                lore.add(Component.text(""));
                lore.add(Component.text("§7Cette dimension est accessible !"));
            } else {
                int currentStepIndex = progress.getCurrentStep();
                if (config.steps().isEmpty()) continue;
                DimensionStep currentStep = config.steps().get(currentStepIndex);

                lore.add(Component.text("§eÉtape §6" + (currentStepIndex + 1) + "§e/§6" + config.steps().size()));
                lore.add(Component.text(""));
                lore.add(Component.text("§7Item requis: §f" + getItemName(currentStep.itemType())));
                lore.add(Component.text("§7Progrès: §6" + progress.getItemsCollected() + "§7/§6" + currentStep.count()));
                lore.add(Component.text(""));

                if (!progress.canProgressToNextStep()) {
                    String timeRemaining = DimsOpenerManager.getFormattedTimeRemaining(dimKey);
                    lore.add(Component.text("§cProchaine contribution: §e" + timeRemaining));
                } else {
                    lore.add(Component.text("§a✔ Vous pouvez contribuer !"));
                }

                lore.add(Component.text(""));
                lore.add(Component.text("§e§lCLIQUEZ POUR CONTRIBUER"));
            }

            ItemBuilder item = new ItemBuilder(this, config.icon(), meta -> {
                meta.displayName(Component.text(config.name()));
                meta.lore(lore);
            }).setItemId("dimension_" + dimKey);

            items.add(item);
        }

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();
        System.out.println("Page: " + this.getPage() + " / " + this.getNumberOfPages());
        if (!isLastPage()) buttons.put(18, MenuUtils.getNavigationButtons(this).get(0).setPreviousPageButton());
        buttons.put(22, MenuUtils.getNavigationButtons(this).get(1).setCloseButton());
        if (this.getPage() < 1) buttons.put(26, MenuUtils.getNavigationButtons(this).get(2).setNextPageButton());
        return buttons;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public int getNumberOfPages() {
        int itemCount = DimsOpenerManager.getAllConfigs().size();
        int itemsPerPage = getInventorySize().getSize() - 9;
        return (int) Math.ceil((double) itemCount / itemsPerPage);
    }

    private String getItemName(String itemType) {
        String name = itemType.replace("minecraft:", "").replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}