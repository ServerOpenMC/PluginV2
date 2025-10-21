package fr.openmc.core.listeners;

import fr.openmc.api.hooks.LuckPermsHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.displays.TabList;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class JoinQuitMessageListener implements Listener {
    private final double balanceOnJoin;
    
    public JoinQuitMessageListener() {
        this.balanceOnJoin = OMCPlugin.getInstance().getConfig().getDouble("money-on-first-join", 500D);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        MessagesManager.sendMessage(player, Component.text("Bienvenue sur OpenMC !"), Prefix.OPENMC, MessageType.INFO, false);

        TabList.updateTabList(player);

        FriendManager.getFriendsAsync(player.getUniqueId()).thenAccept(friendsUUIDS -> {
            for (UUID friendUUID : friendsUUIDS) {
                final Player friend = player.getServer().getPlayer(friendUUID);
                if (friend != null && friend.isOnline()) {
                    MessagesManager.sendMessage(friend, Component.text("§aVotre ami §r" + "§r" + LuckPermsHook.getFormattedPAPIPrefix(player) + player.getName() +" §as'est connecté(e)"), Prefix.FRIEND, MessageType.NONE, true);
                }
            }
        }).exceptionally(throwable -> {
            OMCPlugin.getInstance().getSLF4JLogger().error("An error occurred while loading friends of {} : {}", player.getName(), throwable.getMessage(), throwable);
            return null;
        });

        // Quest pending reward notification
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            for (Quest quest : QuestsManager.getAllQuests()) {
                if (!quest.hasPendingRewards(player.getUniqueId()))
                    continue;

                int pendingRewardsNumber = quest.getPendingRewardTiers(player.getUniqueId()).size();
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    MessagesManager.sendMessage(player,
                            Component.text("§aVous avez " + pendingRewardsNumber + " récompense(s) de quête en attente.")
                                    .append(Component.text(" §6Cliquez ici pour les récupérer."))
                                            .clickEvent(ClickEvent.runCommand("/quest")),
                            Prefix.QUEST,
                            MessageType.INFO,
                            true);
                });
            }
        });

        event.joinMessage(Component.text("§8[§a§l+§8] §r" + "§r" + LuckPermsHook.getFormattedPAPIPrefix(player) + player.getName()));

        // Adjust player's spawn location
        if (!player.hasPlayedBefore()) {
            player.teleport(SpawnManager.getSpawnLocation());
            EconomyManager.setBalance(player.getUniqueId(), this.balanceOnJoin);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                TabList.updateTabList(player);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 100L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> QuestsManager.saveQuests(player.getUniqueId()));

        FriendManager.getFriendsAsync(player.getUniqueId()).thenAccept(friendsUUIDS -> {
            for (UUID friendUUID : friendsUUIDS) {
                final Player friend = player.getServer().getPlayer(friendUUID);
                if (friend != null && friend.isOnline()) {
                    MessagesManager.sendMessage(friend, Component.text("§cVotre ami §e" + "§r" + LuckPermsHook.getFormattedPAPIPrefix(player) + player.getName() +" §cs'est déconnecté(e)"), Prefix.FRIEND, MessageType.NONE, true);
                }
            }
        }).exceptionally(throwable -> {
            OMCPlugin.getInstance().getSLF4JLogger().error("An error occurred while loading friends of {} : {}", player.getName(), throwable.getMessage(), throwable);
            return null;
        });

        if (TPAQueue.requesterHasPendingRequest(player)) {
            Player targetTPA = TPAQueue.getTargetByRequester(player);
            TPAQueue.removeRequest(player, targetTPA);
            MessagesManager.sendMessage(targetTPA, Component.text("§3La demande de téléportation de §6" + player.getName() + " §4a été annulée car il s'est déconnecté"), Prefix.OPENMC, MessageType.INFO, true);
        } else if (TPAQueue.hasPendingRequest(player)) {
            for (Player requester : TPAQueue.getRequesters(player)) {
                TPAQueue.removeRequest(requester, player);
                MessagesManager.sendMessage(requester, Component.text("§4Votre demande de téléportation à §6" + player.getName() + " §4a été annulée car il s'est déconnecté"), Prefix.OPENMC, MessageType.WARNING, true);
            }
        }


        event.quitMessage(Component.text("§8[§c§l-§8] §r" + "§r" + LuckPermsHook.getFormattedPAPIPrefix(player) + player.getName()));
    }

}
