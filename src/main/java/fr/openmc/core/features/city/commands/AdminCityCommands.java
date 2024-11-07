package fr.openmc.core.features.city.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityMessages;
import fr.openmc.core.features.city.CityPermissions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admcity")
@CommandPermission("omc.admins.commands.admincity")
public class AdminCityCommands {

    @Subcommand("clearCache")
    @CommandPermission("omc.admins.commands.admincity.clearCache")
    @AutoComplete("playerCity|cityOwners|cityNames|members|balance|all")
    void clearCache(Player player, @Named("uuid") String cacheLevel) {
        CityManager.clearCache(cacheLevel);
        MessagesManager.sendMessageType(player, "Le cache a été vidé", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("deleteCity")
    @CommandPermission("omc.admins.commands.admincity.deleteCity")
    void deleteCity(Player player, @Named("uuid") String city_uuid) {
        if (CityManager.deleteCity(city_uuid)) {
            MessagesManager.sendMessageType(player, "La ville a été supprimée", Prefix.STAFF, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessageType(player, "Impossible de supprimer la ville", Prefix.STAFF, MessageType.ERROR, false);
        }
    }

    @Subcommand("info")
    @CommandPermission("omc.admins.commands.admincity.info")
    @AutoComplete("here|<uuid>")
    void info(Player player, @Named("uuid") String city_uuid) {
        if (city_uuid.equalsIgnoreCase("here")) {
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
            for (String regionId : regionManager.getRegions().keySet()) {
                if (regionManager.getRegion(regionId).contains(BukkitAdapter.asBlockVector(player.getLocation()))) {
                    city_uuid = regionId.replace("city_", "");
                    break;
                }
            }
            if (city_uuid.equalsIgnoreCase("here")) {
                MessagesManager.sendMessageType(player, "Vous n'êtes pas dans une ville", Prefix.STAFF, MessageType.ERROR, false);
                return;
            }
        }
        CityMessages.sendInfo(player, city_uuid);
    }

    @Subcommand("rename")
    @CommandPermission("omc.admins.commands.admincity.rename")
    void rename(Player player, @Named("uuid") String city_uuid, @Named("nouveau nom") String newName) {
        // Aucune vérification de nom mais faut espérer que le nom est valide :beluclown:
        CityManager.renameCity(city_uuid, newName);
        MessagesManager.sendMessageType(player, "La ville a été renommée", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(Player player, @Named("uuid") String city_uuid, @Named("nouveau maire") Player newOwner) {
        CityManager.changeOwner(newOwner.getUniqueId(), city_uuid);
        MessagesManager.sendMessageType(player, "Le propriétaire a été changé", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(Player player, @Named("uuid") String city_uuid, @Named("balance") double newBalance) {
        CityManager.setBalance(city_uuid, newBalance);
        MessagesManager.sendMessageType(player, "Le solde a été changé", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(Player player, String city_uuid) {
        MessagesManager.sendMessageType(player, "Le solde de la ville est de "+CityManager.getBalance(city_uuid)+ EconomyManager.getEconomyIcon(), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("add")
    @CommandPermission("omc.admins.commands.admincity.add")
    void add(Player player, @Named("uuid") String city_uuid, Player newMember) {
        if (CityManager.getPlayerCity(newMember.getUniqueId()) != null) {
            MessagesManager.sendMessageType(player, "Le joueur est déjà dans une ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityManager.playerJoinCity(newMember.getUniqueId(), city_uuid);
        MessagesManager.sendMessageType(player, "Le joueur a été ajouté", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.admincity.remove")
    void remove(Player player, @Named("uuid") String city_uuid, Player member) {
        String playerCity = CityManager.getPlayerCity(member.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Le joueur n'est pas dans une ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (CityPermissions.hasPermission(city_uuid, member.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessageType(player, "Le joueur est le propriétaire de la ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityManager.playerLeaveCity(member.getUniqueId());
        MessagesManager.sendMessageType(player, "Le joueur a été retiré", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getPlayer")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(Player player, Player member) {
        String playerCity = CityManager.getPlayerCity(member.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Le joueur n'est pas dans une ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessageType(player, "Le joueur est dans la ville "+CityManager.getCityName(playerCity)+" ("+playerCity+")", Prefix.STAFF, MessageType.INFO, false);
    }
}
