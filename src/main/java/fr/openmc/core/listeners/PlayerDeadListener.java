package fr.openmc.core.listeners;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeadListener implements Listener {

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        EconomyManager economyManager = EconomyManager.getInstance();
        double balance = economyManager.getBalance(player.getUniqueId());

        if (balance>0) {
            System.out.println(balance/2);
            economyManager.withdrawBalance(player.getUniqueId(), balance/2);
            MessagesManager.sendMessage(player, Component.text("Vous venez de mourrir avec §6" + economyManager.getFormattedSimplifiedNumber(balance) + "§f, vous n'avez plus que §6" + economyManager.getFormattedSimplifiedNumber(balance/2)), Prefix.OPENMC, MessageType.INFO, false);
        }

    }
}
