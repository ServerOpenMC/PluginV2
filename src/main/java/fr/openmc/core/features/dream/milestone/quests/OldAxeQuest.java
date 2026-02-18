package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.milestone.DreamSteps;
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
						"§3Voyageur : Une hâche, c'est déjà ça.",
						"§6Et maintenant ?",
						"§3Voyageur : Maintenant, il faut obtenir l'§dOrbe de Domination§3, puis je t'en expliquerai plus sur ce monde.",
						"§6Mais comment obtenir cette Orbe ?",
						"§3Voyageur : Ce n'est pas compliqué : elle se fabrique avec la §dtable de craft§3. Fais §d/crafts §3si tu as oublié comment. " +
								"Elle te permettra de pouvoir explorer une nouvelle zone de ce rêve."
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
