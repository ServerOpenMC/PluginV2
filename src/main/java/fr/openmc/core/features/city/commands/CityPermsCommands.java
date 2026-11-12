package fr.openmc.core.features.city.commands;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.commands.autocomplete.CityMembersAutoComplete;
import fr.openmc.core.features.city.commands.autocomplete.CityPermissionsAutoComplete;
import fr.openmc.core.features.city.conditions.CityPermsConditions;
import fr.openmc.core.features.city.menu.CityPermsMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"ville perms", "city perms"})
public class CityPermsCommands {
    @Subcommand("switch")
    @CommandPermission("omc.commands.city.perm.switch")
    @Description("Inverse la permission d'un joueur")
    public static void swap(
            OMCPlayer sender,
            @SuggestWith(CityMembersAutoComplete.class) OMCOfflinePlayer player,
            @SuggestWith(CityPermissionsAutoComplete.class) CityPermission permission) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            sender.message().send(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.EXTERNAL_MEMBER_PERM) &&
                !city.getMembers().contains(player.getUniqueId())) {
            swapPermission(city, sender, player, permission);
            return;
        }

        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, permission)) return;

        swapPermission(city, sender, player, permission);
    }

    private static void swapPermission(City city, OMCPlayer sender, OMCOfflinePlayer player, CityPermission permission) {
        if (city.hasPermission(player.getUniqueId(), permission)) {
            city.removePermission(player.getUniqueId(), permission);
            sender.message().send(TranslationManager.translation(
                    "feature.city.perms.commands.switch.removed",
                    player.getNameWithHead(),
                    Component.text(permission.toString())
            ), Prefix.CITY, MessageType.SUCCESS, false);
        } else {
            city.addPermission(player.getUniqueId(), permission);
            sender.message().send(TranslationManager.translation(
                    "feature.city.perms.commands.switch.added",
                    player.getNameWithHead(),
                    permission.getDisplayName()
            ), Prefix.CITY, MessageType.SUCCESS, false);
        }
    }
    
    @Subcommand("add")
    @CommandPermission("omc.commands.city.perm.add")
    @Description("Ajouter des permissions à un membre")
    void add(
            OMCPlayer sender,
            @Named("membre") @SuggestWith(CityMembersAutoComplete.class) OMCOfflinePlayer player,
            @Named("permission") @SuggestWith(CityPermissionsAutoComplete.class) CityPermission permission
    ) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            sender.message().send(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.EXTERNAL_MEMBER_PERM) &&
                !city.getMembers().contains(player.getUniqueId())&&
                !permission.isExternalPermission()) {
            addPermission(city, sender, player, permission);
            return;
        }

        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, permission)) return;

        addPermission(city, sender, player, permission);
    }

    private void addPermission(City city, OMCPlayer sender, OMCOfflinePlayer player, CityPermission permission) {
        if (city.hasPermission(player.getUniqueId(), permission)) {
            sender.message().send(TranslationManager.translation(
                    "feature.city.perms.commands.add.already_has",
                    player.getNameWithHead()
            ), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.addPermission(player.getUniqueId(), permission);
        sender.message().send(TranslationManager.translation(
                "feature.city.perms.commands.modified",
                player.getNameWithHead()
        ), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.commands.city.perm.remove")
    @Description("Retirer des permissions à un membre")
    void remove(
            OMCPlayer sender,
            @Named("membre") @SuggestWith(CityMembersAutoComplete.class) OMCOfflinePlayer player,
            @Named("permission") @SuggestWith(CityPermissionsAutoComplete.class) CityPermission permission
    ) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            sender.message().send(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.EXTERNAL_MEMBER_PERM) &&
                !city.getMembers().contains(player.getUniqueId()) &&
                !permission.isExternalPermission()) {
            removePermission(city, sender, player, permission);
            return;
        }

        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, permission)) return;

        removePermission(city, sender, player, permission);
    }

    private void removePermission(City city, OMCPlayer sender, OMCOfflinePlayer player, CityPermission permission) {
        if (!city.hasPermission(player.getUniqueId(), permission)) {
            sender.message().send(TranslationManager.translation(
                    "feature.city.perms.commands.remove.does_not_have",
                    player.getNameWithHead()
            ), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.removePermission(player.getUniqueId(), permission);
        sender.message().send(TranslationManager.translation(
                "feature.city.perms.commands.modified",
                player.getNameWithHead()
        ), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("get")
    @CommandPermission("omc.commands.city.perm.get")
    @Description("Obtenir les permissions d'un membre")
    void get(OMCPlayer sender, @SuggestWith(CityMembersAutoComplete.class) OMCOfflinePlayer player) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        new CityPermsMenu(sender, player.getUniqueId(), false).open();
    }
    
    @Subcommand("removeall")
    @CommandPermission("omc.commands.city.perm.removeall")
    @Description("Retirer toutes les permissions d'un membre")
    public static void removeAll(OMCPlayer sender, @SuggestWith(CityMembersAutoComplete.class) OMCOfflinePlayer player) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            sender.message().send(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.EXTERNAL_MEMBER_PERM) &&
                !city.getMembers().contains(player.getUniqueId())) {
            removeAllPermissions(city, player);
            return;
        }

        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, null)) return;
        
        removeAllPermissions(city, player);
    }

    private static void removeAllPermissions(City city, OMCOfflinePlayer player) {
        for (CityPermission permission : CityPermission.values()) {
            if (permission == CityPermission.OWNER) continue;
            if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.EXTERNAL_MEMBER_PERM) &&
                    !city.getMembers().contains(player.getUniqueId()) && !permission.isExternalPermission()) continue;
            city.removePermission(player.getUniqueId(), permission);
        }
    }
    
    @Subcommand("addall")
    @CommandPermission("omc.commands.city.perm.addall")
    @Description("Ajouter toutes les permissions à un membre")
    public static void addAll(OMCPlayer sender, @SuggestWith(CityMembersAutoComplete.class) OMCOfflinePlayer player) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            sender.message().send(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.EXTERNAL_MEMBER_PERM) &&
                !city.getMembers().contains(player.getUniqueId())) {
            addAllPermissions(city, player);
            return;
        }

        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, null)) return;
        
        addAllPermissions(city, player);
    }

    private static void addAllPermissions(City city, OMCOfflinePlayer player) {
        for (CityPermission permission : CityPermission.values()) {
            if (permission == CityPermission.OWNER) continue;
            if (!city.getMembers().contains(player.getUniqueId()) && !permission.isExternalPermission()) continue;
            city.addPermission(player.getUniqueId(), permission);
        }
    }
}
