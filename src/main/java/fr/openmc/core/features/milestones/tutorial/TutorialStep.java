package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.milestones.tutorial.quests.*;
import fr.openmc.core.features.quests.objects.Quest;
import lombok.Getter;

@Getter
public enum TutorialStep {
    BREAK_AYWENITE(null),
    CITY_CREATE(null),
    HOME_CREATE(null),
    HOME_UPGRADE(null),
    OPEN_QUEST(null),
    QUEST_FINISH(null),
    OPEN_ADMINSHOP(null),
    SELL_BUY_ADMINSHOP(null),
    SPARE_BANK(null),
    SETTINGS(null), //TODO: ajouter des quêtes autour des shops/entreprises lorsque refonte faite
    OPEN_CONTEST(null),
    OPEN_LETTER(null),
    LINK_DISCORD(null);

    private Quest quest;

    TutorialStep(Quest quest) {
        this.quest = quest;
    }

    // ça peut paraitre de faire ça mais obligatoire pour pas avoir d'instance nulle de quete.
    static {
        BREAK_AYWENITE.quest = new BreakAyweniteQuest();
        CITY_CREATE.quest = new CityCreateQuest();
        HOME_CREATE.quest = new HomeCreateQuest();
        HOME_UPGRADE.quest = new HomeUpgradeQuest();
        OPEN_QUEST.quest = new OpenQuestMenuQuest();
    }
}
