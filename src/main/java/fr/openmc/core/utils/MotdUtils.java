package fr.openmc.core.utils;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MotdUtils {
    String title = "§d§lOPENMC V2 §r§8- §7Le serveur Open Source §8- §d1.21.1";

    private final List<Component> motdList = Arrays.asList(
            Component.text(title + "\n                      §3§lTOWN UPDATE                      "),
            Component.text(title + "\n                      §3§lV2 UPDATE                      ")
            // oublier pas de changer le motd en fonction des updates qu'il y a (grosse feature ect)
    );

    public void startMOTD(JavaPlugin plugin) {

        new BukkitRunnable() {
            @Override
            public void run() {
                int randomIndex = new Random().nextInt(0, 1);

                Bukkit.getServer().motd(motdList.get(randomIndex));
            }
        }.runTaskTimer(plugin, 0L, 12000L); // 12000 ticks = 10 minutes
    }
}
