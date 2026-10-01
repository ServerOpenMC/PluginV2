package fr.openmc.core.listeners;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.displays.TabList;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.hooks.LuckPermsHook;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;
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
        final OMCPlayer player = OMCPlayer.of(event.getPlayer());

        player.message().send(TranslationManager.translation("core.player.join.welcome"), Prefix.OPENMC, MessageType.INFO, false);

        TabList.updateTabList(player);

        FriendManager.getFriendsAsync(player.getUniqueId()).thenAccept(friendsUUIDS -> {
            for (UUID friendUUID : friendsUUIDS) {
                final Player friend = Bukkit.getPlayer(friendUUID);
                if (friend != null && friend.isOnline() && !friend.hasMetadata(OMCPlugin.VANISH_META_KEY)) {
                    player.message().send(TranslationManager.translation(
                            "core.player.join.friend_online",
                            Component.object(ObjectContents.playerHead(player.getUniqueId())).color(NamedTextColor.WHITE)
                                    .appendSpace()
                                    .append(Component.text(LuckPermsHook.getFormattedPAPIPrefix(player) + player.getName()).color(NamedTextColor.GREEN))
                    ), Prefix.FRIEND, MessageType.NONE, true);
                }
            }
        }).exceptionally(throwable -> {
            OMCLogger.error("An error occurred while loading friends of {} : {}", player.getName(), throwable.getMessage(), throwable);
            return null;
        });

        // Quest pending reward notification
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            for (Quest quest : QuestsManager.getAllQuests()) {
                if (!quest.hasPendingRewards(player.getUniqueId()))
                    continue;

                int pendingRewardsNumber = quest.getPendingRewardTiers(player.getUniqueId()).size();
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    player.message().send(
                            TranslationManager.translation("core.player.join.quest_reward", Component.text(pendingRewardsNumber))
                                    .append(Component.text(" "))
                                    .append(TranslationManager.translation("core.player.join.quest_reward_click"))
                                    .clickEvent(ClickEvent.runCommand("/quest")),
                            Prefix.QUEST,
                            MessageType.INFO,
                            true);
                });
            }

            GitHubHook.refreshContributorId(player.getUniqueId());
        });

        if (!player.hasMetadata(OMCPlugin.VANISH_META_KEY))
            event.joinMessage(Component.text("§8[§a§l+§8] §r")
                    .append(Component.object(ObjectContents.playerHead(player.getUniqueId())).color(NamedTextColor.WHITE))
                    .appendSpace()
                    .append(Component.text(LuckPermsHook.getFormattedPAPIPrefix(player)))
                    .append(Component.text(player.getName()))
            );

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
        final OMCPlayer player = OMCPlayer.of(event.getPlayer());

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> QuestsManager.saveQuests(player.getUniqueId()));

        FriendManager.getFriendsAsync(player.getUniqueId()).thenAccept(friendsUUIDS -> {
            for (UUID friendUUID : friendsUUIDS) {
                final Player friend = Bukkit.getPlayer(friendUUID);
                if (friend != null && friend.isOnline() && !friend.hasMetadata(OMCPlugin.VANISH_META_KEY)) {
                    MessagesManager.sendMessage(friend, TranslationManager.translation(
                            "core.player.quit.friend_offline",
                            Component.object(ObjectContents.playerHead(player.getUniqueId())).color(NamedTextColor.WHITE)
                                    .appendSpace()
                                    .append(Component.text(LuckPermsHook.getFormattedPAPIPrefix(player) + player.getName())).color(NamedTextColor.YELLOW)
                    ), Prefix.FRIEND, MessageType.NONE, true);
                }
            }
        }).exceptionally(throwable -> {
            OMCLogger.error("An error occurred while loading friends of {} : {}", player.getName(), throwable.getMessage(), throwable);
            return null;
        });

        if (TPAManager.requesterHasPendingRequest(player)) {
            OMCPlayer targetTPA = TPAManager.getTargetByRequester(player);
            if (targetTPA == null) return;
            TPAManager.removeRequest(player, targetTPA);
            MessagesManager.sendMessage(targetTPA, TranslationManager.translation(
                    "core.player.tpa.expired_target",
                    targetTPA.getNameWithHead().color(NamedTextColor.GOLD)
            ), Prefix.OPENMC, MessageType.INFO, true);
        } else if (TPAManager.hasPendingRequest(player)) {
            for (OMCPlayer requester : TPAManager.getRequesters(player)) {
                TPAManager.removeRequest(requester, player);
                MessagesManager.sendMessage(requester, TranslationManager.translation(
                        "core.player.tpa.expired_requester",
                        requester.getNameWithHead().color(NamedTextColor.GOLD)
                ), Prefix.OPENMC, MessageType.WARNING, true);
            }
        }

        if (!player.hasMetadata(OMCPlugin.VANISH_META_KEY))
            event.quitMessage(Component.text("§8[§c§l-§8] §r")
                    .append(Component.object(ObjectContents.playerHead(player.getUniqueId())).color(NamedTextColor.WHITE))
                    .appendSpace()
                    .append(Component.text(LuckPermsHook.getFormattedPAPIPrefix(player)))
                    .append(Component.text(player.getName()))
            );
    }
}
