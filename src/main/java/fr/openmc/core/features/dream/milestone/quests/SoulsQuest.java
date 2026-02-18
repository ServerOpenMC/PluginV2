package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.loots.Soul;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SoulsQuest extends MilestoneQuest implements Listener {
	public SoulsQuest() {
		super(
				"Mes amis viennent de l'au-delà",
				List.of(
						"§fRécuérer §d20 §fâmes",
						"§8§oIl me semble avoir vu des créatures volantes rôder vers les grands arbres sombres."
				),
				DreamItemRegistry.getByName("omc_dream:soul").getBest(),
				MilestoneType.DREAM,
				DreamSteps.SOULS,
				new QuestTier(
						20,
						new QuestTextReward("Quelles sont ces créatures ? Des joueurs morts ?? \n" +
								"A vrai dire, je n'ai pas vraiment envie d'y penser, il me faut cette nouvelle orbe.", Prefix.DREAM, MessageType.SUCCESS)
				)
		);
	}
	
	@EventHandler
	public void onCollectSoul(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			ItemStack baseItem = e.getItem().getItemStack();
			
			DreamItem item = DreamItemRegistry.getByItemStack(baseItem);
			if (item == null) return;
			if (item instanceof Soul) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId(), baseItem.getAmount());
				getType().getMilestone().getPlayerData().get(player.getUniqueId()).incrementProgress(baseItem.getAmount());
			}
		}
	}
}
