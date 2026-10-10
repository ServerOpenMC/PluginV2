package fr.openmc.api.cooldown;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Represents a single cooldown with duration and last use time
 */
@DatabaseTable(tableName = "cooldowns")
@Getter
public class Cooldown {
    private final static DynamicCooldownManager DYNAMIC_COOLDOWN_MANAGER = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(uniqueCombo = true, canBeNull = false)
    private UUID uniqueId;
    @DatabaseField(uniqueCombo = true, canBeNull = false)
    private String group;
    @DatabaseField(canBeNull = false)
    private long duration;
    @DatabaseField(canBeNull = false)
    private long lastUse;
    private BukkitTask scheduledTask;

    Cooldown() {
        // required for ORMLite
    }

    /**
     * @param duration Cooldown duration in ms
     */
    public Cooldown(UUID cooldownUUID, String group, long duration, long lastUse) {
        this.duration = duration;
        this.lastUse = lastUse;
        this.uniqueId = cooldownUUID;
        this.group = group;

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CooldownStartEvent(this.uniqueId, this.group))
        );

        long delayTicks = getRemaining() / 50; //ticks

        this.scheduledTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new CooldownEndEvent(this.uniqueId, this.group))
            );
            DYNAMIC_COOLDOWN_MANAGER.clear(this.uniqueId, this.group, false);
        }, delayTicks);
    }

    public void cancelTask() {
        if (scheduledTask != null) scheduledTask.cancel();
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
