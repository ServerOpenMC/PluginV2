package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.CloudOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class KillBreezyQuest extends MilestoneQuest implements Listener {
	public KillBreezyQuest() {
		super(
				"L'air du vent",
				List.of(
						"§fBattre §dBreezy",
						"§8§oOn va dompter un des esprits de la montagne",
						"§8§ode Poncahontas, ou alors c'est Elsa ?"
				),
				Material.WIND_CHARGE,
				MilestoneType.DREAM,
				DreamSteps.KILL_BREEZY,
				new QuestTier(1),
				List.of(
						"§6Mais c'est qu'il est balèze ce Breeze !!",
						"§3Voyageur : Oui, c'est ce que je craignais... même ici, tout a été corrompu.",
						"§6Comment ça corrompu ?",
						"§3Voyageur : Les explications arriveront en temps et en heures. Pour le moment, redescends sur terre, et dirige-toi vers les §dplages§3. " +
								"J'aimerais pouvoir dire de sable fin...",
						"§3Voyageur : Mais avant de partir, tu peux récupérer dans les coffres du château l'§dArmure des Nuages§3, qui te donnera §d" +
								((DreamEquipableItem) Objects.requireNonNull(DreamItemRegistry.getByName("omc_dream:cloud_chestplate"))).getAdditionalMaxTime() +
								" secondes §3de temps supplémentaire par pièces d'armure. Tu peux également récupérer une §dcanne à pêche des nuages§3, et un §dlivre enchanté§3.",
						"§6Une canne à pêche ? Mais pour pêcher quoi ? Des gouttelettes de nuage ?!",
						"§3Voyageur : Exactement ! Celle-ci te permettra de pêcher dans les nuages comme si c'était un lac. Tu pourras notamment récupérer des " +
								"§dsomnifères §3qui te permettent de rester plus longtemps endormi, ou de t'endormir efficacement, dans le cas où tu es éveillé."
				)
		);
	}
	
	@EventHandler
	public void onCollectOrb(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (!DreamUtils.isInDreamWorld(player)) return;
			if (!DreamStructuresManager.isInsideStructure(player.getLocation(), DreamStructure.DreamType.CLOUD_CASTLE)) return;
			
			ItemStack baseItem = e.getItem().getItemStack();
			
			DreamItem item = DreamItemRegistry.getByItemStack(baseItem);
			if (item == null) return;
			if (item instanceof CloudOrb) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId(), baseItem.getAmount());
			}
		}
	}
}
