package fr.openmc.core.features.city.sub.mayor.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

@Command({"adminmayor"})
@CommandPermission("omc.admins.commands.adminmayor")
public class AdminMayorCommands {
    private final MayorManager mayorManager = OMCRegistry.CITY_FEATURES.MAYOR;

    @Subcommand({"setphase"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void setPhase(
            OMCPlayer sender,
            @Named("phase") @Suggest({"1", "2"}) int phase
    ) {
        if (phase == 1) {
            mayorManager.initOpenElectionPhase();
        } else if (phase == 2){
            mayorManager.initElectedMayorPhase();
        }
    }

    @Subcommand({"changeelection"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void changeElection(
            OMCPlayer sender,
            @Named("name") @SuggestWith(CityNameAutoComplete.class) String cityName,
            @Named("electionType") @Suggest({"owner_choose", "election"}) String electionType
    ) {
        City city = City.of(cityName);

        if (city == null) {
            sender.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, false);
            sender.message().sendInfo(TranslationManager.translation("feature.city.mayor.admin.changeelection.usage"), Prefix.STAFF, false);
            return;
        }

        if (!Objects.equals(electionType, "owner_choose") && !Objects.equals(electionType, "election")) {
            sender.message().sendInfo(TranslationManager.translation("feature.city.mayor.admin.changeelection.usage"), Prefix.STAFF, false);
            return;
        }

        ElectionType E = electionType.equals("owner_choose") ? ElectionType.OWNER_CHOOSE : ElectionType.ELECTION;

        city.getMayor().setElectionType(E);

        sender.message().sendInfo(TranslationManager.translation(
                "feature.city.mayor.admin.changeelection.success",
                Component.text(electionType),
                Component.text(city.getName())
        ), Prefix.STAFF, false);

    }
}