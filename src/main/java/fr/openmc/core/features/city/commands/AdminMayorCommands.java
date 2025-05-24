package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.util.Objects;

@Command({"adminmayor"})
@CommandPermission("omc.admins.commands.adminmayor")
public class AdminMayorCommands {
    @Subcommand({"setphase"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void setPhase(Player sender, int phase) throws SQLException {
        MayorManager mayorManager = MayorManager.getInstance();
        if (phase == 1) {
            mayorManager.initPhase1();
        } else if (phase == 2){
            mayorManager.initPhase2();
        }
    }

    @Subcommand({"changeelection"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void changeElection(Player sender, @Named("uuid") String cityUUID, String electionType) throws SQLException {
        City city = CityManager.getCity(cityUUID);

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.CITYNOTFOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            MessagesManager.sendMessage(sender, Component.text("/adminmayor changeelection cityUUID electionType<owner_choose/election>"), Prefix.STAFF, MessageType.INFO, false);
            return;
        }

        if (!Objects.equals(electionType, "owner_choose") && !Objects.equals(electionType, "election")) {
            MessagesManager.sendMessage(sender, Component.text("/adminmayor changeelection cityUUID electionType<owner_choose/election>"), Prefix.STAFF, MessageType.INFO, false);
            return;
        }

        ElectionType E = electionType.equals("owner_choose") ? ElectionType.OWNER_CHOOSE : ElectionType.ELECTION;

        city.getMayor().setElectionType(E);

        MessagesManager.sendMessage(sender, Component.text("Vous venez de mettre : " + electionType + " dans la ville " + city.getUUID()), Prefix.STAFF, MessageType.INFO, false);

    }
}