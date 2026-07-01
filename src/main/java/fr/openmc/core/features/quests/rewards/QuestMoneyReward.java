package fr.openmc.core.features.quests.rewards;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * Class representing a money reward for a quest.
 * <p>
 * This class implements the QuestReward interface and provides functionality to give a specified amount of money to a player.
 */
public record QuestMoneyReward(double amount) implements QuestReward {

    /**
     * Gives the specified amount of money to the player.
     *
     * @param player The player to whom the reward will be given.
     */
    @Override
    public void giveReward(Player player) {
        EconomyManager.addBalance(player.getUniqueId(), amount, "Récompense de quête");
        Component amountComponent = Component.text(EconomyManager.getFormattedSimplifiedNumber(amount) + " " + EconomyManager.getEconomyIcon())
                .color(NamedTextColor.YELLOW);
        Component message = TranslationManager.translation("feature.quests.message.money_reward", amountComponent)
                .color(NamedTextColor.GREEN);
        MessagesManager.sendMessage(
                player,
                message,
                Prefix.QUEST,
                MessageType.SUCCESS,
                false
        );
    }
}
