package fr.openmc.core.features.dream.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.commands.autocomplete.DreamMilestoneStepsAutoComplete;
import fr.openmc.core.features.dream.commands.autocomplete.DreamOrbAutoComplete;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.milestones.dialogs.MilestoneDialog;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@Command("admdream")
@CommandPermission("omc.admins.commands.admindream")
public class AdminDreamCommands {
    private final DreamManager dreamManager;

    public AdminDreamCommands(DreamManager manager) {
        this.dreamManager = manager;
    }

    @Subcommand("setprogressionorb")
    @CommandPermission("omc.admins.commands.admindream.setprogressionorb")
    void setProgressionOrb(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player toPlayer,
            @Named("nb_progression_orb") @SuggestWith(DreamOrbAutoComplete.class) int orbProgression
    ) {
        dreamManager.setProgressionOrb(toPlayer, orbProgression, null);
        DBDreamPlayer cache = dreamManager.getCacheDreamPlayer(player);

        if (cache != null) {
            cache.setProgressionOrb(orbProgression);
            dreamManager.saveDreamPlayerData(cache);
            return;
        }

        DreamPlayer dreamPlayer = dreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;
        dreamManager.saveDreamPlayerData(dreamPlayer);

        DBDreamPlayer cache1 = dreamManager.getCacheDreamPlayer(player);
        if (cache1 == null) return;
        cache1.setProgressionOrb(orbProgression);
        dreamManager.saveDreamPlayerData(cache1);
    }

    @Subcommand("showdialog")
    @CommandPermission("omc.admins.commands.admindream.showdialog")
    void showMilestoneDialog(Player player, @Named("milestone_step") @SuggestWith(DreamMilestoneStepsAutoComplete.class) String stepName) {
        MilestoneQuest quest;
        try {
            quest = DreamSteps.valueOf(stepName).getQuest();
        } catch (IllegalArgumentException e) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.admin.commands.showdialog.invalid_step"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        List<Component> dialogs = quest.getDialogs();
        if (dialogs == null || dialogs.isEmpty()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.admin.commands.showdialog.no_dialogs"), Prefix.DREAM, MessageType.WARNING, false);
            return;
        }

        MilestoneDialog.send(player, quest.getStep(), dialogs);
    }
}
