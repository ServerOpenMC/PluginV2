package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.milestones.tutorial.quests.BreakAyweniteQuest;
import fr.openmc.core.features.milestones.tutorial.quests.CityCreateQuest;
import fr.openmc.core.features.milestones.tutorial.quests.HomeCreateQuest;
import fr.openmc.core.features.quests.objects.Quest;
import lombok.Getter;

@Getter
public enum TutorialStep {
    BREAK_AYWENITE(new BreakAyweniteQuest()),
    CITY_CREATE(new CityCreateQuest()),
    HOME_CREATE(new HomeCreateQuest());

    private final Quest quest;

    TutorialStep(Quest quest) {
        this.quest = quest;
    }
}
