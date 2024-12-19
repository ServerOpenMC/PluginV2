package fr.openmc.core.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/**
 * Manages dynamic cooldowns for entities identified by UUID
 */
public class DynamicCooldown {

    @Getter private static final DynamicCooldown cityCreationCommands = new DynamicCooldown();

    /**
     * Represents a single cooldown with duration and last use time
     */
    public class Cooldown {
        private final long duration;
        private long lastUse;

        /**
         * @param duration Cooldown duration in ms
         */
        public Cooldown(long duration) {
            this.duration = duration;
            this.lastUse = System.currentTimeMillis();
        }

        /**
         * @return true if cooldown has expired
         */
        public boolean isReady() {
            return System.currentTimeMillis() - lastUse > duration;
        }
    }

    private HashMap<UUID, Cooldown> lastUse = new HashMap<>();

    /**
     * @param uuid Entity UUID to check
     * @return true if entity can perform action
     */
    public boolean isReady(UUID uuid) {
        Cooldown cooldown = lastUse.get(uuid);
        return cooldown == null || cooldown.isReady();
    }

    /**
     * Puts entity on cooldown
     * @param uuid Entity UUID
     * @param duration Cooldown duration in ms
     */
    public void use(UUID uuid, long duration) {
        lastUse.put(uuid, new Cooldown(duration));
    }

    /**
     * Removes expired cooldowns
     */
    public void cleanup() {
        lastUse.entrySet().removeIf(entry -> entry.getValue().isReady());
    }

    /**
     * Get remaining time of cooldown in ms
     * @param uuid Entity UUID
     */
    public long getRemainingTime(UUID uuid) {
        Cooldown cooldown = lastUse.get(uuid);
        if (cooldown == null) return 0;
        return cooldown.duration - (System.currentTimeMillis() - cooldown.lastUse);
    }
}