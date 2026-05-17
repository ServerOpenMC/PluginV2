package fr.openmc.core.features.friend.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.friend.commands.autocomplete.FriendsAutoComplete;
import fr.openmc.core.features.friend.commands.autocomplete.FriendsRequestAutoComplete;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;

import javax.annotation.Syntax;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Command({"friends", "friend", "ami", "f"})
public class FriendCommand {

    @Subcommand("add")
    @Description("Envoyer une demande d'ami")
    public void addCommand(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player target
    ) {
        try {
            if (player.getUniqueId().equals(target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.add.self"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            if (!PlayerSettingsManager.canReceiveFriendRequest(target.getUniqueId(), player.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.add.disabled"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            if (FriendManager.isRequestPending(target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.add.already_sent"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            if (FriendManager.areFriends(player.getUniqueId(), target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.add.already_friend"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            FriendManager.addRequest(player.getUniqueId(), target.getUniqueId());
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.friend.add.sent",
                            Component.text(target.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.FRIEND,
                    MessageType.INFO,
                    true
            );

            Component acceptButton = Component.text(" [Accepter]", NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand("/friend accept " + player.getName()))
                    .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour accepter la demande d'ami", NamedTextColor.GREEN)));

            Component ignoreButton = Component.text(" [Ignorer]", NamedTextColor.GRAY)
                    .clickEvent(ClickEvent.callback(audience -> {
                        if (!FriendManager.isRequestPending(player.getUniqueId())) {
                            MessagesManager.sendMessage(target, TranslationManager.translation("feature.friend.request.expired"), Prefix.FRIEND, MessageType.INFO, true);
                            return;
                        }
                        FriendManager.removeRequest(FriendManager.getRequest(player.getUniqueId()));
                        MessagesManager.sendMessage(
                                target,
                                TranslationManager.translation(
                                        "feature.friend.request.ignored",
                                        Component.text(player.getName()).color(NamedTextColor.YELLOW)
                                ),
                                Prefix.FRIEND,
                                MessageType.INFO,
                                true
                        );
                    }))
                    .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour ignorer la demande d'ami", NamedTextColor.GRAY)));

            Component denyButton = Component.text(" [Refuser]", NamedTextColor.RED)
                    .clickEvent(ClickEvent.runCommand("/friend deny " + player.getName()))
                    .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour refuser la demande d'ami", NamedTextColor.RED)));

            MessagesManager.sendMessage(
                    target,
                    TranslationManager.translation(
                            "feature.friend.request.received",
                            Component.text(player.getName()).color(NamedTextColor.YELLOW)
                    ).append(acceptButton).append(ignoreButton).append(denyButton),
                    Prefix.FRIEND,
                    MessageType.INFO,
                    true
            );
        } catch (Exception e) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.add.error"), Prefix.FRIEND, MessageType.ERROR, true);
            throw new RuntimeException(e);
        }
    }

    @Subcommand("remove")
    @Description("Supprimer un ami de votre liste")
    public void removeCommand(
            Player player,
            @Named("ami") @SuggestWith(FriendsAutoComplete.class) String targetName
    ) {
        try {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.player_not_found"), Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            if (!FriendManager.areFriends(player.getUniqueId(), target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.remove.not_friend"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            if (!FriendManager.removeFriend(player.getUniqueId(), target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.remove.error"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.friend.remove.success",
                            Component.text(target.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.FRIEND,
                    MessageType.INFO,
                    true
            );
            if (target instanceof Player targetPlayer && targetPlayer.isOnline()) {
                MessagesManager.sendMessage(
                        targetPlayer,
                        TranslationManager.translation(
                                "feature.friend.remove.removed_by",
                                Component.text(player.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.FRIEND,
                        MessageType.INFO,
                        true
                );
            }
        } catch (Exception e) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.remove.error"), Prefix.FRIEND, MessageType.ERROR, true);
            throw new RuntimeException(e);
        }
    }

    @Subcommand("list")
    @Description("Afficher la liste de vos amis")
    @Syntax("[page]")
    public void listCommand(
            Player player,
            @Named("page") @Optional Integer page
    ) {
        int currentPage = (page != null && page > 0) ? page : 1;
        final int ITEMS_PER_PAGE = 7;

        FriendManager.getFriendsAsync(player.getUniqueId()).thenAccept(friends -> {
            if (friends.isEmpty()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.list.none"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }

            List<UUID> friendsList = new ArrayList<>(friends);
            int totalPages = (int) Math.ceil((double) friendsList.size() / ITEMS_PER_PAGE);

            if (currentPage > totalPages) {
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.friend.list.page_invalid",
                                Component.text(currentPage).color(NamedTextColor.RED),
                                Component.text(totalPages).color(NamedTextColor.RED)
                        ),
                        Prefix.FRIEND,
                        MessageType.ERROR,
                        true
                );
                return;
            }

            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, friendsList.size());

            Component header = Component.text("  ✦ Liste d'amis (" + currentPage + "/" + totalPages + ") ✦  ")
                    .color(NamedTextColor.GOLD);
            player.sendMessage(header);
            for (int i = startIndex; i < endIndex; i++) {
                UUID friendUUID = friendsList.get(i);
                OfflinePlayer friend = CacheOfflinePlayer.getOfflinePlayer(friendUUID);

                try {
                    Timestamp timestamp = FriendManager.getTimestamp(player.getUniqueId(), friend.getUniqueId());
                    String formattedDate = getFormattedDate(timestamp);

                    boolean isOnline = friend.isOnline();

                    City city = CityManager.getPlayerCity(friend.getUniqueId());
                    String formattedMoney = EconomyManager.getFormattedBalance(friend.getUniqueId());
                    Component cityComponent = Component.text(city != null ? city.getName() : "Aucune").color(NamedTextColor.YELLOW);
                    Component moneyComponent = Component.text(formattedMoney).color(NamedTextColor.YELLOW);
                    Component statusComponent = isOnline
                            ? Component.text("En ligne").color(NamedTextColor.GREEN)
                            : Component.text("Hors ligne").color(NamedTextColor.RED);

                    TextComponent friendComponent = Component.text("  " + (i+1) + ". ")
                            .color(NamedTextColor.GRAY)
                            .append(Component.text(friend.getName())
                                    .color(isOnline ? NamedTextColor.GREEN : NamedTextColor.YELLOW)
                                    .decoration(TextDecoration.BOLD, isOnline))
                            .hoverEvent(HoverEvent.showText(
                                    TranslationManager.translation(
                                            "feature.friend.list.hover",
                                            cityComponent,
                                            moneyComponent,
                                            statusComponent
                                    )))
                            ;

                    Component statusIcon = isOnline
                            ? Component.text(" ⬤ ").color(NamedTextColor.GREEN)
                            : Component.text(" ⬤ ").color(NamedTextColor.RED);
                    
                    Component dateInfo = Component.text("Depuis le " + formattedDate)
                            .color(NamedTextColor.GRAY);

                    Component actions = Component.text(" [✖]")
                            .color(NamedTextColor.RED)
                            .clickEvent(ClickEvent.runCommand("/friends remove " + friend.getName()))
                            .hoverEvent(HoverEvent.showText(Component.text("Supprimer cet ami", NamedTextColor.RED)));

                    player.sendMessage(friendComponent.append(statusIcon).append(dateInfo).append(actions));

                } catch (Exception e) {
                    player.sendMessage(Component.text("Erreur lors de la récupération des informations de " + friend.getName())
                            .color(NamedTextColor.RED));
                    throw new RuntimeException(e);
                }
            }

            if (totalPages > 1) {
                Component navigation = Component.empty();

                if (currentPage > 1) {
                    navigation = navigation.append(
                            Component.text(" « Page précédente ")
                                    .color(NamedTextColor.AQUA)
                                    .clickEvent(ClickEvent.runCommand("/friends list " + (currentPage - 1)))
                                    .hoverEvent(HoverEvent.showText(Component.text("Page " + (currentPage - 1))))
                    );
                } else {
                    navigation = navigation.append(
                            Component.text(" « Page précédente ")
                                    .color(NamedTextColor.DARK_GRAY)
                    );
                }

                navigation = navigation.append(Component.text(" | ").color(NamedTextColor.GRAY));

                if (currentPage < totalPages) {
                    navigation = navigation.append(
                            Component.text("Page suivante » ")
                                    .color(NamedTextColor.AQUA)
                                    .clickEvent(ClickEvent.runCommand("/friends list " + (currentPage + 1)))
                                    .hoverEvent(HoverEvent.showText(Component.text("Page " + (currentPage + 1))))
                    );
                } else {
                    navigation = navigation.append(
                            Component.text("Page suivante » ")
                                    .color(NamedTextColor.DARK_GRAY)
                    );
                }

                player.sendMessage(navigation);
            }
        }).exceptionally(ex -> {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.list.error"), Prefix.FRIEND, MessageType.ERROR, true);
            throw new RuntimeException(ex);
        });
    }

    @Subcommand("accept")
    @Description("Accepter une demande d'ami")
    public void acceptCommand(
            Player player,
            @Named("ami") @SuggestWith(FriendsRequestAutoComplete.class) String targetName
    ) {
        try {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.player_not_found"), Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            if (!FriendManager.isRequestPending(target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.request.not_received"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            FriendManager.addFriend(player.getUniqueId(), target.getUniqueId());
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.friend.request.accepted",
                            Component.text(target.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.FRIEND,
                    MessageType.INFO,
                    true
            );
            if (target instanceof Player targetPlayer && targetPlayer.isOnline()) {
                MessagesManager.sendMessage(
                        targetPlayer,
                        TranslationManager.translation(
                                "feature.friend.request.accepted",
                                Component.text(player.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.FRIEND,
                        MessageType.INFO,
                        true
                );
            }
        } catch (Exception e) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.request.accept_error"), Prefix.FRIEND, MessageType.ERROR, true);
            throw new RuntimeException(e);
        }
    }

    @Subcommand("deny")
    @Description("Refuser une demande d'ami")
    public void denyCommand(
            Player player,
            @Named("demande d'ami") @SuggestWith(FriendsRequestAutoComplete.class) String targetName
    ) {
        try {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (!target.hasPlayedBefore()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.player_not_found"), Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            if (!FriendManager.isRequestPending(target.getUniqueId())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.request.not_received"), Prefix.FRIEND, MessageType.ERROR, true);
                return;
            }
            FriendManager.removeRequest(FriendManager.getRequest(target.getUniqueId()));
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.friend.request.denied",
                            Component.text(target.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.FRIEND,
                    MessageType.INFO,
                    true
            );
            if (target instanceof Player targetPlayer && targetPlayer.isOnline()) {
                MessagesManager.sendMessage(
                        targetPlayer,
                        TranslationManager.translation(
                                "feature.friend.request.denied_by",
                                Component.text(player.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.FRIEND,
                        MessageType.INFO,
                        true
                );
            }
        } catch (Exception e) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.friend.request.deny_error"), Prefix.FRIEND, MessageType.ERROR, true);
            throw new RuntimeException(e);
        }
    }

    public String getFormattedDate(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(timestamp.getTime());
        return sdf.format(date);
    }
}
