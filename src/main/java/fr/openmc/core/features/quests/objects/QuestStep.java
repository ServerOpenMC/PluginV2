package fr.openmc.core.features.quests.objects;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a step in a quest.
 * Each step has a description, a target number, and tracks the progress of players.
 */
@Getter
public class QuestStep {
    private final String description;
    private final int target;
    private final Map<UUID, Integer> progress = new ConcurrentHashMap<>();

    /**
     * Constructor for QuestStep.
     *
     * @param description The description of the step
     * @param target      The target number to reach for this step
     */
    public QuestStep(String description, int target) {
        this.description = description;
        this.target = target;
    }

    /**
     * Get the description of the step.
     * <p>
     * The description can contain the placeholder {target} which will be replaced by the target number.
     * @return The description of the step with the target number replaced
     */
    public String getDescription() {
        return this.description.replace("{target}", String.valueOf(this.target));
    }

    /**
     * Get the target number of the step.
     *
     * @param playerUUID The UUID of the player
     * @return           The target number
     */
    public int getProgress(UUID playerUUID) {
        return this.progress.getOrDefault(playerUUID, 0);
    }

    /**
     * Set the progress for a player.
     *
     * @param playerUUID The UUID of the player
     * @param progress   The progress to set
     */
    public void setProgress(UUID playerUUID, int progress) {
        this.progress.put(playerUUID, Math.min(progress, this.target));
    }

    /**
     * Increment the progress for a player by a specified amount.
     *
     * @param playerUUID The UUID of the player
     * @param amount     The amount to increment the progress by
     */
    public void incrementProgress(UUID playerUUID, int amount) {
        int currentProgress = getProgress(playerUUID);
        setProgress(playerUUID, currentProgress + amount);
    }

    /**
     * Check if the step is completed for a player.
     *
     * @param playerUUID The UUID of the player
     * @return           True if the step is completed, false otherwise
     */
    public boolean isCompleted(UUID playerUUID) {
        return getProgress(playerUUID) >= this.target;
    }
}