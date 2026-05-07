package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.loots.Soul;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SoulsQuest extends MilestoneQuest implements Listener {
	
	public static final int SOULS = 20;
	
	public SoulsQuest() {
		super(
				"Mes amis viennent de l'au-delà",
				List.of(
						"§fRécuérer §d" + SOULS + " §fâmes",
						"§8§oIl me semble avoir vu des créatures",
						"§8§ovolantes rôder vers les grands arbres sombres."
				),
				DreamItemRegistry.getByName("omc_dream:soul").getBest(),
				MilestoneType.DREAM,
				DreamSteps.SOULS,
				new QuestTier(SOULS),
				List.of(
						"§6Et de " + SOULS + " ! Que dois-je faire maintenant ? Il y a un craft spécifique de l'orbe à faire ?",
						"§3Voyageur : Non, il faut simplement répéter l'incantation suivante 2 fois : §dAshkara no thari fu laq to",
						"§6Ashkara no thari fu laq to ! Ashkara no thari fu laq to !",
						"§6...pas facile à dire...",
						"§3Voyageur : Ahah, en réalité, il te faut simplement intéragir à nouveau avec l'§dOrbe de Domination sur l'§dAltar§3."
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
