package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarBindEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.orb.DominationOrb;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneQuest;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

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
				new QuestTier(1),
				List.of(
						"§3Voyageur : Pour obtenir de l'orbe des âmes, il te faudra...",
						"§6Des âmes ?!",
						"§3Voyageur : Oui ! C'est ça ! Il t'en faudra §d" + SoulsQuest.SOULS + "§3",
						"§3Voyageur : Mais fais attention à toi, elles adorent le temps encore plus que les creakings",
						"§6Comment puis-je en trouver, et comment les reconnaître ?",
						"§3Voyageur : Regarde autour de l'autel, vers les §darbres§3. Les âmes se baladent à l'extérieur du bâtiment.",
						"§3Voyageur : Tout comme l'armure \"Creaking\", il est possible d'avoir l'armure des §d\"Âmes\"§3. Celle-ci te confèrera §d" +
								((DreamEquipableItem) Objects.requireNonNull(DreamItemRegistry.getByName("omc_dream:soul_chestplate"))).getAdditionalMaxTime() +
								" secondes §3supplémentaires par pièces d'armure équipées.",
						"§3Voyageur : Tu peux également transformer ta hache à l'autel avec quelques âmes supplémentaires."
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
