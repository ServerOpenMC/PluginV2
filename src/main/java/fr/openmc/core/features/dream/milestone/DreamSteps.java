package fr.openmc.core.features.dream.milestone;

import fr.openmc.core.features.dream.milestone.quests.*;
import fr.openmc.core.features.milestones.MilestoneStep;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import lombok.Getter;

@Getter
public enum DreamSteps implements MilestoneStep {
	
	SLEEP(null),
	CRAFTS(null),
	CRAFTING_TABLE(null),
	CREAKING_HEART(null),
	OLD_AXE(null),
	CRAFT_DOMINATION_ORB(null),
	CUBE_TEMPLE(null),
	ALTAR(null),
	SOULS(null),
	SOUL_ORB(null),
	CLOUD_VALLEY(null),
	CLOUD_CASTLE(null),
	KILL_BREEZY(null),
	MUD_BEACH(null),
	METAL_DETECTOR(null),
	MUD_ORB(null),
	CRYSTALLIZED_PICKAXE(null),
	FIND_CUBE(null),
	CRAFT_ETERNAL_FIRE(null),
	GROTTO_CAMP(null),
	ILLUSIONIST(null),
	EWENITE(null),
	GLACITE_ORB(null),
	CRAFT_SINGULARITY(null),
	TRANSFERABLE_OBJECT(null)
	;
	
	private MilestoneQuest quest;
	
	DreamSteps(MilestoneQuest quest) {
		this.quest = quest;
	}
	
	static {
		SLEEP.quest = new SleepQuest();
		CRAFTS.quest = new CraftsQuest();
		CRAFTING_TABLE.quest = new CraftingTableQuest();
		CREAKING_HEART.quest = new CreakingHeartQuest();
		OLD_AXE.quest = new OldAxeQuest();
		CRAFT_DOMINATION_ORB.quest = new CraftDominationOrbQuest();
		CUBE_TEMPLE.quest = new CubeTempleQuest();
		ALTAR.quest = new AltarQuest();
		SOULS.quest = new SoulsQuest();
		SOUL_ORB.quest = new SoulOrbQuest();
		CLOUD_VALLEY.quest = new CloudValleyQuest();
		CLOUD_CASTLE.quest = new CloudCastleQuest();
		KILL_BREEZY.quest = new KillBreezyQuest();
		MUD_BEACH.quest = new MudBeachQuest();
		METAL_DETECTOR.quest = new MetalDetectorQuest();
		MUD_ORB.quest = new MudOrbQuest();
		CRYSTALLIZED_PICKAXE.quest = new CrystallizedPickaxeQuest();
		FIND_CUBE.quest = new FindCubeQuest();
		CRAFT_ETERNAL_FIRE.quest = new CraftEternalFireQuest();
		GROTTO_CAMP.quest = new GrottoCampQuest();
		ILLUSIONIST.quest = new IllusionistQuest();
		EWENITE.quest = new EweniteQuest();
		GLACITE_ORB.quest = new GlaciteOrbQuest();
		CRAFT_SINGULARITY.quest = new CraftSingularityQuest();
		TRANSFERABLE_OBJECT.quest = new TransferableObjectQuest();
	}
}
