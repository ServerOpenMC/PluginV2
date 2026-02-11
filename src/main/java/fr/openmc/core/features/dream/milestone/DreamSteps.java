package fr.openmc.core.features.dream.milestone;

import fr.openmc.core.features.dream.milestone.quests.*;
import fr.openmc.core.features.milestones.MilestoneQuest;
import lombok.Getter;

@Getter
public enum DreamSteps {
	
	SLEEP(null),
	CRAFTS(null),
	CRAFTING_TABLE(null),
	GET_HEART(null),
	OLD_AXE(null),
	DOMINATION_ORB(null),
	CUBE_TEMPLE(null),
	ALTAR_DOMINATION(null),
	SOULS(null),
	SOUL_ORB(null),
	;
	
	private MilestoneQuest quest;
	
	DreamSteps(MilestoneQuest quest) {
		this.quest = quest;
	}
	
	static {
		SLEEP.quest = new SleepQuest();
		CRAFTS.quest = new CraftsQuest();
		CRAFTING_TABLE.quest = new CraftingTableQuest();
		GET_HEART.quest = new GetHeartQuest();
		OLD_AXE.quest = new OldAxeQuest();
		DOMINATION_ORB.quest = new CraftDominationOrbQuest();
		CUBE_TEMPLE.quest = new CubeTempleQuest();
		ALTAR_DOMINATION.quest = new AltarDominationOrbQuest();
		SOULS.quest = new SoulsQuest();
		SOUL_ORB.quest = new SoulOrbQuest();
	}
}
