package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.milestones.tutorial.quests.*;
import fr.openmc.core.features.quests.objects.Quest;
import lombok.Getter;

@Getter
public enum TutorialStep {
    BREAK_AYWENITE(null),
    CITY_CREATE(null),
    CITY_LEVEL_2(null),
    HOME_CREATE(null),
    HOME_UPGRADE(null),
    OPEN_QUEST(null),
    FINISH_QUEST(null),
    OPEN_ADMINSHOP(null),
    SELL_BUY_ADMINSHOP(null),
    SPARE_BANK(null),
    //TODO: ajouter des quêtes autour des shops/entreprises lorsque refonte faite
    OPEN_SETTINGS(null),
    OPEN_CONTEST(null),
    CLAIM_LETTER(null),
    ;

    private Quest quest;

    TutorialStep(Quest quest) {
        this.quest = quest;
    }
    
    // ça peut paraitre con de faire ça, mais obligatoire pour pas avoir d'instance nulle de quête.
    static {
        BREAK_AYWENITE.quest = new BreakAyweniteQuest();
        CITY_CREATE.quest = new CityCreateQuest();
        CITY_LEVEL_2.quest = new CityLevel2Quest();
        HOME_CREATE.quest = new HomeCreateQuest();
        HOME_UPGRADE.quest = new HomeUpgradeQuest();
        OPEN_QUEST.quest = new OpenQuestMenuQuest();
        FINISH_QUEST.quest = new FinishQuestQuest();
        OPEN_ADMINSHOP.quest = new OpenAdminShopMenuQuest();
        SELL_BUY_ADMINSHOP.quest = new SellBuyQuest();
        SPARE_BANK.quest = new SpareBankQuest();
        OPEN_SETTINGS.quest = new OpenSettingsMenuQuest();
        OPEN_CONTEST.quest = new OpenContestMenuQuest();
        CLAIM_LETTER.quest = new ClaimLetterQuest();
    }
}
