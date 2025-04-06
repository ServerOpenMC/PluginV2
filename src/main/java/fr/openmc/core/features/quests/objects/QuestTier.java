package fr.openmc.core.features.quests.objects;

import fr.openmc.core.features.quests.rewards.QuestReward;

public record QuestTier(int target, QuestReward reward, String description) {
    public QuestTier(int target, QuestReward reward, String description) {
        this.target = target;
        this.reward = reward;
        this.description = description;
    }

    public String description() {
        return this.description.replace("{number}", String.valueOf(this.target));
    }

    public int target() {
        return this.target;
    }

    public QuestReward reward() {
        return this.reward;
    }
}
