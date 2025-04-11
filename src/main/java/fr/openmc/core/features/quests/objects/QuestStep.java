package fr.openmc.core.features.quests.objects;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Getter
public class QuestStep {
    private final String description;
    private final int target;
    private final Map<UUID, Integer> progress = new ConcurrentHashMap<>();

    public QuestStep(String description, int target) {
        this.description = description;
        this.target = target;
    }


    public String getDescription() {
        return this.description.replace("{number}", String.valueOf(this.target));
    }


    public int getProgress(UUID playerUUID) {
        return this.progress.getOrDefault(playerUUID, 0);
    }


    public void setProgress(UUID playerUUID, int progress) {
        this.progress.put(playerUUID, Math.min(progress, this.target));
    }

    public void incrementProgress(UUID playerUUID, int amount) {
        int currentProgress = getProgress(playerUUID);
        setProgress(playerUUID, currentProgress + amount);
    }


    public boolean isCompleted(UUID playerUUID) {
        return getProgress(playerUUID) >= this.target;
    }
}