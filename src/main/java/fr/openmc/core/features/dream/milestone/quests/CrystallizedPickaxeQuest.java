package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.DreamEnterEvent;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.CrystalizedPickaxe;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.utils.text.DirectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class CrystallizedPickaxeQuest extends MilestoneQuest implements Listener {
	
	private static final Location cubeLoc = Objects.requireNonNull(MultiBlockManager.getMultiblockAtDimension(DreamDimensionManager.DIMENSION_NAME)).origin;
	
	public CrystallizedPickaxeQuest() {
		super(
				"Bonne pioche",
				List.of(
						"§fObtenir la §dPioche Cristallisée",
						"§8§oParfois, il faut savoir se creuser la tête"
				),
				DreamItemRegistry.getByName("omc_dream:crystallized_pickaxe").getBest(),
				MilestoneType.DREAM,
				DreamSteps.CRYSTALLIZED_PICKAXE,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Celle-ci sera ta meilleure amie dans les §dgrottes§3, en remplacement de ta hache.",
						"§3Voyageur : À partir de maintenant, tu vas devoir principalement miner. Les profondeurs de ce monde regorgent de §dminerais utiles " +
								"§3pour la §ddernière étape §3de cette quête.",
						"§6Alors ne traînons pas, partons en grotte.",
						"§3Voyageur : Non ! Avant d'aller chercher le dernier orbe, fais un détour aux coordonnées §cX: " +
								CrystallizedPickaxeQuest.cubeLoc.getBlockX() + " §9Z: " +
								CrystallizedPickaxeQuest.cubeLoc.getBlockZ() + "§3. Comme promis, je te dois des explications."
				),
				(player) -> new BukkitRunnable() {
					@Override
					public void run() {
						if (!player.isOnline() || !DreamUtils.isInDream(player)) {
							this.cancel();
							return;
						}
						
						if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) > DreamSteps.FIND_CUBE.ordinal()) {
							this.cancel();
							return;
						}
						
						int distance = (int) player.getLocation().distance(CrystallizedPickaxeQuest.cubeLoc);
						String direction = DirectionUtils.getDirectionArrow(player, CrystallizedPickaxeQuest.cubeLoc);
						player.sendActionBar(Component.text("§b【Cube】 §eDistance : §6" + distance + " blocs §7(" + direction + ")"));
					}
				}.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L)
		);
	}
	
	@EventHandler
	public void onPickUp(MetalDetectorLootEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = DreamItemRegistry.getByItemStack(e.getLoot().getFirst());
		if (item == null) return;
		if (item instanceof CrystalizedPickaxe) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerReturnDim(DreamEnterEvent e) {
		Player player = e.getPlayer();
		if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) == DreamSteps.FIND_CUBE.ordinal()) {
			this.afterDialog.accept(player);
		}
	}
}
