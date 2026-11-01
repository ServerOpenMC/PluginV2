package fr.openmc.core.features.milestones.commands;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.milestones.commands.autocomplete.MilestoneAutocomplete;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneModel;
import fr.openmc.core.features.milestones.models.MilestoneType;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admmilestones")
@CommandPermission("omc.admins.commands.admmilestones")
public class AdminMilestoneCommand {
    @Subcommand("modifyProgress")
    @CommandPermission("omc.admins.commands.admmilestones.modifyProgress")
    void mainCommand(Player player,
                     @SuggestWith(OnlinePlayerAutoComplete.class) OMCOfflinePlayer target,
                     @SuggestWith(MilestoneAutocomplete.class) String milestoneType,
                     int step) {
        MilestoneType type = MilestoneType.valueOf(milestoneType);
        Milestone<?> milestone = type.getMilestone();
        MilestoneModel model = milestone.getPlayerData().get(target.getUniqueId());

        model.setStep(step);
    }
}
