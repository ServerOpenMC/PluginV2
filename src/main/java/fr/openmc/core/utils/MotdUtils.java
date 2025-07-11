package fr.openmc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MotdUtils {
    private final YamlConfiguration motdConfig;
    private static Component motd;

    public MotdUtils() {
        File motdFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "motd.yml");

        if(!motdFile.exists()) {
            motdFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/motd.yml", false);
        }

        motdConfig = YamlConfiguration.loadConfiguration(motdFile);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<Map<?, ?>> motds = motdConfig.getMapList("motds");

                int randomIndex = new Random().nextInt(motds.size());
                Map<?, ?> motdData = motds.get(randomIndex);

                String line1 = (String) (motdData).get("line1");
                String line2 = (String) (motdData).get("line2");


                motd=Component.text(line1 + "\n" + line2);
                Bukkit.getServer().motd(motd);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 12000L); // 12000 ticks = 10 minutes
    }
}
