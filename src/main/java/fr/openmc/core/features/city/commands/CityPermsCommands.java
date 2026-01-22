package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.commands.autocomplete.CityMembersAutoComplete;
import fr.openmc.core.features.city.commands.autocomplete.CityPermissionsAutoComplete;
import fr.openmc.core.features.city.conditions.CityPermsConditions;
import fr.openmc.core.features.city.menu.CityPermsMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"ville perms", "city perms"})
public class CityPermsCommands {
    @Subcommand("switch")
    @CommandPermission("omc.commands.city.perm.switch")
    @Description("Inverse la permission d'un joueur")
    public static void swap(Player sender, @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player, @SuggestWith(CityPermissionsAutoComplete.class) CityPermission permission) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, permission)) return;

        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), permission)) {
            city.removePermission(player.getUniqueId(), permission);
            MessagesManager.sendMessage(sender, Component.text(player.getName()+" a perdu la permission \""+permission.toString()+"\""), Prefix.CITY, MessageType.SUCCESS, false);
        } else {
            city.addPermission(player.getUniqueId(), permission);
	        MessagesManager.sendMessage(sender, Component.text(player.getName() + " a reçu la permission \"" + permission.toString() + "\""), Prefix.CITY, MessageType.SUCCESS, false);
        }
    }
    
    @Subcommand("add")
    @CommandPermission("omc.commands.city.perm.add")
    @Description("Ajouter des permissions à un membre")
    void add(
            Player sender,
            @Named("membre") @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player,
            @Named("permission") @SuggestWith(CityPermissionsAutoComplete.class) CityPermission permission
    ) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, permission)) return;
      
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), permission)) {
            MessagesManager.sendMessage(sender, Component.text(player.getName() + " a déjà cette permission"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.addPermission(player.getUniqueId(), permission);
        MessagesManager.sendMessage(sender, Component.text("Les permissions de "+ player.getName() + " ont été modifiées"), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.commands.city.perm.remove")
    @Description("Retirer des permissions à un membre")
    void remove(
            Player sender,
            @Named("membre") @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player,
            @Named("permission") @SuggestWith(CityPermissionsAutoComplete.class) CityPermission permission
    ) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, permission)) return;
  
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), permission)) {
            MessagesManager.sendMessage(sender, Component.text(player.getName() + " n'a pas cette permission"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.removePermission(player.getUniqueId(), permission);
        MessagesManager.sendMessage(sender, Component.text("Les permissions de "+ player.getName() + " ont été modifiées"), Prefix.CITY, MessageType.SUCCESS, false);
    }


    @Subcommand("get")
    @CommandPermission("omc.commands.city.perm.get")
    @Description("Obtenir les permissions d'un membre")
    void get(Player sender, @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        new CityPermsMenu(sender, player.getUniqueId(), false).open();
    }
    
    @Subcommand("removeall")
    @CommandPermission("omc.commands.city.perm.removeall")
    @Description("Retirer toutes les permissions d'un membre")
    public static void removeAll(Player sender, @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, null)) return;
        
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        
        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        
        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        
        for (CityPermission permission : CityPermission.values()) {
            if (permission == CityPermission.OWNER) continue;
            city.removePermission(player.getUniqueId(), permission);
        }
    }
    
    @Subcommand("addall")
    @CommandPermission("omc.commands.city.perm.addall")
    @Description("Ajouter toutes les permissions à un membre")
    public static void addAll(Player sender, @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player) {
        if (!CityPermsConditions.canSeePerms(sender, player.getUniqueId())) return;
        if (!CityPermsConditions.canModifyPerms(sender, null)) return;
        
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        
        for (CityPermission permission : CityPermission.values()) {
            if (permission == CityPermission.OWNER) continue;
            city.addPermission(player.getUniqueId(), permission);
        }
    }
}
