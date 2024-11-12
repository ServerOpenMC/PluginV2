package fr.openmc.core.features.city.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import fr.openmc.core.features.city.*;
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
    @Subcommand("deleteCity")
    @CommandPermission("omc.admins.commands.admincity.deleteCity")
    void deleteCity(Player player, @Named("uuid") String city_uuid) {
        City city = CityManager.getCity(city_uuid);

        if (city == null) {
            MessagesManager.sendMessageType(player, "La ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.delete();
        MessagesManager.sendMessageType(player, "La ville a été supprimée", Prefix.STAFF, MessageType.SUCCESS, false);
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
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            MessagesManager.sendMessageType(player, "Cette ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        CityMessages.sendInfo(player, city);
    }

    @Subcommand("rename")
    @CommandPermission("omc.admins.commands.admincity.rename")
    void rename(Player player, @Named("uuid") String city_uuid, @Named("nouveau nom") String newName) {
        // Aucune vérification de nom mais faut espérer que le nom est valide :beluclown:
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            MessagesManager.sendMessageType(player, "La ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        city.renameCity(newName);

        MessagesManager.sendMessageType(player, "La ville a été renommée", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(Player player, @Named("uuid") String city_uuid, @Named("nouveau maire") Player newOwner) {
        City city = CityManager.getCity(city_uuid);

        if (city == null) {
            MessagesManager.sendMessageType(player, "La ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.changeOwner(newOwner.getUniqueId());
        MessagesManager.sendMessageType(player, "Le propriétaire a été changé", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(Player player, @Named("uuid") String city_uuid, @Named("balance") double newBalance) {
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            MessagesManager.sendMessageType(player, "La ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.setBalance(newBalance);
        MessagesManager.sendMessageType(player, "Le solde a été changé", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(Player player, String city_uuid) {
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            MessagesManager.sendMessageType(player, "La ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessageType(player, "Le solde de la ville est de "+ city.getBalance()+ EconomyManager.getEconomyIcon(), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("add")
    @CommandPermission("omc.admins.commands.admincity.add")
    void add(Player player, @Named("uuid") String city_uuid, Player newMember) {
        City city = CityManager.getCity(city_uuid);

        if (city == null) {
            MessagesManager.sendMessageType(player, "La ville n'existe pas", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getPlayerCity(newMember.getUniqueId()) != null) {
            MessagesManager.sendMessageType(player, "Le joueur est déjà dans une ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.addPlayer(newMember.getUniqueId());
        MessagesManager.sendMessageType(player, "Le joueur a été ajouté", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.admincity.remove")
    void remove(Player player, @Named("uuid") String city_uuid, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, "Le joueur n'est pas dans une ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(member.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessageType(player, "Le joueur est le propriétaire de la ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.removePlayer(member.getUniqueId());
        MessagesManager.sendMessageType(player, "Le joueur a été retiré", Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getPlayer")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(Player player, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, "Le joueur n'est pas dans une ville", Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessageType(player, "Le joueur est dans la ville "+ city.getName()+" ("+city.getUUID()+")", Prefix.STAFF, MessageType.INFO, false);
    }
}
