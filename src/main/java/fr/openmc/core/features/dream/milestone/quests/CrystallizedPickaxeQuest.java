package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.CrystalizedPickaxe;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class CrystallizedPickaxeQuest extends MilestoneQuest implements Listener {
	
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
								Objects.requireNonNull(MultiBlockManager.getMultiblockAtDimension("world_dream")).origin.getBlockX() + " §9Z: " +
								Objects.requireNonNull(MultiBlockManager.getMultiblockAtDimension("world_dream")).origin.getBlockZ() + "§3. Comme promis, je te dois des explications."
				)
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
}
