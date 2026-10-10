package fr.openmc.core.features.milestones.quests;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.milestones.MilestoneStep;
import fr.openmc.core.features.milestones.dialogs.MilestoneDialog;
import fr.openmc.core.features.quests.rewards.QuestReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.List;

public record QuestDialogReward(Enum<? extends MilestoneStep> step, List<Component> dialogs) implements QuestReward {
    @Override
    public void giveReward(OMCPlayer player) {
        Bukkit.getServer().getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            player.closeInventory();
            MilestoneDialog.addToMilestoneDialog(player);
            MilestoneDialog.send(player, step, dialogs);
        }, 20);
    }
}
