package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.MudOrb;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMethodsReward;
import fr.openmc.core.features.quests.rewards.QuestTextReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;

public class MudOrbQuest extends MilestoneQuest implements Listener {
	
	public MudOrbQuest() {
		super(
				"Bip Biip Biiiiiiip",
				List.of(
						"Trouver l'Orbe de Boue"
				),
				DreamItemRegistry.getByName("omc_dream:mud_orb").getBest(),
				MilestoneType.DREAM,
				DreamSteps.MUD_ORB,
				new QuestTier(
						1,
						new QuestTextReward("", Prefix.DREAM, MessageType.SUCCESS),
						new QuestMethodsReward(
								player -> {
									if (player.getInventory().contains(DreamItemRegistry.getByName("omc_dream:crystallized_pickaxe").getBest())) {
										DreamSteps.CRYSTALLIZED_PICKAXE.getQuest().incrementProgressInDream(player.getUniqueId());
									}
								}
						)
				)
		);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			DreamItem item = DreamItemRegistry.getByItemStack(e.getItem().getItemStack());
			if (item == null) return;
			if (item instanceof MudOrb) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
