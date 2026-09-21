package fr.openmc.core.features.leaderboards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.utils.world.entities.TextDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;

import java.io.IOException;

import static fr.openmc.core.features.leaderboards.LeaderBoardManager.reload;

public abstract class LeaderBoard {

    public static final FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());

    @Getter
    protected Location location;

    protected TextDisplay display;

    private final long updatePeriodTicks;
    private BukkitTask updateTask;

    protected LeaderBoard() {
        if (getUpdateDelay() <= 0)
            throw new IllegalArgumentException("updateTimeSeconds must be greater than zero");
        this.updatePeriodTicks = Math.max(1L, Math.round(getUpdateDelay() * 20));
        boolean changed = false;

        if (!config.contains("scale")) {
            config.set("scale", 0.75D);
            changed = true;
        }

        String locationKey = getId() + "-location";
        this.location = config.getLocation(locationKey);
        if (this.location == null) {
            Location defaultLocation = getDefaultLocation();
            if (defaultLocation != null) {
                this.location = defaultLocation;
                config.set(locationKey, defaultLocation);
                changed = true;
            } else {
                OMCLogger.warn("Can't initialize location for " + getId() + ": no world is loaded");
            }
        }

        if (changed) {
            try {
                config.save(LeaderBoardManager.getLeaderBoardFile());
            } catch (IOException e) {
                throw new IllegalStateException("Can't save leaderboard config", e);
            }
        }
    }

    private static Location getDefaultLocation() {
        World world = Bukkit.getWorld("world");
        if (world == null && !Bukkit.getWorlds().isEmpty())
            world = Bukkit.getWorlds().getFirst();
        return world == null ? null : new Location(world, 0, 0, 0);
    }

    private static float getScale() {
        return (float) config.getDouble("scale", 0.75D);
    }

    public abstract double getUpdateDelay();

    public abstract String getId();

    public abstract Component createComponent();

    public void setLocation(Location location) throws IOException{
        this.location = location;
        config.set(getId() + "-location", location);
        config.save(LeaderBoardManager.getLeaderBoardFile());
        if(this.display != null)
            this.display.setLocation(location);
    }

    public void update(){
        if(this.display != null)
            this.display.updateText(createComponent());
    }

    public void start() {
        if ((updateTask != null && !updateTask.isCancelled()) || this.location == null) return;

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, updatePeriodTicks);
        this.display = new TextDisplay(createComponent(), this.location, new Vector3f(getScale()));
    }

    public void updateViewers() {
        if (display != null) this.display.updateViewersList();
    }

    public void refreshViewer(org.bukkit.entity.Player viewer) {
        if (display != null) display.refreshViewer(viewer);
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

    public static void setScale(float scale) throws IOException {
        config.set("scale", scale);
        config.save(LeaderBoardManager.getLeaderBoardFile());
        reload();
    }

}
