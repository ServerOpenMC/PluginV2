package fr.openmc.core.features.quests.objects;

import fr.openmc.core.features.quests.rewards.QuestReward;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a tier in a quest.
 * A tier can have a target progress, rewards, and steps.
 */
@Getter
public class QuestTier {
    private final int target;
    private final List<QuestReward> rewards;
    private final List<QuestStep> steps;
    private final boolean requireStepsCompletion;

    /**
     * Creates a new quest tier without steps.
     *
     * @param target The target progress to complete this tier
     * @param reward The reward for completing this tier
     */
    public QuestTier(int target, QuestReward reward) {
        this(target, List.of(reward), new ArrayList<>(), false);
    }

    /**
     * Creates a new quest tier without steps, with multiple rewards.
     *
     * @param target The target progress to complete this tier
     * @param rewards The reward for completing this tier
     */
    public QuestTier(int target, QuestReward... rewards) {
        this(target, List.of(rewards), new ArrayList<>(), false);
    }

    /**
     * Creates a new quest tier with steps.
     *
     * @param target The target progress to complete this tier
     * @param rewards The reward for completing this tier
     * @param steps The steps required for this tier
     * @param requireStepsCompletion If true, all steps must be completed to complete the tier
     */
    public QuestTier(int target, List<QuestReward> rewards, List<QuestStep> steps, boolean requireStepsCompletion) {
        this.target = target;
        this.rewards = rewards;
        this.steps = steps;
        this.requireStepsCompletion = requireStepsCompletion;
    }

    /**
     * Gets the target progress for this tier.
     * @return The target progress
     */
    public int target() {
        return this.target;
    }

    /**
     * Checks if all steps are completed for a player.
     * @param playerUUID The UUID of the player.
     * @return True if all steps are completed, false otherwise.
     */
    public boolean areStepsCompleted(UUID playerUUID) {
        if (steps.isEmpty()) {
            return true;
        }

        return steps.stream().allMatch(step -> step.isCompleted(playerUUID));
    }
}