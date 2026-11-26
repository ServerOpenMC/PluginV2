package fr.openmc.core.features.events.contents.deposit.listener;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import fr.openmc.core.features.events.contents.deposit.menu.DepositMenu;
import fr.openmc.core.features.events.contents.deposit.models.DepositEventModel;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DepositListener implements Listener {

    @EventHandler
    public void onNpcClick(NpcInteractEvent event) {
        String npcName = event.getNpc().getData().getName();
        OMCPlayer player = OMCPlayer.of(event.getPlayer());

        for (DepositEvent e : OMCRegistry.DEPOSIT_EVENTS.values()) {
            if (e.getNpcId().equals(npcName)) {
                if (!DepositEventManager.isActive(e)) {
                    boolean ended = DepositEventManager.getInfo(e).map(DepositEventModel::isEnded).orElse(false);

                    player.message().sendError(
                            TranslationManager.translation((ended ? "feature.events.deposit.ended" : "feature.events.deposit.not_started"), e.getName())
                    );
                    player.closeInventory();
                } else {
                    new DepositMenu(player, e).open();
                }
            }
        }
    }

}
