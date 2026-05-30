package fr.openmc.core.listeners;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static fr.openmc.core.features.economy.EconomyManager.*;

public class PlayerDeathListener implements Listener {
    public static final double LOSS_MONEY = 0.35;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDead(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        double balance = getBalance(player.getUniqueId());

         if (balance>0 && !DreamUtils.isInDreamWorld(player)) {
             withdrawBalance(player.getUniqueId(), balance * LOSS_MONEY);
             MessagesManager.sendMessage(player, TranslationManager.translation("core.player.death.message", Component.text(getFormattedSimplifiedNumber(balance) + EconomyManager.getEconomyIcon()), Component.text(getFormattedSimplifiedNumber(balance * LOSS_MONEY) + EconomyManager.getEconomyIcon())), Prefix.OPENMC, MessageType.INFO, false);
         }

        Component deathMessage = event.deathMessage();
        if (deathMessage == null) return;
        MessagesManager.broadcastMessage(deathMessage.color(NamedTextColor.DARK_RED), Prefix.DEATH, MessageType.INFO);
        event.deathMessage(null);
    }
}
