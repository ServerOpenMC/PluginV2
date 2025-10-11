package fr.openmc.core.commands.utils;

import fr.openmc.api.cooldown.DynamicCooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;

public class RTPCommands {

    private final File rtpFile;
    private int minRadius;
    private int maxRadius;
    private int maxTries;
    private int rtpCooldown;

    public RTPCommands() {
        this.rtpFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "rtp.yml");
        loadRTPConfig();
    }

    private void loadRTPConfig() {
        if (!rtpFile.exists()) {
            rtpFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/rtp.yml", false);
        }

        FileConfiguration rtpConfig = YamlConfiguration.loadConfiguration(rtpFile);
        this.maxRadius = rtpConfig.getInt("max-radius");
        this.minRadius = rtpConfig.getInt("min-radius");
        this.maxTries = rtpConfig.getInt("max-tries");
        this.rtpCooldown = rtpConfig.getInt("rtp-cooldown");
    }

    @Command("rtp")
    @Description("Permet de se téléporter à un endroit aléatoire")
    @CommandPermission("omc.commands.rtp")
    @DynamicCooldown(
            group="player:rtp",
            message = "§cTu dois attendre avant de pouvoir te rtp (%formatTime%)")
    public void rtp(Player player) {
        if (DynamicCooldownManager.isReady(player.getUniqueId(), "player:rtp")) {
            DynamicCooldownManager.use(player.getUniqueId(), "player:rtp", rtpCooldown * 1000L); // Pour être sûr que le jouer ne réexécute pas la commande avant qu'elle soit finie
            rtpPlayer(player, 0);
        }
    }

    private void rtpPlayer(Player player, int tries) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tryRtp(player))
                    return;

                if ((tries + 1) >= maxTries) {
                    // On a déjà mis le cooldown au début
	                player.sendActionBar(Component.text("Échec du RTP. Réessayez plus tard..."));
                    return;
                }

                player.sendActionBar(Component.text("RTP: Tentative " + (tries + 1) + "/" + maxTries + " §cÉchec§r..."));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        rtpPlayer(player, tries + 1);
                    }
                }.runTaskLaterAsynchronously(OMCPlugin.getInstance(), 20);
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());

    }

    public boolean tryRtp(Player player) {
        Location loc = generateRandomLocation(player.getWorld());
        if (isSafe(loc)) {
            loc.add(0.5, 1, 0.5);
            tpPlayer(player, loc);
            return true;
        } else {
            return false;
        }
    }

    public boolean isSafe(Location loc) {
        return loc.getBlock().isSolid() && loc.getBlockY() > 50;
    }

    public Location generateRandomLocation(World world) {
        int radius = (int) (Math.random() * (maxRadius - minRadius + 1)) + minRadius;
        float angle = (float) (Math.random() * 2 * Math.PI);
        int x = (int) (Math.cos(angle) * radius);
        int z = (int) (Math.sin(angle) * radius);
        return world.getHighestBlockAt(x, z).getLocation();
    }

    public void tpPlayer(Player player, Location loc) {
        PlayerUtils.sendFadeTitleTeleport(player, loc);
        MessagesManager.sendMessage(player, Component.text("§aVous avez été téléporté à §6X: §e" + loc.getBlockX() + "§6, Y: §e" + loc.getBlockY() + "§6, Z: §e" + loc.getBlockZ()), Prefix.OPENMC, MessageType.SUCCESS, true);
    }

}
