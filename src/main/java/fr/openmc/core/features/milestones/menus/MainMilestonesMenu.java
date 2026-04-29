package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.sub.milestone.menu.CityMilestoneMenu;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMilestonesMenu extends Menu {

    public MainMilestonesMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des milestones - Plus d'info";
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
                inventory.put(10, new ItemBuilder(this, milestone.getIcon(), itemMeta -> {
                    itemMeta.displayName(Component.text(milestone.getName()));
                    itemMeta.lore(milestone.getDescription());
                    itemMeta.setEnchantmentGlintOverride(MilestonesManager.getPlayerStep(milestone.getType(), player) + 1 >= milestone.getSteps().size());
                }).setOnClick(event -> {
                    milestone.getMenu(player).open();
                }));
            }
        });

        List<Component> loreMilestoneVille = new ArrayList<>();
        
        loreMilestoneVille.add(Component.text("§7Découvrez l'intégralité §3des villes"));
        loreMilestoneVille.add(Component.text("§7Via cette §3route de progression §7!"));
        loreMilestoneVille.add(Component.empty());
        loreMilestoneVille.add(Component.text("§8§oLes claims, Les mascottes, Les maires, Les guerres, ..."));

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            loreMilestoneVille.add(Component.empty());
            loreMilestoneVille.add(Component.text("§cCréez ou rejoignez une ville pour accéder à cela !"));
        } else {
            loreMilestoneVille.add(Component.empty());
            loreMilestoneVille.add(Component.text("§7Level de votre ville : §3" + playerCity.getLevel()));
            loreMilestoneVille.add(Component.empty());
            loreMilestoneVille.add(Component.text("§e§lCLIQUEZ ICI POUR ACCEDER A VOTRE MILESTONE"));
        }
	    
	    inventory.put(12, new ItemBuilder(this, Material.SCULK, itemMeta -> {
		    itemMeta.displayName(Component.text(" §kd §r§cComming soon §kr"));
	    }));
		
        inventory.put(14, new ItemBuilder(this, Material.SEA_LANTERN, itemMeta -> {
            itemMeta.displayName(Component.text("§3Milestone des villes"));
            itemMeta.lore(loreMilestoneVille);
        }).setOnClick(event -> {
            if (playerCity == null) {
                new NoCityMenu(player).open();
            } else {
                new CityMilestoneMenu(player, playerCity).open();
            }
        }));
	    
	    inventory.put(16, new ItemBuilder(this, Material.DEAD_BUBBLE_CORAL_BLOCK, itemMeta -> {
		    itemMeta.displayName(Component.text(" §ks §cComming soon §ke"));
	    }));

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
