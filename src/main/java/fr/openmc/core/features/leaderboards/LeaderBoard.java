package fr.openmc.core.features.leaderboards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.world.entities.TextDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;

public abstract class LeaderBoard {

    @Getter
    private final String id;

    @Getter
    protected Location location;

    protected TextDisplay display;

    private final long updatePeriodTicks;
    private BukkitTask updateTask;

    protected LeaderBoard(String id, Location location, TextDisplay display, double updateTimeSeconds) {
        if (updateTimeSeconds <= 0) {
            throw new IllegalArgumentException("updateTimeSeconds must be greater than zero");
        }
        this.id = id;
        this.location = location;
        this.display = display;
        this.updatePeriodTicks = Math.max(1L, Math.round(updateTimeSeconds * 20));
    }

    public void update(){
        if(this.display != null)
            this.display.updateText(createComponent());
    }

    public abstract Component createComponent();

    public abstract void setLocation(Location location) throws IOException;

    public void start() {
        if (updateTask != null && !updateTask.isCancelled()) {
            return;
        }

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                update();
                updateViewers();
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, updatePeriodTicks);
    }

    public void updateViewers() {
        if (display != null) {
            this.display.updateViewersList();
        }
    }

    public void remove() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        if (display != null) {
            display.remove();
            display = null;
        }
    }

}
