package fr.openmc.core.features.city.commands;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.autocomplete.InvitedAutoComplete;
import fr.openmc.core.features.city.commands.autocomplete.InviterAutoComplete;
import fr.openmc.core.features.city.conditions.CityInviteConditions;
import fr.openmc.core.features.city.models.CityInvite;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class CityInviteCommands {
    public static final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();

    @Command("city invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    public static void invite(
            OMCPlayer sender,
            @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) OMCPlayer target
    ) {
        City city = City.ofPlayer(sender);

        if (!CityInviteConditions.canCityInvitePlayer(city, sender, target)) return;

        cityManager.addInvitation(target.getUniqueId(), new CityInvite(sender.getUniqueId(), city));

        sender.message().sendSuccess(
                TranslationManager.translation("feature.city.invite.commands.invite.success", target.getNameWithHead())
                        .appendNewline()
                        .append(TranslationManager.translation("feature.city.invite.commands.invite.cancel")),
                Prefix.CITY
        );

        target.message().sendInfo(
                TranslationManager.translation(
                                "feature.city.invite.commands.invite.received",
                                sender.getNameWithHead(),
                                Component.text(city.getName())
                        )
                        .appendNewline()
                        .append(TranslationManager.translation("feature.city.invite.commands.invite.accept")
                                .clickEvent(ClickEvent.runCommand("/city accept " + sender.getName()))
                                .hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.city.invite.commands.invite.accept_hover")))
                        )
                        .appendNewline()
                        .append(TranslationManager.translation("feature.city.invite.commands.invite.deny")
                                .clickEvent(ClickEvent.runCommand("/city deny " + sender.getName()))
                                .hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.city.invite.commands.invite.deny_hover")))
                        ),
                Prefix.CITY
        );
    }

    @Command("city accept")
    @CommandPermission("omc.commands.city.accept")
    @Description("Accepter une invitation")
    public static void acceptInvitation(
            OMCPlayer player,
            @Named("inviteur") @SuggestWith(InviterAutoComplete.class) OMCOfflinePlayer inviter
    ) {
        if (!CityInviteConditions.canCityInviteAccept(player, inviter)) return;

        CityInvite invite = cityManager.getInvitation(player.getUniqueId(), inviter.getUniqueId());
        City city = invite.city();

        city.addPlayer(player.getUniqueId());
        cityManager.clearInvitations(player.getUniqueId());

        player.message().sendSuccess(
                TranslationManager.translation("feature.city.invite.commands.accept.joined",
                        Component.text(city.getName())),
                Prefix.CITY
        );

        inviter.message().sendSuccess(
                TranslationManager.translation("feature.city.invite.commands.accept.inviter_notified",
                        player.getNameWithHead()),
                Prefix.CITY,
                true
        );
    }

    @Command("city deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    public static void denyInvitation(
            OMCPlayer player,
            @Named("inviteur") @SuggestWith(InviterAutoComplete.class) OMCOfflinePlayer inviter
    ) {
        if (!CityInviteConditions.canCityInviteDeny(player, inviter)) return;

        cityManager.removeInvitation(inviter.getUniqueId(), player.getUniqueId());

        inviter.message().sendWarning(
                TranslationManager.translation("feature.city.invite.commands.deny.inviter_notified",
                        player.getNameWithHead()),
                Prefix.CITY,
                true
        );
    }

    @Command("city cancel")
    @CommandPermission("omc.commands.city.cancel")
    @Description("Annuler une invitation")
    public static void cancelInvitation(
            OMCPlayer player,
            @Named("invité") @SuggestWith(InvitedAutoComplete.class) OMCOfflinePlayer invited
    ) {
        if (!CityInviteConditions.canCityInviteCancel(invited, player)) return;

        City city = cityManager.getInvitation(invited.getUniqueId(), player.getUniqueId()).city();
        cityManager.removeInvitation(player.getUniqueId(), player.getUniqueId());

        invited.message().sendInfo(
                TranslationManager.translation("feature.city.invite.commands.cancel.invited_notified",
                        player.getNameWithHead(),
                        Component.text(city.getName())),
                Prefix.CITY,
                true
        );

        player.message().sendInfo(
                TranslationManager.translation("feature.city.invite.commands.cancel.cancelled",
                        invited.getNameWithHead()),
                Prefix.CITY,
                true
        );
    }
}
