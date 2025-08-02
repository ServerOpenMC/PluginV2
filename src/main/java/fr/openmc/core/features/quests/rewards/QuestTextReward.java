package fr.openmc.core.features.quests.rewards;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Class representing a money reward for a quest.
 * <p>
 * This class implements the QuestReward interface and provides functionality to give a specified amount of money to a player.
 */
public record QuestTextReward(String text, Prefix prefix, MessageType messageType) implements QuestReward {
    /**
     * Gives the specified amount of money to the player.
     *
     * @param player The player to whom the reward will be given.
     */
    @Override
    public void giveReward(Player player) {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            MessagesManager.sendMessage(
                    player,
                    Component.text(text),
                    prefix,
                    messageType,
                    false
            );
        }, 1L);

    }
}
