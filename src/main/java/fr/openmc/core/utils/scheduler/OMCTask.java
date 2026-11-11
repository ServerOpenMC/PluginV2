package fr.openmc.core.utils.scheduler;

import fr.openmc.core.utils.text.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OMCTask {
    @Getter
    private final LocalDateTime target;
    @Getter
    private boolean cancelled = false;
    @Setter
    private BukkitTask current;

    public OMCTask(LocalDateTime target) {
        this.target = target;
    }

    public void cancel() {
        cancelled = true;
        BukkitTask task = current;
        if (task != null) task.cancel();
    }

    public long getRemainingSeconds() {
        return Math.max(0, DateUtils.getLocalDateTime().until(target, ChronoUnit.SECONDS));
    }
}
