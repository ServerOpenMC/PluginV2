package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.Singularity;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class CraftSingularityQuest extends MilestoneQuest implements Listener {
	
	public CraftSingularityQuest() {
		super(
				"La finalité ?",
				List.of(
						"§fCrafter la §dSingularité",
						"§8§oLes orbes n'auraient-ils pas une utilité finale ?"
				),
				DreamItemRegistry.getByName("omc_dream:singularity").getBest(),
				MilestoneType.DREAM,
				DreamSteps.CRAFT_SINGULARITY,
				new QuestTier(1),
				List.of(
						"§6Voilà ! Que faut-il faire pour pouvoir maintenant remonter le temps ?",
						"§3Voyageur : ...",
						"§3Voyageur : Je... je vais t'avouer quelque chose. Il n'a jamais été question de remonter le temps dans cette dimension, mais de pouvoir me " +
								"transférer dans une autre dimension afin d'échapper aux malheurs de ce monde. Mon but a bien toujours été d'aider les gens, mais cela " +
								"n'a pas été possible dans cette dimension.",
						"§3Voyageur : Je n'ai pas voulu t'en parler au risque de paraître égoïste.",
						"§3Voyageur : Malheureusement pour moi, la singularité que nous avons n'est pas assez puissante pour pouvoir transférer da la vie dans ta dimension " +
								"afin de pouvoir reprendre un nouveau départ et de pouvoir aider les gens.",
						"§3Voyageur : Mais cela n'est pas grave, j'ai aimé pour t'aider à explorer ce monde et surtout à pouvoir récupérer des objets qui te seront " +
								"sans doute utiles dans ton monde.",
						"§6...",
						"§3Voyageur : Je n'ai pas réussi à sauver cette dimension, mais j'ai tout de même réussi à t'aider \"toi\" dans cette dimension.",
						"§3Voyageur : Quoi qu'il en soit, tu vas pouvoir §drécupérer les enchantements §3de cette dimension grâce à la singularité et même l'§darmure §3si tu le souhaites.",
						"§3Voyageur : La singularité n'est pas assez puissante pour moi, mais largement assez pour des nouvelles armes ou armures.",
						"§3Voyageur : Tout comme les orbes précédents, tu as la possibilité de te fabriquer une nouvelle armure. Celle-ci en revanche va nécessiter de refaire " +
								"l'ensemble des orbes, mais ça en vaut le coup, car elle rajoute §d" + ((DreamEquipableItem) Objects.requireNonNull(DreamItemRegistry.getByName("omc_dream:dream_chestplate"))).getAdditionalMaxTime()
								+ " secondes §3de plus par pièce portée, mais aussi des effets.",
						"§6... Merci !",
						"§3Voyageur : Non ! C'est moi qui te remercie. Car un jour, quelqu'un que je respectais comme personne m'a dit : \"Remercie la personne que tu as aidé, " +
								"car elle t'a permis de faire une bonne action\".",
						"§6Tu resteras avec moi ?",
						"§3Voyageur : Non, je ne peux pas. D'autres joueurs se perdent ici, et ils ont besoin de moi. Alors, je te dis adieu..."
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
			if (dreamItem instanceof Singularity) {
				if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
				this.incrementProgressInDream(player.getUniqueId());
			}
		}
	}
}
