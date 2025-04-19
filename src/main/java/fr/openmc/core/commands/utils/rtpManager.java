package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class rtpManager {

    private final OMCPlugin plugin;
    private final File rtpFile;
    private FileConfiguration rtpConfig;
    @Getter private int minRadius;
    @Getter private int maxRadius;
    @Getter private int maxTries;
    @Getter private int RtpCooldown;
    @Getter static rtpManager instance;

    public rtpManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.rtpFile = new File(plugin.getDataFolder() + "/data", "rtp.yml");
        loadSpawnConfig();
    }

    private void loadSpawnConfig() {
        if(!rtpFile.exists()) {
            rtpFile.getParentFile().mkdirs();
            plugin.saveResource("data/rtp.yml", false);
        }

        rtpConfig = YamlConfiguration.loadConfiguration(rtpFile);
        this.maxRadius = rtpConfig.getInt("max-radius");
        this.minRadius = rtpConfig.getInt("min-radius");
        this.maxTries = rtpConfig.getInt("max-tries");
        this.RtpCooldown = rtpConfig.getInt("rtp-cooldown");
    }
}
