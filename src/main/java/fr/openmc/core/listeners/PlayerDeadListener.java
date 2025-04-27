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
    private final double LOSS_MONEY = 0.35;
    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        EconomyManager economyManager = EconomyManager.getInstance();
        double balance = economyManager.getBalance(player.getUniqueId());

        if (balance>0) {
            economyManager.withdrawBalance(player.getUniqueId(), balance*LOSS_MONEY);
            MessagesManager.sendMessage(player, Component.text("Vous venez de mourrir avec §6" + economyManager.getFormattedSimplifiedNumber(balance) + EconomyManager.getEconomyIcon() + "§f, vous avez perdu §6" + economyManager.getFormattedSimplifiedNumber(balance*LOSS_MONEY) + EconomyManager.getEconomyIcon() + "\n§8*pensez à mettre votre argent dans la banque*"), Prefix.OPENMC, MessageType.INFO, false);
        }

    }
}
