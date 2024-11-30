package fr.openmc.core.features.city.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import fr.openmc.core.features.city.*;
import fr.openmc.core.features.economy.EconomyManager;
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
    private final MessagesManager msgStaff  = new MessagesManager(Prefix.STAFF);

    @Subcommand("deleteCity")
    @CommandPermission("omc.admins.commands.admincity.deleteCity")
    void deleteCity(Player player, @Named("uuid") String city_uuid) {
        City city = CityManager.getCity(city_uuid);

        if (city == null) {
            msgStaff.error(player, "La ville n'existe pas");
            return;
        }

        city.delete();
        msgStaff.success(player, "La ville a été supprimée");
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
                msgStaff.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
                return;
            }
        }
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            msgStaff.error(player, "Cette ville n'existe pas");
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
            msgStaff.error(player, "La ville n'existe pas");
            return;
        }
        city.renameCity(newName);

        msgStaff.success(player, "La ville a été renommée");
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(Player player, @Named("uuid") String city_uuid, @Named("nouveau maire") Player newOwner) {
        City city = CityManager.getCity(city_uuid);

        if (city == null) {
            msgStaff.error(player, "La ville n'existe pas");
            return;
        }

        city.changeOwner(newOwner.getUniqueId());
        msgStaff.success(player, "Le propriété a été transférée");
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(Player player, @Named("uuid") String city_uuid, @Named("balance") double newBalance) {
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            msgStaff.error(player, "La ville n'existe pas");
            return;
        }

        city.setBalance(newBalance);
        msgStaff.success(player, "Le solde a été modifié");
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(Player player, String city_uuid) {
        City city = CityManager.getCity(city_uuid);
        if (city == null) {
            msgStaff.error(player, "La ville n'existe pas");
            return;
        }

        msgStaff.info(player, "Le solde de la ville est de "+ city.getBalance()+ EconomyManager.getEconomyIcon());
    }

    @Subcommand("add")
    @CommandPermission("omc.admins.commands.admincity.add")
    void add(Player player, @Named("uuid") String city_uuid, Player newMember) {
        City city = CityManager.getCity(city_uuid);

        if (city == null) {
            msgStaff.error(player, "La ville n'existe pas");
            return;
        }

        if (CityManager.getPlayerCity(newMember.getUniqueId()) != null) {
            msgStaff.error(player, "Le joueur est déjà dans une ville");
            return;
        }

        city.addPlayer(newMember.getUniqueId());
        msgStaff.success(player, "Le joueur a été ajouté");
    }

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.admincity.remove")
    void remove(Player player, @Named("uuid") String city_uuid, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            msgStaff.error(player, "Le joueur n'est pas dans une ville");
            return;
        }

        if (city.hasPermission(member.getUniqueId(), CPermission.OWNER)) {
            msgStaff.error(player, "Le joueur est le propriétaire de la ville");
            return;
        }

        city.removePlayer(member.getUniqueId());
        msgStaff.success(player, "Le joueur a été retiré");
    }

    @Subcommand("getPlayer")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(Player player, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            msgStaff.error(player, "Le joueur n'est pas dans une ville");
            return;
        }

        msgStaff.info(player, "Le joueur est dans la ville "+ city.getName()+" ("+city.getUUID()+")");
    }
}
