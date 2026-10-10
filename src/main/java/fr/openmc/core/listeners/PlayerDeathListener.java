package fr.openmc.core.listeners;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.utils.EconomyUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    public static final double LOSS_MONEY = 0.35;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDead(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        OMCPlayer player = OMCPlayer.of(event.getPlayer());

        double balance = player.economy().getBalance();

         if (balance>0 && !DreamUtils.isInDreamWorld(player)) {
             player.economy().withdrawBalance(balance * LOSS_MONEY);
             player.message().send(TranslationManager.translation(
                     "core.player.death.message",
                     Component.text(EconomyUtils.getFormattedSimplifiedNumber(balance) + economyManager.getEconomyIcon()).color(NamedTextColor.GOLD),
                     Component.text(EconomyUtils.getFormattedSimplifiedNumber(balance * LOSS_MONEY) + economyManager.getEconomyIcon()).color(NamedTextColor.GOLD)
             ), Prefix.OPENMC, MessageType.INFO, false);
         }

        Component deathMessage = event.deathMessage();
        if (deathMessage != null) {
            MessagesManager.broadcastMessage(deathMessage.color(NamedTextColor.DARK_RED), Prefix.DEATH, MessageType.INFO);
            event.deathMessage(null);
        }
    }
}
