package fr.openmc.core.features.dream.mecanism.rng;

import fr.openmc.core.utils.RngUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class DreamRngLootManager {
    public static void sendMessageLoot(DreamRngLootEvent event) {
        double chance = event.getChance() != null ? event.getChance() : 0.0;
        String prefix;
        if (chance <= 0.001) { // 0.1%
            prefix = "§b§lCRAZY RARE LOOT!";
        } else if (chance <= 0.05) { // 5%
            prefix = "§6§lWOW!";
        } else if (chance <= 0.1) { // 10%
            prefix = "§5§lPRETTY NICE!";
        } else if (chance <= 0.25) { // 25%
            prefix = "§9§lNICE!";
        } else {
            prefix = "§7§lGOOD!";
        }

        sendSoundLoot(event);
        MessagesManager.sendMessage(event.getPlayer(),
                Component.text(prefix + " §fVous avez obtenu §e" + event.getAmount() + "x ")
                .append(event.getItem().displayName())
                        .append(Component.text("§7 " + event.getChance() == null ? "" : " §b("+ Math.round(event.getChance() * 1000.0) / 10.0+"% ★)" + "§f!")), Prefix.DREAM, MessageType.INFO,false);
    }

    private static void sendSoundLoot(DreamRngLootEvent event) {
        double chance = event.getChance() != null ? event.getChance() : 0.0;
        Player player = event.getPlayer();

        RngUtils.sendSoundRng(player, chance);
    }
}
