package fr.openmc.core.features.homes.command;

import dev.xernas.menulib.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.menu.HomeMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class TpHome {

    private final HomesManager homesManager;
    public TpHome(HomesManager homesManager) {
        this.homesManager = homesManager;
    }

    @Command("home")
    @Description("Se téléporter à un home")
    @CommandPermission("omc.commands.home.teleport")
    @AutoComplete("@homes")
    public void home(Player player, @Optional String home) {

        if(home != null && home.contains(":") && player.hasPermission("omc.admin.homes.teleport.others")) {
            String[] split = home.split(":");
            String targetName = split[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if(!player.isConnected() && !target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, Component.text("§cCe joueur n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            List<Home> homes = HomesManager.getHomes(target.getUniqueId());


            if(split.length < 2) {
                if(homes.isEmpty()) {
                    MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home."), Prefix.HOME, MessageType.ERROR, true);
                    return;
                }

                Menu menu = new HomeMenu(player, target);
                menu.open();
                return;
            }

            for(Home h : homes) {
                if (h.getName().equalsIgnoreCase(split[1])) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.teleport(h.getLocation());
                                    MessagesManager.sendMessage(player, Component.text("§aVous avez été téléporté au home §e" + h.getName() + " §ade §e" + target.getName() + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                                }
                            }.runTaskLater(OMCPlugin.getInstance(), 10);
                        }
                    }.runTaskLater(OMCPlugin.getInstance(), 10);
                    return;
                }
            }

            MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        List<Home> homes = HomesManager.getHomes(player.getUniqueId());

        if(home == null || home.isBlank() || home.isEmpty()) {
            if(homes.isEmpty()) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de home."), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            Menu menu = new HomeMenu(player);
            menu.open();
            return;
        }

        for(Home h : homes) {
            if(h.getName().equalsIgnoreCase(home)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(h.getLocation());
                                MessagesManager.sendMessage(player, Component.text("§aVous avez été téléporté à votre home §e" + h.getName() + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                            }
                        }.runTaskLater(OMCPlugin.getInstance(), 10);
                    }
                }.runTaskLater(OMCPlugin.getInstance(), 10);
                return;
            }
        }

        MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de home avec ce nom."), Prefix.HOME, MessageType.ERROR, true);
    }

}
