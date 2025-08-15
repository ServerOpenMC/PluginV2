package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMilestonesMenu extends Menu {

    public MainMilestonesMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Milestones - Plus d'info";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        MilestonesManager.getRegisteredMilestones().forEach(milestone -> {
            if (milestone.getType().equals(MilestoneType.TUTORIAL)) {
                inventory.put(11, new ItemBuilder(this, milestone.getIcon(), itemMeta -> {
                    itemMeta.displayName(Component.text(milestone.getName()));
                    itemMeta.lore(milestone.getDescription());
                    itemMeta.setEnchantmentGlintOverride(MilestonesManager.getPlayerStep(milestone.getType(), player) + 1 >= milestone.getSteps().size());
                }).setOnClick(event -> {
                    milestone.getMenu(player).open();
                }));
            }
        });

        inventory.put(35, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour")), true));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
