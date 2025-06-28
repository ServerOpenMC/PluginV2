package fr.openmc.core.listeners;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static fr.openmc.core.features.economy.EconomyManager.*;

public class PlayerDeathListener implements Listener {
    private final double LOSS_MONEY = 0.35;
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        double balance = getBalance(player.getUniqueId());

        if (balance>0) {
            withdrawBalance(player.getUniqueId(), balance * LOSS_MONEY);
            MessagesManager.sendMessage(player, Component.text("Vous venez de mourrir avec §6" + getFormattedSimplifiedNumber(balance) + EconomyManager.getEconomyIcon() + "§f, vous avez perdu §6" + getFormattedSimplifiedNumber(balance * LOSS_MONEY) + EconomyManager.getEconomyIcon() + "\n§8*pensez à mettre votre argent dans la banque*"), Prefix.OPENMC, MessageType.INFO, false);
        }
        
        if (event.deathMessage() == null) return;
        MessagesManager.broadcastMessage(event.deathMessage().color(NamedTextColor.DARK_RED), Prefix.DEATH, MessageType.INFO);
        event.deathMessage(null);
    }
}
