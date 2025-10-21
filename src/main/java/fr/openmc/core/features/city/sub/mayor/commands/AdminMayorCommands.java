package fr.openmc.core.features.city.sub.mayor.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

@Command({"adminmayor"})
@CommandPermission("omc.admins.commands.adminmayor")
public class AdminMayorCommands {
    @Subcommand({"setphase"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void setPhase(
            Player sender,
            @Named("phase") @Suggest({"1", "2"}) int phase
    ) {
        if (phase == 1) {
            MayorManager.initPhase1();
        } else if (phase == 2){
            MayorManager.initPhase2();
        }
    }

    @Subcommand({"changeelection"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void changeElection(
            Player sender,
            @Named("name") @SuggestWith(CityNameAutoComplete.class) String cityName,
            @Named("electionType") @Suggest({"owner_choose", "election"}) String electionType
    ) {
        City city = CityManager.getCityByName(cityName);

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            MessagesManager.sendMessage(sender, Component.text("/adminmayor changeelection cityUUID electionType<owner_choose/election>"), Prefix.STAFF, MessageType.INFO, false);
            return;
        }

        if (!Objects.equals(electionType, "owner_choose") && !Objects.equals(electionType, "election")) {
            MessagesManager.sendMessage(sender, Component.text("/adminmayor changeelection cityUUID electionType<owner_choose/election>"), Prefix.STAFF, MessageType.INFO, false);
            return;
        }

        ElectionType E = electionType.equals("owner_choose") ? ElectionType.OWNER_CHOOSE : ElectionType.ELECTION;

        city.getMayor().setElectionType(E);

        MessagesManager.sendMessage(sender, Component.text("Vous venez de mettre : " + electionType + " dans la ville " + city.getName()), Prefix.STAFF, MessageType.INFO, false);

    }
}