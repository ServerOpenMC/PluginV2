package fr.openmc.core.features.displays.holograms;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.milestones.tutorial.TutorialHologram;
import fr.openmc.core.utils.entities.TextDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HologramLoader {

    public static final HashMap<String, HologramInfo> displays = new HashMap<>();

    private static final File hologramFolder = new File(OMCPlugin.getInstance().getDataFolder(), "data/holograms");

    public HologramLoader() {
        hologramFolder.mkdirs();

        HologramLoader.registerHolograms(
                new TutorialHologram()
        );

        HologramLoader.loadAllFromFolder(hologramFolder);
    }

    public static void registerHolograms(Hologram... holograms) {
        for (Hologram hologram : holograms) {
            if (hologram == null) continue;

            File file = new File(hologramFolder, hologram.getName() + ".yml");

            if (!file.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                config.set("world", hologram.getLocation().getWorld().getName());
                config.set("x", hologram.getLocation().getX());
                config.set("y", hologram.getLocation().getY());
                config.set("z", hologram.getLocation().getZ());
                config.set("pitch", hologram.getLocation().getPitch());
                config.set("yaw", hologram.getLocation().getYaw());
                config.set("scale", hologram.getScale());
                config.set("line", Arrays.asList(hologram.getLines()));
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
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
        Location hologramLocation = hologramConfig.getLocation("");
        if (hologramLocation == null) {
            Bukkit.getLogger().warning("Hologram file " + file.getName() + " has invalid or missing location.");
            return;
        }

        float scale = (float) hologramConfig.getDouble("scale");
        List<String> lines = hologramConfig.getStringList("line");
        if (lines == null || lines.isEmpty()) return;

        Component component = null;

        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            System.out.println(rawLine);
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
        displays.clear();
    }

    public static void setHologramLocation(String hologramName, Location location) throws IOException {
        HologramInfo hologramInfo = displays.get(hologramName);
        FileConfiguration hologramConfig = YamlConfiguration.loadConfiguration(hologramInfo.file());
        hologramConfig.set("", location);
        hologramConfig.save(hologramInfo.file());
        loadAllFromFolder(hologramFolder);

        hologramInfo.display().setLocation(location);
    }
}
