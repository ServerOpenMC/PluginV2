package fr.openmc.core.features.dream.milestone;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.milestones.MilestoneUtils;
import fr.openmc.core.features.milestones.bossbar.MilestoneBossBarOptions;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneModel;
import fr.openmc.core.features.milestones.models.MilestoneType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DreamMilestone implements Milestone<DreamSteps>  {
	
	private static final HashMap<UUID, MilestoneModel> playerData = new HashMap<>();
	
	@Override
	public HashMap<UUID, MilestoneModel> getPlayerData() {
		return playerData;
	}
	
	@Override
	public String getName() {
		return "§1Tutoriel de la Dimension des Rêves";
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
	public Class<DreamSteps> getStepClass() {
		return DreamSteps.class;
	}
	
	@Override
	public MilestoneType getType() {
		return MilestoneType.DREAM;
	}
	
	@Override
	public Menu getMenu(Player player) {
		return new MilestoneMenu(player, this);
	}

	@Override
	public MilestoneBossBarOptions getBossBarOptions() {
		return new MilestoneBossBarOptions(
				NamedTextColor.DARK_AQUA,
				BossBar.Color.WHITE,
				BossBar.Overlay.PROGRESS
		);
	}

	@Override
	public boolean shouldDisplayBossBar(Player player) {
		return DreamUtils.isInDreamWorld(player) && !MilestoneUtils.hasFinishedMilestone(this.getType(), player);
	}
}
