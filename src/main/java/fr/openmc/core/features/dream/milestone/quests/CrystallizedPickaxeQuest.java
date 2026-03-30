package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.dream.DreamUtils;
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
import org.bukkit.event.entity.EntityPickupItemEvent;

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
						"§3Voyageur : Celle-ci sera ta meilleure amie dans les grottes, en remplacement de ta hache.",
						"§3Voyageur : À partir de maintenant, tu vas devoir principalement miner. Les profondeurs de ce monde regorgent de minerais utiles " +
								"pour la dernière étape de cette quête.",
						"§6Alors ne traînons pas, pastons en grotte.",
						"§3Voyageur : Non ! Avant d'aller chercher le dernier orbe, fais un détour aux coordonnées " +
								Objects.requireNonNull(MultiBlockManager.getMultiblockAtDimension("world_dream")).origin.getBlockX() + " " +
								Objects.requireNonNull(MultiBlockManager.getMultiblockAtDimension("world_dream")).origin.getBlockZ() + ". Comme promis, je te dois des explications."
				)
		);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (! DreamUtils.isInDreamWorld(player)) return;
			
			DreamItem item = DreamItemRegistry.getByItemStack(e.getItem().getItemStack());
			if (item == null) return;
			if (item instanceof CrystalizedPickaxe) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
