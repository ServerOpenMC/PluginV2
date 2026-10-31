package fr.openmc.core.features.city.sub.mayor.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
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
        } else if (phase == 2) {
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
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.mayor.admin.changeelection.usage"), Prefix.STAFF, MessageType.INFO, false);
            return;
        }

        if (!Objects.equals(electionType, "owner_choose") && !Objects.equals(electionType, "election")) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.mayor.admin.changeelection.usage"), Prefix.STAFF, MessageType.INFO, false);
            return;
        }

        ElectionType E = electionType.equals("owner_choose") ? ElectionType.OWNER_CHOOSE : ElectionType.ELECTION;

        city.getMayor().setElectionType(E);

        MessagesManager.sendMessage(sender, TranslationManager.translation(
                "feature.city.mayor.admin.changeelection.success",
                Component.text(electionType),
                Component.text(city.getName())
        ), Prefix.STAFF, MessageType.INFO, false);

    }

    @Subcommand("warp")
    @CommandPermission("omc.admins.commands.adminmayor.warp")
    public void warpTeleport(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City playerCity = CityManager.getCityByName(name);

        if (playerCity == null) return;

        CityLaw law = playerCity.getLaw();
        Location warp = law.getWarp();

        if (warp == null) {
            if (MayorManager.phaseMayor == 2) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.command.warp.not_set.phase2"), Prefix.CITY, MessageType.INFO, true);
                return;
            }
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.command.warp.not_set.no_mayor"), Prefix.CITY, MessageType.INFO, true);
            return;
        }

        PlayerUtils.sendFadeTitleTeleport(
                player,
                warp
        );
    }
}