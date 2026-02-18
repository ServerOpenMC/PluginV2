package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarBindEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.items.orb.DominationOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class AltarQuest extends MilestoneQuest implements Listener {
	public AltarQuest() {
		super(
				"Début du rituel",
				List.of(
						"§fDéposer l'§dOrbe de Domination §fsur l'§dAltar",
						"§8§oCommençons le rituel de conversion de l'orbe"
				),
				Material.ENCHANTING_TABLE,
				MilestoneType.DREAM,
				DreamSteps.ALTAR,
				new QuestTier(
						1,
						new QuestTextReward("Hmmm... avec cette table étrange, il est visiblement possible de transformer l'Orbe de Domination. Mais pour en faire quoi !? \n" +
								"Ce qui est sur, c'est qui me manque quelque chose pour accomplir ce rituel jusqu'au bout.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onAltarBind(AltarBindEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		DreamItem item = e.getItem();
		if (item == null) return;
		if (item instanceof DominationOrb) {
			if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
			
			this.incrementProgressInDream(player.getUniqueId());
		}
	}
}
