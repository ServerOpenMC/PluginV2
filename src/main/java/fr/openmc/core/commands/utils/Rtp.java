package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.cooldown.DynamicCooldown;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Rtp {
    @Command("rtp")
    @Description("Permet de se téléporter à un endroit aléatoire")
    @CommandPermission("omc.commands.rtp")
    @DynamicCooldown(group = "rpt")
    public void rtp(CommandSender sender) {

        if (sender instanceof Player player) {
            World world = player.getWorld();
            int minRadius = rtpManager.getInstance().getMinRadius();
            int maxRadius = rtpManager.getInstance().getMaxRadius();
            int maxTries = rtpManager.getInstance().getMaxTries();
            int rtpCooldown = rtpManager.getInstance().getRtpCooldown();

            boolean result = rtpPlayer(player);
            if (!result) {
                player.sendActionBar("RTP: Tentative 1/" + maxTries + " §cÉchec§r...");
                for (int i = 1; i < maxTries; i++) {
                    // Attendre une seconde avant de réessayer
                    try {
                        Thread.sleep(1000); // C'est un tread asynchrone, donc pas de problème (normalement)
                    } catch (InterruptedException e) {
                        Bukkit.getLogger().severe(e.getMessage());
                    }
                    result = rtpPlayer(player);
                    if (!result) {
                        player.sendActionBar("RTP: Tentative " + i + "/" + maxTries + " §cÉchec§r...");
                    } else {
                        DynamicCooldownManager.use(player.getUniqueId(), "rpt", rtpCooldown);
                        return;
                    }

                }
            } else {
                DynamicCooldownManager.use(player.getUniqueId(), "rpt", rtpCooldown);
                return;
            }
            player.sendActionBar("Échec du RTP réessayez plus tard...");
            DynamicCooldownManager.use(player.getUniqueId(), "rpt", 15);
        }
    }
    public boolean rtpPlayer(Player player) {
        World world = player.getWorld();
        int minRadius = rtpManager.getInstance().getMinRadius();
        int maxRadius = rtpManager.getInstance().getMaxRadius();
        int maxTries = rtpManager.getInstance().getMaxTries();
        int rtpCooldown = rtpManager.getInstance().getRtpCooldown();
        int radius = (int) (Math.random() * (maxRadius - minRadius + 1)) + minRadius;
        float angle = (float) (Math.random() * 2 * Math.PI);
        int x = (int) (Math.cos(angle) * radius);
        int z = (int) (Math.sin(angle) * radius);
        Location loc = world.getHighestBlockAt(x, z).getLocation();

        if (loc.getBlock().isSolid() && loc.getBlockY() > 0) {
            loc.setY(loc.getY() + 1);
            player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(loc);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(player, "§aVous avez été téléporté à §6X: §e" + loc.getBlockX() + "§6, Y:§e" + loc.getBlockY() + "§6, Z: §e" + loc.getBlockZ()));
                }
            }.runTaskLater(OMCPlugin.getInstance(), 10);
            return true;
        } else {
            return false;
        }
    }

}
