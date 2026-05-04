package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.sub.milestone.menu.CityMilestoneMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
        
        Milestone tutoMilestone = MilestoneType.TUTORIAL.getMilestone();
        
        inventory.put(10, new ItemBuilder(this, tutoMilestone.getIcon(), itemMeta -> {
            itemMeta.displayName(Component.text(tutoMilestone.getName()).decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(tutoMilestone.getDescription());
            itemMeta.setEnchantmentGlintOverride(MilestonesManager.getPlayerStep(tutoMilestone.getType(), player) + 1 >= tutoMilestone.getSteps().size());
        }).setOnClick(inventoryClickEvent -> tutoMilestone.getMenu(player).open()));
        
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

        inventory.put(12, new ItemBuilder(this, Material.SEA_LANTERN, itemMeta -> {
            itemMeta.displayName(Component.text("§3Milestone des villes").decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(loreMilestoneVille);
        }).setOnClick(inventoryClickEvent -> {
            if (playerCity == null) {
                new NoCityMenu(player).open();
            } else {
                new CityMilestoneMenu(player, playerCity).open();
            }
        }));
        
        Milestone dreamMilestone = MilestoneType.DREAM.getMilestone();
        
        inventory.put(14, new ItemBuilder(this, dreamMilestone.getIcon(), itemMeta -> {
            itemMeta.displayName(Component.text(dreamMilestone.getName()).decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(dreamMilestone.getDescription());
            itemMeta.setEnchantmentGlintOverride(MilestonesManager.getPlayerStep(dreamMilestone.getType(), player) + 1 >= dreamMilestone.getSteps().size());
        }).setOnClick(inventoryClickEvent -> dreamMilestone.getMenu(player).open()));

        inventory.put(16, new ItemBuilder(this, Material.BARREL, itemMeta -> itemMeta.displayName(Component.text(" §ks §cComming soon §ke"))));

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
