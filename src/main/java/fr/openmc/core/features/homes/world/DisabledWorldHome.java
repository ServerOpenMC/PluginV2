package fr.openmc.core.features.homes.world;

import fr.openmc.core.OMCPlugin;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisabledWorldHome {

    private static File file;
    private static FileConfiguration config;
    private static Map<String, WorldDisableInfo> disabledWorlds;

    public static void init() {
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "disabled_world_home.yml");
        disabledWorlds = new HashMap<>();
        loadConfig();
    }

    public static void loadConfig() {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                OMCPlugin.getInstance().getSLF4JLogger().error("Error while creating disabled worlds config: {}", e.getMessage(), e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadDisabledWorlds();
    }

    private static void loadDisabledWorlds() {
        disabledWorlds.clear();
        ConfigurationSection sections = config.getConfigurationSection("disabled-worlds");
        if(sections != null) {
            for(String key : sections.getKeys(false)) {
                ConfigurationSection section = sections.getConfigurationSection(key);
                if(section != null) {
                    String addedBy = section.getString("added-by", "unknown");
                    long addedOn = section.getLong("added-on", 0);
                    disabledWorlds.put(key, new WorldDisableInfo(addedBy, addedOn));
                }
            }
        }
    }

    public static void saveConfig() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Saving disabled worlds config...");
        config.set("disabled-worlds", null);
        for(Map.Entry<String, WorldDisableInfo> entry : disabledWorlds.entrySet()) {
            String key = entry.getKey();
            WorldDisableInfo info = entry.getValue();
            config.set("disabled-worlds." + key + ".added-by", info.addedBy());
            config.set("disabled-worlds." + key + ".added-on", info.addedOn());
        }
        try {
            config.save(file);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error while saving disabled worlds config: {}", e.getMessage(), e);
        }
    }

    public static void addDisabledWorld(World world, Player player) {
        if(!disabledWorlds.containsKey(world.getName())) {
            disabledWorlds.put(world.getName(), new WorldDisableInfo(player.getName(), System.currentTimeMillis()));
            saveConfig();
        }
    }

    public static void removeDisabledWorld(World world) {
        if(disabledWorlds.remove(world.getName()) != null) {
            saveConfig();
        }
    }

    public static boolean isDisabledWorld(World world) {
        return disabledWorlds.containsKey(world.getName());
    }

    public static List<String> getDisabledWorlds() {
        return new ArrayList<>(disabledWorlds.keySet());
    }

    public static String getDisabledWorldInfo(String world) {
        WorldDisableInfo info = disabledWorlds.get(world);
        if(info != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return "§7Ajouté par §e" + info.addedBy() + " §7le §e" + sdf.format(info.addedOn());
        }
        return null;
    }

}
