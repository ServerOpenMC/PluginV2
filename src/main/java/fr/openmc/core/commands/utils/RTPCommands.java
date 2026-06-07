package fr.openmc.core.commands.utils;

import fr.openmc.api.cooldown.DynamicCooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

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
            group = "player:rtp",
            messageKey = "command.utils.rtp.must_wait")
    @Cooldown(15)
    public void rtp(Player player) {
        if (DynamicCooldownManager.isReady(player.getUniqueId(), "player:rtp")) {
            rtpPlayer(player, 0);
        }
    }

    private void rtpPlayer(Player player, int tries) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            int[] coords = generateRandomCoords();
            int chunkX = coords[0] >> 4;
            int chunkZ = coords[1] >> 4;

            World world = player.getWorld();

            world.getChunkAtAsync(chunkX, chunkZ).thenAccept(_ -> {
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    Location loc = world.getHighestBlockAt(coords[0], coords[1]).getLocation();

                    if (isSafe(loc)) {
                        loc.add(0.5, 1, 0.5);
                        tpPlayer(player, loc);
                        return;
                    }

                    int nextTry = tries + 1;
                    if (nextTry >= maxTries) {
                        // On a déjà mis le cooldown au début
                        player.sendActionBar(TranslationManager.translation("command.utils.rtp.fail"));
                        return;
                    }

                    player.sendActionBar(TranslationManager.translation("command.utils.rtp.try",
                            Component.text(nextTry),
                            Component.text(maxTries)));

                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(),
                            () -> rtpPlayer(player, nextTry), 20L);
                });
            });
        });
    }

    private int[] generateRandomCoords() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int radius = random.nextInt(minRadius, maxRadius + 1);
        double angle = random.nextDouble() * 2 * Math.PI;
        int x = (int) (Math.cos(angle) * radius);
        int z = (int) (Math.sin(angle) * radius);
        return new int[]{x, z};
    }

    private boolean isSafe(Location loc) {
        return loc.getBlock().isSolid() && loc.getBlockY() > 50;
    }

    private void tpPlayer(Player player, Location loc) {
        PlayerUtils.sendFadeTitleTeleport(player, loc);
        MessagesManager.sendMessage(player, TranslationManager.translation("command.utils.rtp.success",
                        Component.text(loc.getBlockX()).color(YELLOW),
                        Component.text(loc.getBlockY()).color(YELLOW),
                        Component.text(loc.getBlockZ()).color(NamedTextColor.YELLOW)
                ).color(NamedTextColor.GREEN), Prefix.OPENMC, MessageType.SUCCESS, true);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                DynamicCooldownManager.use(player.getUniqueId(), "player:rtp", rtpCooldown * 1000L)
        );
    }
}
