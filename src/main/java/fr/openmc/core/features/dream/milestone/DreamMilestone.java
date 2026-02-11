package fr.openmc.core.features.dream.milestone;

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

public class DreamMilestone implements Milestone {
	
	private static HashMap<UUID, MilestoneModel> playerData = new HashMap<>();
	
	@Override
	public HashMap<UUID, MilestoneModel> getPlayerData() {
		return playerData;
	}
	
	@Override
	public String getName() {
		return "Tutoriel de la Dream Dim";
	}
	
	@Override
	public List<Component> getDescription() {
		return List.of(
				Component.text("§7Plongez-vous dans un §6long sommeil §7!"),
				Component.text("§7Arpentez vos rêves et gagnez des §6récompenses §7!"),
				Component.text("§7Un monde nouveau s'offre à vous,"),
				Component.text("§7mais arriverez-vous à vous extraire de ce lieu sombre et chaotique ?")
		);
	}
	
	@Override
	public ItemStack getIcon() {
		return ItemStack.of(Material.SCULK);
	}
	
	@Override
	public List<MilestoneQuest> getSteps() {
		return Arrays.stream(DreamSteps.values()).map(DreamSteps::getQuest).toList();
	}
	
	@Override
	public MilestoneType getType() {
		return MilestoneType.DREAM;
	}
	
	@Override
	public Menu getMenu(Player player) {
		return new MilestoneMenu(player, this);
	}
}
