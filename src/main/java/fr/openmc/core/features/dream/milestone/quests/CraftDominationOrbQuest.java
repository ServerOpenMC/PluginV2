package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.DominationOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftDominationOrbQuest extends MilestoneQuest implements Listener {
	public CraftDominationOrbQuest() {
		super(
				"Dominer, c'est cool",
				List.of(
						"§fFabriquer l'§dOrbe de Domination",
						"§8§o1 sur 5 pour les dominer tous !"
				),
				DreamItemRegistry.getByName("omc_dream:domination_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.DOMINATION_ORB,
				new QuestTier(1),
				List.of(
						"§6Voilà l'orbe, que dois-je faire avec ?",
						"§3Voyageur : Cette orbe est la première d'une série de §d5§3. Chacune d'elles permet de survivre dans la §dzone suivante§3, " +
								"permettant de récupérer une nouvelle orbe et ainsi de suite pour ma euh... notre quête !",
						"§6Je dois donc explorer un nouveau biome...",
						"§3Voyageur : C'est exact ! Il va falloir trouver le §dTemple §3du maître des lieux. Tu ne devrais pas le louper je pense..."
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if (e.getWhoClicked() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			ItemStack item = e.getCurrentItem();
			if (item == null) return;
			
			DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
			if (dreamItem == null) return;
			if (dreamItem instanceof DominationOrb) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
