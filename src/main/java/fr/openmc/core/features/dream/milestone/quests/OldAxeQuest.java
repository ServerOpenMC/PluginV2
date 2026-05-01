package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.OldCreakingAxe;
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
import java.util.Objects;

public class OldAxeQuest extends MilestoneQuest implements Listener {
	
	public OldAxeQuest() {
		super(
				"Le premier outil ! Enfin !",
				List.of(
						"§fFabriquer une §dVieille hache du Creaking",
						"§8§oNotre meilleur ami dans ce monde !"
				),
				DreamItemRegistry.getByName("omc_dream:old_creaking_axe").getBest(),
				MilestoneType.DREAM,
				DreamSteps.OLD_AXE,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Bien ! Cela va nous être utile. Le prochain objectif va être d'obtenir l'§dOrbe de Domination§3.",
						"§6À quoi va-t-elle servir ?",
						"§3Voyageur : Je t'expliquerai lorsque tu l'auras. Elle va être facile à récupérer avec la hache.",
						"§6Ai-je besoin d'autre chose ?",
						"§3Voyageur : Ce n'est pas obligatoire, mais elle te permettrait de rester plus longtemps dans ce monde. Il existe l'armure §d\"Creaking\" §3que tu peux fabriquer. " +
								"Cela te confèrera §d" + ((DreamEquipableItem) Objects.requireNonNull(DreamItemRegistry.getByName("omc_dream:old_creaking_chestplate"))).getAdditionalMaxTime() + " secondes §3supplémentaires " +
								"par pièces d'armure équipées."
				)
		);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		
		DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
		if (dreamItem == null) return;
		if (dreamItem instanceof OldCreakingAxe) {
			if (e.getWhoClicked() instanceof Player player) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
