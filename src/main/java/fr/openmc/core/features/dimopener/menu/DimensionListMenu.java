package fr.openmc.core.features.dimopener.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.dimopener.DimensionProgress;
import fr.openmc.core.features.dimopener.data.DimensionData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DimensionListMenu extends Menu {

    public DimensionListMenu(Player owner) {
        super(owner);
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dimopener.menu.list.title");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = fill(Material.GRAY_STAINED_GLASS_PANE);

        List<DimensionData> dims = new ArrayList<>(DimensionOpenerManager.getEnabledDimensions());
        int slot = 10;

        for (DimensionData dim : dims) {
            if (slot % 9 == 8) slot += 2;

            DimensionProgress progress = DimensionOpenerManager.getProgress(dim.getId());
            boolean unlocked = DimensionOpenerManager.isPrerequisiteMet(dim);

            ItemMenuBuilder item = new ItemMenuBuilder(this, DimensionOpenerManager.resolveIcon(dim), meta -> {
                meta.itemName(TranslationManager.translation("feature.dimopener.menu.list.name", Component.text(dim.getName())));
                List<Component> lore = new ArrayList<>();
                lore.add(TranslationManager.translation("feature.dimopener.menu.list.description", Component.text(dim.getDescription())));
                lore.add(Component.empty());

                if (unlocked) {
                    lore.add(TranslationManager.translation("feature.dimopener.menu.list.state", describeState(progress)));
                    lore.add(Component.empty());
                    lore.add(TranslationManager.translation("feature.dimopener.menu.list.click"));
                } else {
                    DimensionData required = DimensionOpenerManager.getDimension(dim.getRequireDimension());
                    String requiredName = required != null ? required.getName() : dim.getName();
                    lore.add(TranslationManager.translation("feature.dimopener.menu.locked.title"));
                    lore.add(TranslationManager.translation("feature.dimopener.menu.list.requires", Component.text(requiredName)));
                }

                meta.lore(lore);
            }).setOnClick(_ -> {
                if (unlocked) new DimensionContributeMenu(getOwner(), dim.getId()).open();
            });

            content.put(slot, item);
            slot++;
        }

        return content;
    }

    private Component describeState(DimensionProgress progress) {
        String key = switch (progress.getState()) {
            case LOCKED -> "feature.dimopener.state.locked";
            case STEP_IN_PROGRESS -> "feature.dimopener.state.in_progress";
            case STEP_COOLDOWN -> "feature.dimopener.state.cooldown";
            case ALL_STEPS_DONE -> "feature.dimopener.state.all_done";
            case OPENED -> "feature.dimopener.state.opened";
        };
        return TranslationManager.translation(key);
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}