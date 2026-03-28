package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.milestones.Milestone;
import fr.openmc.core.features.milestones.MilestoneModel;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TutorialMilestone implements Milestone {
	
	private static HashMap<UUID, MilestoneModel> playerData = new HashMap<>();
	
	@Override
	public HashMap<UUID, MilestoneModel> getPlayerData() {
		return playerData;
	}
	
	@Override
    public String getName() {
        return "§7Tutoriel d'OpenMC";
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.text("§7Découvrez §dOpenMC §7!"),
                Component.text("§7Passez en revue les §dfeatures"),
                Component.text("§8§oLes villes, les contests, l'adminshop, les quêtes, ..."),
                Component.text("§7Idéal pour se lancer dans l'aventure !")
        );
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GRASS_BLOCK);
    }

    @Override
    public List<MilestoneQuest> getSteps() {
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
