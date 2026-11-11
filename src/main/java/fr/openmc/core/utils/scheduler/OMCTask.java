package fr.openmc.core.utils.scheduler;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;

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
}
