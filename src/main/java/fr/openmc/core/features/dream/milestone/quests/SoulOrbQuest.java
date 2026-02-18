package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.SoulOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SoulOrbQuest extends MilestoneQuest implements Listener {
	public SoulOrbQuest() {
		super(
				"Il faut un sacrifice !",
				List.of(
						"§fObtenir l'§dOrbe des Âmes",
						"§8§oÂmes dans la poche, permettez moi, par votre pouvoir, d'obtenir votre orbe avec le sacrifice de l'Orbe de Domination."
				),
				DreamItemRegistry.getByName("omc_dream:ame_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.SOUL_ORB,
				new QuestTier(
						1,
						new QuestTextReward("Et de deux ! Maintenant que j'ai l'Orbe des Âmes, je n'ai plus celle de Domination, il faudra donc que j'y retourne en fabriquer. \n" +
								"Et où dois-je aller maintenant ? Hmmmm... ces nuages m'intriguent, j'ai l'impression d'y voir un château.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onSoulOrbCrafting(AltarCraftingEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = e.getCraftedItem();
		if (item == null) return;
		if (item instanceof SoulOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
