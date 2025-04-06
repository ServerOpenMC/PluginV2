package fr.openmc.core.features.quests.objects;

import fr.openmc.core.features.quests.rewards.QuestReward;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class QuestTier {
    private final int target;
    private final QuestReward reward;
    private final String description;
    private final List<QuestStep> steps;
    private final boolean requireStepsCompletion;


    /**
     * Creates a new quest tier without steps.
     *
     * @param target The target progress to complete this tier
     * @param reward The reward for completing this tier
     * @param description The description of this tier
     */
    public QuestTier(int target, QuestReward reward, String description) {
        this(target, reward, description, new ArrayList<>(), false);
    }

    /**
     * Creates a new quest tier with steps.
     *
     * @param target The target progress to complete this tier
     * @param reward The reward for completing this tier
     * @param description The description of this tier
     * @param steps The steps required for this tier
     * @param requireStepsCompletion If true, all steps must be completed to complete the tier
     */
    public QuestTier(int target, QuestReward reward, String description, List<QuestStep> steps, boolean requireStepsCompletion) {
        this.target = target;
        this.reward = reward;
        this.description = description;
        this.steps = steps;
        this.requireStepsCompletion = requireStepsCompletion;
    }

    /**
     * Gets the formatted description with the target value.
     * @return The formatted description
     */
    public String description() {
        return this.description.replace("{number}", String.valueOf(this.target));
    }

    /**
     * Gets the target progress for this tier.
     * @return The target progress
     */
    public int target() {
        return this.target;
    }

    /**
     * Gets the reward for completing this tier.
     * @return The reward for this tier
     */
    public QuestReward reward() {
        return this.reward;
    }

    /**
     * Adds a step to this tier.
     * @param step The step to add
     */
    public void addStep(QuestStep step) {
        this.steps.add(step);
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

    /**
     * Gets the current active step for a player.
     * @param playerUUID The UUID of the player.
     * @return The current step, or null if all steps are completed.
     */
    public QuestStep getCurrentStep(UUID playerUUID) {
        if (steps.isEmpty()) {
            return null;
        }

        for (QuestStep step : steps) {
            if (!step.isCompleted(playerUUID)) {
                return step;
            }
        }

        return null;
    }
}