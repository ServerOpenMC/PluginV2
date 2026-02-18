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
	ETERNAL_FIRE(null),
	GROTTO_CAMP(null),
	ILLUSIONIST(null),
	EWENITE(null),
	MECHANIC_PICKAXE(null),
	GLACITE_ORB(null),
	SINGULARITY(null),
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
		GET_HEART.quest = new CreakingHeartQuest();
		OLD_AXE.quest = new OldAxeQuest();
		DOMINATION_ORB.quest = new CraftDominationOrbQuest();
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
		ETERNAL_FIRE.quest = new CraftEternalFireQuest();
		GROTTO_CAMP.quest = new GrottoCampQuest();
		ILLUSIONIST.quest = new IllusionistQuest();
		EWENITE.quest = new EweniteQuest();
		MECHANIC_PICKAXE.quest = new CraftMechanicPickaxeQuest();
		GLACITE_ORB.quest = new GlaciteOrbQuest();
		SINGULARITY.quest = new CraftSingularityQuest();
		TRANSFERABLE_OBJECT.quest = new TransferableObjectQuest();
	}
}
