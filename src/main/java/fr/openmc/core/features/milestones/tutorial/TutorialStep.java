package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.milestones.tutorial.quests.BreakAyweniteQuest;
import fr.openmc.core.features.milestones.tutorial.quests.CityCreateQuest;
import fr.openmc.core.features.milestones.tutorial.quests.HomeCreateQuest;
import fr.openmc.core.features.quests.objects.Quest;
import lombok.Getter;

@Getter
public enum TutorialStep {
    BREAK_AYWENITE(null),
    CITY_CREATE(null),
    HOME_CREATE(null);

    private Quest quest;

    TutorialStep(Quest quest) {
        this.quest = quest;
    }

    // ça peut paraitre de faire ça mais obligatoire pour pas avoir d'instance nulle de quete.
    static {
        BREAK_AYWENITE.quest = new BreakAyweniteQuest();
        CITY_CREATE.quest = new CityCreateQuest();
        HOME_CREATE.quest = new HomeCreateQuest();
    }
}
