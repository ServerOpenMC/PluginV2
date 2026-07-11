package fr.openmc.core.features.dimopener.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.dimopener.DimensionProgress;
import fr.openmc.core.features.dimopener.data.DimensionData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        return Component.text("Dimensions").color(NamedTextColor.DARK_PURPLE);
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

            Material icon = switch (progress.getState()) {
                case OPENED -> Material.END_PORTAL_FRAME;
                case ALL_STEPS_DONE, STEP_COOLDOWN -> Material.CLOCK;
                default -> Material.ENDER_PEARL;
            };

            ItemMenuBuilder item = new ItemMenuBuilder(this, icon, meta -> {
                meta.itemName(Component.text(dim.getName()).color(NamedTextColor.LIGHT_PURPLE));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(dim.getDescription()).color(NamedTextColor.GRAY));
                lore.add(Component.empty());
                lore.add(Component.text("Etat : " + describeState(progress)).color(NamedTextColor.YELLOW));
                lore.add(Component.empty());
                lore.add(Component.text("Cliquez pour contribuer").color(NamedTextColor.GREEN));
                meta.lore(lore);
            });

            item.setOnClick(_ -> new DimensionContributeMenu(getOwner(), dim.getId()).open());

            content.put(slot, item);
            slot++;
        }

        return content;
    }

    private String describeState(DimensionProgress progress) {
        return switch (progress.getState()) {
            case LOCKED -> "Non commencee";
            case STEP_IN_PROGRESS -> "Etape en cours";
            case STEP_COOLDOWN -> "En attente de la prochaine etape";
            case ALL_STEPS_DONE -> "Ouverture imminente";
            case OPENED -> "Ouverte";
        };
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