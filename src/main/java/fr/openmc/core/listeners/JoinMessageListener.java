package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.scoreboards.TabList;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class JoinMessageListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        MessagesManager.sendMessage(player, Component.text("Bienvenue sur OpenMC !"), Prefix.OPENMC, MessageType.INFO, false);

        TabList.getInstance().updateTabList(player);

        FriendManager.getInstance().getFriendsAsync(player.getUniqueId()).thenAccept(friendsUUIDS -> {
            for (UUID friendUUID : friendsUUIDS) {
                final Player friend = player.getServer().getPlayer(friendUUID);
                if (friend != null && friend.isOnline()) {
                    MessagesManager.sendMessage(friend, Component.text("§aVotre ami §r" + "§r" + LuckPermsAPI.getFormattedPAPIPrefix(player) + player.getName() +" §as'est connecté(e)"), Prefix.FRIEND, MessageType.NONE, true);
                }
            }
        }).exceptionally(throwable -> {
            OMCPlugin.getInstance().getLogger().severe("An error occurred while loading friends of " + player.getName() + " : " + throwable.getMessage());
            return null;
        });

        event.joinMessage(Component.text("§8[§a§l+§8] §r" + "§r" + LuckPermsAPI.getFormattedPAPIPrefix(player) + player.getName()));

        // Adjust player's spawn location
        if (!player.hasPlayedBefore()) player.teleport(SpawnManager.getInstance().getSpawnLocation());


        new BukkitRunnable() {
            @Override
            public void run() {
                TabList.getInstance().updateTabList(player);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 100L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        QuestsManager.getInstance().saveQuests(player.getUniqueId());

        FriendManager.getInstance().getFriendsAsync(player.getUniqueId()).thenAccept(friendsUUIDS -> {
            for (UUID friendUUID : friendsUUIDS) {
                final Player friend = player.getServer().getPlayer(friendUUID);
                if (friend != null && friend.isOnline()) {
                    MessagesManager.sendMessage(friend, Component.text("§cVotre ami §e" + "§r" + LuckPermsAPI.getFormattedPAPIPrefix(player) + player.getName() +" §cs'est déconnecté(e)"), Prefix.FRIEND, MessageType.NONE, true);
                }
            }
        }).exceptionally(throwable -> {
            OMCPlugin.getInstance().getLogger().severe("An error occurred while loading friends of " + player.getName() + " : " + throwable.getMessage());
            return null;
        });

        event.quitMessage(Component.text("§8[§c§l-§8] §r" + "§r" + LuckPermsAPI.getFormattedPAPIPrefix(player) + player.getName()));
    }

}
