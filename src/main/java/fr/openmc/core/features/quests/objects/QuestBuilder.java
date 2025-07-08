package fr.openmc.core.features.quests.objects;

import fr.openmc.core.features.quests.rewards.QuestReward;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class QuestBuilder {
    private final String name, description;
    private final ItemStack icon;
    private final List<QuestTier> tiers = new ArrayList<>();
    private int currentTierTarget;
    private List<QuestReward> currentTierRewards;
    private String currentTierDescription;
    private final List<QuestStep> currentTierSteps = new ArrayList<>();
    private boolean currentTierRequireSteps = false;

    public QuestBuilder(String name, String description, ItemStack icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }


    public QuestBuilder tier(int target, String description, QuestReward... rewards) {
        if (currentTierDescription != null) {
            finishTier();
        }

        this.currentTierTarget = target;
        this.currentTierRewards = List.of(rewards);
        this.currentTierDescription = description;
        this.currentTierSteps.clear();
        this.currentTierRequireSteps = false;

        return this;
    }

    public QuestBuilder step(String description, int target) {
        if (currentTierDescription == null) {
            throw new IllegalStateException("Must define a tier before adding steps");
        }

        currentTierSteps.add(new QuestStep(description, target));
        return this;
    }

    public QuestBuilder requireAllSteps(boolean require) {
        this.currentTierRequireSteps = require;
        return this;
    }

    private void finishTier() {
        if (currentTierDescription != null) {
            QuestTier tier = new QuestTier(
                    currentTierTarget,
                    new ArrayList<>(currentTierRewards),
                    new ArrayList<>(currentTierSteps),
                    currentTierRequireSteps
            );

            tiers.add(tier);

            currentTierDescription = null;
            currentTierSteps.clear();
        }
    }

    public Quest build() {
        finishTier();

        Quest quest = new Quest(name, description, icon);
        for (QuestTier tier : tiers) {
            quest.addTier(tier);
        }

        return quest;
    }
}