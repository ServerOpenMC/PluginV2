package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.sub.milestone.menu.CityMilestoneMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.milestones.menu.title.main");
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
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();
        Player player = getOwner();
        
        Milestone tutoMilestone = MilestoneType.TUTORIAL.getMilestone();
        
        inventory.put(10, new ItemMenuBuilder(this, tutoMilestone.getIcon(), itemMeta -> {
            itemMeta.displayName(Component.text(tutoMilestone.getName()).decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(tutoMilestone.getDescription());
            itemMeta.setEnchantmentGlintOverride(MilestonesManager.getPlayerStep(tutoMilestone.getType(), player) + 1 >= tutoMilestone.getSteps().size());
        }).setOnClick(inventoryClickEvent -> tutoMilestone.getMenu(player).open()));
        
	    List<Component> loreMilestoneVille = new ArrayList<>();
        
        loreMilestoneVille.add(TranslationManager.translation("feature.milestones.menu.city.lore.intro_1"));
        loreMilestoneVille.add(TranslationManager.translation("feature.milestones.menu.city.lore.intro_2"));
        loreMilestoneVille.add(Component.empty());
        loreMilestoneVille.add(TranslationManager.translation("feature.milestones.menu.city.lore.details"));

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            loreMilestoneVille.add(Component.empty());
            loreMilestoneVille.add(TranslationManager.translation("feature.milestones.menu.city.lore.need_city"));
        } else {
            loreMilestoneVille.add(Component.empty());
            loreMilestoneVille.add(TranslationManager.translation(
                    "feature.milestones.menu.city.lore.level",
                    Component.text(playerCity.getLevel()).color(NamedTextColor.DARK_AQUA)
            ).color(NamedTextColor.GRAY));
            loreMilestoneVille.add(Component.empty());
            loreMilestoneVille.add(TranslationManager.translation("feature.milestones.menu.city.lore.click"));
        }

        inventory.put(12, new ItemMenuBuilder(this, Material.SEA_LANTERN, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.milestones.menu.city.name")
                    .decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(loreMilestoneVille);
        }).setOnClick(inventoryClickEvent -> {
            if (playerCity == null) {
                new NoCityMenu(player).open();
            } else {
                new CityMilestoneMenu(player, playerCity).open();
            }
        }));
        
        Milestone dreamMilestone = MilestoneType.DREAM.getMilestone();
        
        inventory.put(14, new ItemMenuBuilder(this, dreamMilestone.getIcon(), itemMeta -> {
            itemMeta.displayName(Component.text(dreamMilestone.getName()).decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(dreamMilestone.getDescription());
            itemMeta.setEnchantmentGlintOverride(MilestonesManager.getPlayerStep(dreamMilestone.getType(), player) + 1 >= dreamMilestone.getSteps().size());
        }).setOnClick(inventoryClickEvent -> dreamMilestone.getMenu(player).open()));

        inventory.put(16, new ItemMenuBuilder(this, Material.BARREL, itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.milestones.menu.coming_soon"))));

        inventory.put(35, new ItemMenuBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.milestones.menu.back")), true));

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
