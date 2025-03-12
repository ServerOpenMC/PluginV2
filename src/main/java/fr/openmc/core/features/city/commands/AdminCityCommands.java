package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.*;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.util.List;

@Command("admcity")
@CommandPermission("omc.admins.commands.admincity")
public class AdminCityCommands {
    @Subcommand("deleteCity")
    @CommandPermission("omc.admins.commands.admincity.deleteCity")
    void deleteCity(Player player, @Named("uuid") String cityUUID) {
        City city = CityManager.getCity(cityUUID);

        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.delete();
        MessagesManager.sendMessage(player, Component.text("La ville a été supprimée"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("info")
    @CommandPermission("omc.admins.commands.admincity.info")
    @AutoComplete("<uuid>")
    void info(Player player, @Named("uuid") String cityUUID) {
        City city = CityManager.getCity(cityUUID);

        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Cette ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityMessages.sendInfo(player, city);
    }

    @Subcommand("rename")
    @CommandPermission("omc.admins.commands.admincity.rename")
    void rename(Player player, @Named("uuid") String cityUUID, @Named("nouveau nom") String newName) {
        // Aucune vérification de nom mais faut espérer que le nom est valide :beluclown:
        City city = CityManager.getCity(cityUUID);
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        city.renameCity(newName);

        MessagesManager.sendMessage(player, Component.text("La ville a été renommée"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(Player player, @Named("uuid") String cityUUID, @Named("nouveau maire") Player newOwner) {
        City city = CityManager.getCity(cityUUID);

        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.changeOwner(newOwner.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Le propriété a été transférée"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(Player player, @Named("uuid") String cityUUID, @Named("balance") double newBalance) {
        City city = CityManager.getCity(cityUUID);
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.setBalance(newBalance);
        MessagesManager.sendMessage(player, Component.text("Le solde a été modifié"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(Player player, String cityUUID) {
        City city = CityManager.getCity(cityUUID);
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player, Component.text("Le solde de la ville est de "+ city.getBalance()+ EconomyManager.getEconomyIcon()), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("add")
    @CommandPermission("omc.admins.commands.admincity.add")
    void add(Player player, @Named("uuid") String cityUUID, Player newMember) {
        City city = CityManager.getCity(cityUUID);

        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getPlayerCity(newMember.getUniqueId()) != null) {
            MessagesManager.sendMessage(player, Component.text("Le joueur est déjà dans une ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.addPlayer(newMember.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Le joueur a été ajouté"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.admincity.remove")
    void remove(Player player, @Named("uuid") String cityUUID, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Le joueur n'est pas dans une ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(member.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessage(player, Component.text("Le joueur est le propriétaire de la ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.removePlayer(member.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Le joueur a été retiré"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getPlayer")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(Player player, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Le joueur n'est pas dans une ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player, Component.text("Le joueur est dans la ville "+ city.getName()+" ("+city.getUUID()+")"), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("freeclaim add")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.add")
    public void freeClaimAdd(@Named("player") Player player, @Named("claim") int claim) {
        MascotsManager.addFreeClaim(claim, player);
    }

    @Subcommand("freeclaim remove")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimRemove(@Named("player") Player player, @Named("claim") int claim) {
        MascotsManager.removeFreeClaim(claim, player);
    }

    @Subcommand("mascots remove")
    @CommandPermission("omc.admins.commands.admcity.mascots.remove")
    public void forceRemoveMascots (Player sender, @Named("player") Player target) throws SQLException {
        List<String> uuidList = CityManager.getAllCityUUIDs();
        City city = CityManager.getPlayerCity(target.getUniqueId());

        if (city != null){
            String city_uuid = city.getUUID();

            if (uuidList.contains(city_uuid)){
                MascotsManager.removeMascotsFromCity(city_uuid);
                return;
            }

            MessagesManager.sendMessage(sender, Component.text("§cVille innexistante"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("mascots chest")
    @CommandPermission("omc.admins.commands.admcity.mascots.chest")
    public void giveMascotsChest(@Named("player") Player target){
        if (target.isOnline()){
            MascotsManager.giveChest(target);
        }
    }

    @Subcommand("mascots immunityoff")
    @CommandPermission("omc.admins.commands.admcity.mascots.immunityoff")
    public void removeMascotImmunity(Player sender, @Named("player") Player target){
        City city = CityManager.getPlayerCity(target.getUniqueId());

        if (city==null){
            MessagesManager.sendMessage(sender, Component.text("§cLe joueur n'a pas de ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        String city_uuid = city.getUUID();


        MascotsManager.loadMascotsConfig();
        if (!MascotsManager.mascotsConfig.getBoolean("mascots." + city_uuid + ".alive")){
            MessagesManager.sendMessage(sender, Component.text("§cLa mascotte est en immunité forcée"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        MascotsManager.mascotsConfig.set("mascots." + city_uuid + ".immunity.activate", false);
        MascotsManager.mascotsConfig.set("mascots." + city_uuid + ".immunity.time", 0);
        MascotsManager.saveMascotsConfig();
    }
}
