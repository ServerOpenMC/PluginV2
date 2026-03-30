package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.mobs.Breezy;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestoneType;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.Objects;

public class KillBreezyQuest extends MilestoneQuest implements Listener {
	public KillBreezyQuest() {
		super(
				"L'air du vent",
				List.of(
						"§fBattre §dBreezy",
						"§8§oOn va dompter un des esprits de la montagne de Poncahontas, ou alors c'est Elsa ?"
				),
				Material.WIND_CHARGE,
				MilestoneType.DREAM,
				DreamSteps.KILL_BREEZY,
				new QuestTier(1),
				List.of(
						"§6Mais c'est qu'il est balèze ce Breeze !!",
						"§3Voyageur : Oui, c'est ce que je craignais... même ici, tout a été corrompu.",
						"§6Comment ça corrompu ?",
						"§3Voyageur : Les explications arriveront en temps et en heures. Pour le moment, redescends sur la terre, et dirige-toi vers les plages. " +
								"J'aimerais pouvoir dire de sable fin...",
						"§3Voyageur : Mais avant de partir, tu peux récupérer dans les coffres du château l'armure des Nuages, qui te donnera " +
								((DreamEquipableItem) Objects.requireNonNull(DreamItemRegistry.getByName("omc_dream:cloud_chestplate"))).getAdditionalMaxTime() +
								" secondes de temps supplémentaire par pièces d'armure. Tu peux également récupérer une canne à pêche des nuages, et un livre enchanté.",
						"§6Une canne à pêche ? Mais pour pêcher quoi ? Des gouttelettes de nuage ?!",
						"§3Voyageur : Exactement ! Celle-ci te permettra de pêcher dans les nuages comme si c'était de l'eau. Tu pourras notamment récupérer des " +
								"somnifères qui te permettent de rester plus longtemps endormi, ou de t'endormir efficacement, dans le cas où tu es éveillé."
				)
		);
	}
	
	@EventHandler
	public void onKillBreezy(EntityDeathEvent e) {
		if (e.getDamageSource().getCausingEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			
			if (!DreamStructuresManager.isInsideStructure(player.getLocation(), DreamStructure.DreamType.CLOUD_CASTLE)) return;
			
			if (e.getEntity() instanceof Breezy breezy && breezy.getId().equals("breezy")) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
