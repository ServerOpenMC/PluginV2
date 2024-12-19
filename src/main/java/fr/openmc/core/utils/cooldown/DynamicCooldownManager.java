package fr.openmc.core.utils.cooldown;

import java.util.HashMap;
import java.util.UUID;

/**
 * Main class for managing cooldowns
 */
public class DynamicCooldownManager {
    /**
     * Represents a single cooldown with duration and last use time
     */
    public static class Cooldown {
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

        /**
         * @return remaining time in milliseconds
         */
        public long getRemaining() {
            return Math.max(0, duration - (System.currentTimeMillis() - lastUse));
        }
    }

    // Map structure: UUID -> (Group -> Cooldown)
    private static final HashMap<UUID, HashMap<String, Cooldown>> cooldowns = new HashMap<>();

    /**
     * @param uuid Entity UUID to check
     * @param group Cooldown group
     * @return true if entity can perform action
     */
    public static boolean isReady(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null) return true;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null || cooldown.isReady();
    }

    /**
     * Puts entity on cooldown
     * @param uuid Entity UUID
     * @param group Cooldown group
     * @param duration Cooldown duration in ms
     */
    public static void use(UUID uuid, String group, long duration) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(group, new Cooldown(duration));
    }

    /**
     * Get remaining cooldown time
     * @param uuid Entity UUID
     * @param group Cooldown group
     * @return remaining time in milliseconds, 0 if no cooldown
     */
    public static long getRemaining(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns == null) return 0;

        Cooldown cooldown = userCooldowns.get(group);
        return cooldown == null ? 0 : cooldown.getRemaining();
    }

    /**
     * Removes all expired cooldowns
     */
    public static void cleanup() {
        cooldowns.entrySet().removeIf(entry -> {
            entry.getValue().entrySet().removeIf(groupEntry -> groupEntry.getValue().isReady());
            return entry.getValue().isEmpty();
        });
    }

    /**
     * Removes all cooldowns for a specific entity
     * @param uuid Entity UUID
     */
    public static void clear(UUID uuid) {
        cooldowns.remove(uuid);
    }

    /**
     * Removes a specific cooldown group for an entity
     * @param uuid Entity UUID
     * @param group Cooldown group
     */
    public static void clear(UUID uuid, String group) {
        var userCooldowns = cooldowns.get(uuid);
        if (userCooldowns != null) {
            userCooldowns.remove(group);
            if (userCooldowns.isEmpty()) {
                cooldowns.remove(uuid);
            }
        }
    }
}