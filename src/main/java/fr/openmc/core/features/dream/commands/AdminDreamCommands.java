package fr.openmc.core.features.dream.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.commands.autocomplete.DreamMilestoneStepsAutoComplete;
import fr.openmc.core.features.dream.listeners.orb.PlayerObtainOrb;
import fr.openmc.core.features.dream.milestone.DreamMilestoneDialog;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@Command("admdream")
@CommandPermission("omc.admins.commands.admindream")
public class AdminDreamCommands {
    @Subcommand("setprogressionorb")
    @CommandPermission("omc.admins.commands.admindream.setprogressionorb")
    void setProgressionOrb(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player toPlayer,
            @Named("nb_progression_orb") @Suggest({"1", "2", "3", "4", "5"}) int orbProgression
    ) {
        PlayerObtainOrb.setProgressionOrb(toPlayer, orbProgression, null);
        DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);

        if (cache != null) {
            cache.setProgressionOrb(orbProgression);
            DreamManager.saveDreamPlayerData(cache);
            return;
        }

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;
        DreamManager.saveDreamPlayerData(dreamPlayer);

        DBDreamPlayer cache1 = DreamManager.getCacheDreamPlayer(player);
        if (cache1 == null) return;
        cache1.setProgressionOrb(orbProgression);
        DreamManager.saveDreamPlayerData(cache1);
    }
    
    @Subcommand("showdialog")
    @CommandPermission("omc.admins.commands.admindream.showdialog")
    void showMilestoneDialog(Player player, @Named("milestone_step") @SuggestWith(DreamMilestoneStepsAutoComplete.class) String stepName) {
        MilestoneQuest quest;
        try {
            quest = DreamSteps.valueOf(stepName).getQuest();
        } catch (IllegalArgumentException e) {
            MessagesManager.sendMessage(player, Component.text("§cLe nom de l'étape n'est pas valide !"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }
        
        List<String> dialogs = quest.getDialogs();
        if (dialogs == null || dialogs.isEmpty()) {
            MessagesManager.sendMessage(player, Component.text("§cCette étape n'a pas de dialogs !"), Prefix.DREAM, MessageType.WARNING, false);
            return;
        }
        
        DreamMilestoneDialog.send(player, (DreamSteps) quest.getStep(), dialogs, 1);
    }
}
