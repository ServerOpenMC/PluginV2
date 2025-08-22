package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.milestones.Milestone;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import fr.openmc.core.features.quests.objects.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TutorialMilestone implements Milestone {
    @Override
    public String getName() {
        return "§7Tutoriel d'OpenMC";
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.text("§7Découvrez §dOpenMC §7!"),
                Component.text("§7Passez en revue les §dFeatures"),
                Component.text("§8§oLes Villes, les Contests, l'Admin Shop, les Quêtes, ..."),
                Component.text("§7Idéal pour se lancer dans l'aventure !")
        );
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GRASS_BLOCK);
    }

    @Override
    public List<Quest> getSteps() {
        return Arrays.stream(TutorialStep.values()).map(TutorialStep::getQuest).toList();
    }

    @Override
    public MilestoneType getType() {
        return MilestoneType.TUTORIAL;
    }

    @Override
    public Menu getMenu(Player player) {
        return new MilestoneMenu(player, this);
    }
}
