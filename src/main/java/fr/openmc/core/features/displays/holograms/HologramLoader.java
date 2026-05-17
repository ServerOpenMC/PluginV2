package fr.openmc.core.features.displays.holograms;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.features.types.NotInUnitTest;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.displays.holograms.commands.HologramCommand;
import fr.openmc.core.features.milestones.tutorial.TutorialHologram;
import fr.openmc.core.utils.world.entities.TextDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Credit(developers = {"iambibi_", "miseur"})
public class HologramLoader extends Feature implements NotInUnitTest, LoadAfterItemsAdder, HasCommands {

    public static final HashMap<String, HologramInfo> displays = new HashMap<>();
    private static BukkitTask taskTimer;
    public static File hologramFolder;

    public static File getHologramFolder() {
        if (hologramFolder == null) {
            OMCPlugin plugin = OMCPlugin.getInstance();
            if (plugin == null) {
                throw new IllegalStateException("OMCPlugin instance not initialized");
            }
            hologramFolder = new File(plugin.getDataFolder(), "data/holograms");
        }
        return hologramFolder;
    }

    @Override
    public void init() {
        File hologramFolder = getHologramFolder();
        hologramFolder.mkdirs();

        HologramLoader.registerHolograms(
                new TutorialHologram()
        );

        updateHologramsViewers();
        HologramLoader.loadAllFromFolder(hologramFolder);
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new HologramCommand()
        );
    }

    @Override
    public void save() {
        HologramLoader.unloadAll();
    }

    public static void updateHologramsViewers() {
        taskTimer = new BukkitRunnable() {
            @Override
            public void run() {
                displays.values().forEach(hologramInfo -> {
                    TextDisplay display = hologramInfo.display();
                    display.updateViewersList();
                });
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0, 20L); // Toutes les 15 secondes en async sauf l'updateGithubContributorsMap qui est toutes les 30 minutes
    }

    public static void registerHolograms(Hologram... holograms) {
        File hologramFolder = getHologramFolder();
        for (Hologram hologram : holograms) {
            if (hologram == null) continue;

            File file = new File(hologramFolder, hologram.getName() + ".yml");

            if (!file.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                config.set("location", hologram.getLocation());
                config.set("scale", hologram.getScale());
                config.set("line", Arrays.asList(hologram.getLines()));
                try {
                    config.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Component component = null;
            for (int i = 0; i < hologram.getLines().length; i++) {
                String rawLine = hologram.getLines()[i];

                if (component == null) {
                    component = Component.text(rawLine);
                } else {
                    component = component.append(Component.newline()).append(Component.text(rawLine));
                }
            }

            displays.put(hologram.getName(), new HologramInfo(
                    file,
                    new TextDisplay(component, hologram.getLocation(), new Vector3f(hologram.getScale()))
            ));
        }

    }

    public static void loadAllFromFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) return;
        for (File file : Objects.requireNonNull(folder.listFiles((f) -> f.getName().endsWith(".yml")))) {
            loadHologramFromFile(file);
        }
    }

    private static void loadHologramFromFile(File file) {
        FileConfiguration hologramConfig = YamlConfiguration.loadConfiguration(file);
        String hologramName = file.getName().replace(".yml", "");
        Location hologramLocation = hologramConfig.getLocation("location");
        if (hologramLocation == null) {
            OMCLogger.warn("Hologram {} has no location set in its config file, skipping.", hologramName);
            return;
        }

        float scale = (float) hologramConfig.getDouble("scale");
        List<String> lines = hologramConfig.getStringList("line");
        if (lines.isEmpty()) return;

        Component component = null;

        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            if (component == null) {
                component = Component.text(rawLine);
            } else {
                component = component.append(Component.newline()).append(Component.text(rawLine));
            }
        }
        TextDisplay display = new TextDisplay(component, hologramLocation, new Vector3f(scale));
        displays.put(hologramName, new HologramInfo(file, display));
    }

    public static void unloadAll() {
        for (HologramInfo info : displays.values()) {
            info.display().remove();
        }
        taskTimer.cancel();
        displays.clear();
    }

    public static void setHologramLocation(String hologramName, Location location) throws IOException {
        File hologramFolder = getHologramFolder();
        HologramInfo hologramInfo = displays.get(hologramName);
        FileConfiguration hologramConfig = YamlConfiguration.loadConfiguration(hologramInfo.file());
        hologramConfig.set("location", location);
        hologramConfig.save(hologramInfo.file());
        loadAllFromFolder(hologramFolder);

        hologramInfo.display().setLocation(location);
    }
}
