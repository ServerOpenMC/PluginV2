package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.features.cube.events.EnterCubeZoneEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FindCubeQuest extends MilestoneQuest implements Listener {
	public FindCubeQuest() {
		super(
				"Un petit détour",
				List.of(
						"§fTrouver le §dCube",
						"§8§oMystère et explications"
				),
				Material.LAPIS_BLOCK,
				MilestoneType.DREAM,
				DreamSteps.FIND_CUBE,
				new QuestTier(1),
				List.of(
						"§3Voyageur : Voilà l'origine de la catastrophe de ce monde. Il s'agit du §d\"Cube\"§3, le maître des lieux. Il a été vénéré par le peuple de cette dimension " +
								"qui a construit les temples que tu as vu. Son nom : §dBobby§3.",
						"§6Mais qu'a-t-il fait et pourquoi n'y a-t-il pas de sculk autour de lui ?",
						"§3Voyageur : Au début, celui-ci nous apportait paix et prospérité. Mais un jour, un violent orage serait survenu, un éclair l'aurait frappé et l'aurait endommagé. ",
						"§3Voyageur : Le cube après s'être régénéré durant une longue période, aurait commencé à bouger et peu à peu, à §dcorrompre §3le monde, mais en installant un §décosystème autosuffisant " +
								"§3pour son nouvel état. C'est-à-dire que lui-même se protège et se propage seul, en se nourrissant de la §dvie §3du monde.",
						"§3Voyageur : Cela a conduit à la §danhilation §3ou la §dcorruption §3de toute vie dans ce lieu.",
						"§6Et toi dans tout cela, qui es-tu ? Depuis le début de notre aventure, tu me parles, mais tu n'es pas vraiment là.",
						"§3Voyageur : Il est très probable que je ne sois plus de ce monde, du moins physiquement. Lorsque le cube a corrompu la carte, au lieu de lutter contre lui, " +
								"le l'ai utilisé pour §ddétruire mon corps§3, en forçant la §dsurvie §3de mon esprit. Étant donné que j'ai utilisé le cube pour survivre, il ne peut pas m'éliminer " +
								"sans se tuer par lui-même.",
						"§3Voyageur : Je suis alors présent pour aider se monde à §dretrouver son ancienne vie§3. Lorsque je t'ai vu arriver, je t'ai suivi afin que tu puisses §dréaliser §3à ma place, " +
								"la quête que j'étais censé réaliser.",
						"§6...",
						"§3Voyageur : Voilà, maintenant que tu en sais un peu plus, nous devons avancer dans la quête qui est désormais la tienne. Direction : §dles grottes§3."
				)
		);
	}
	
	@EventHandler
	public void onEnterCubeZone(EnterCubeZoneEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) return;
		this.incrementProgressInDream(player.getUniqueId());
	}
}
