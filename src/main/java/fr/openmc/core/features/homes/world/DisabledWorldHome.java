package fr.openmc.core.features.homes.world;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

public class DisabledWorldHome extends Feature {

    private File file;
    private FileConfiguration config;
    private Map<String, WorldDisableInfo> disabledWorlds;

    @Override
    public void onEnable() {
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "disabled_world_home.yml");
        disabledWorlds = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                OMCLogger.error("Error while creating disabled worlds config: {}", e.getMessage(), e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadDisabledWorlds();
    }

    private void loadDisabledWorlds() {
        disabledWorlds.clear();
        ConfigurationSection sections = config.getConfigurationSection("disabled-worlds");
        if (sections != null) {
            for (String key : sections.getKeys(false)) {
                ConfigurationSection section = sections.getConfigurationSection(key);
                if (section != null) {
                    String addedBy = section.getString("added-by", "unknown");
                    long addedOn = section.getLong("added-on", 0);
                    disabledWorlds.put(key, new WorldDisableInfo(addedBy, addedOn));
                }
            }
        }
    }

    public void saveConfig() {
        OMCLogger.info("Saving disabled worlds config...");
        config.set("disabled-worlds", null);
        for (Map.Entry<String, WorldDisableInfo> entry : disabledWorlds.entrySet()) {
            String key = entry.getKey();
            WorldDisableInfo info = entry.getValue();
            config.set("disabled-worlds." + key + ".added-by", info.addedBy());
            config.set("disabled-worlds." + key + ".added-on", info.addedOn());
        }
        try {
            config.save(file);
        } catch (Exception e) {
            OMCLogger.error("Error while saving disabled worlds config: {}", e.getMessage(), e);
        }
    }

    public  void addDisabledWorld(World world, Player player) {
        if (!disabledWorlds.containsKey(world.getName())) {
            disabledWorlds.put(world.getName(), new WorldDisableInfo(player.getName(), System.currentTimeMillis()));
            saveConfig();
        }
    }

    public void removeDisabledWorld(World world) {
        if (disabledWorlds.remove(world.getName()) != null) {
            saveConfig();
        }
    }

    public boolean isDisabledWorld(World world) {
        return disabledWorlds.containsKey(world.getName());
    }

    public List<String> getDisabledWorlds() {
        return new ArrayList<>(disabledWorlds.keySet());
    }

    public Component getDisabledWorldInfo(String world) {
        WorldDisableInfo info = disabledWorlds.get(world);
        if (info != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return TranslationManager.translation(
                    "feature.homes.world.info",
                    Component.text(info.addedBy()).color(NamedTextColor.YELLOW),
                    Component.text(sdf.format(info.addedOn())).color(NamedTextColor.YELLOW)
            );
        }
        return Component.empty();
    }

}
