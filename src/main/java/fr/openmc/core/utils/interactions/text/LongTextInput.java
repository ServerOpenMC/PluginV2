package fr.openmc.core.utils.interactions.text;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class LongTextInput implements Listener {

    private static final Map<UUID, Consumer<String>> playerInputs = new HashMap<>();

    public static void sendInput(Player player, String startMessage, Consumer<String> callback) {
        playerInputs.put(player.getUniqueId(), callback);
        player.closeInventory();
        player.sendMessage(startMessage);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (playerInputs.containsKey(player.getUniqueId())) {
            event.setCancelled(true);

            Consumer<String> callback = playerInputs.remove(player.getUniqueId());

            if (event.message() instanceof TextComponent textComponent) {
                String string = textComponent.content();
                if (string.contains("cancel")) {
                    player.sendMessage("§eVous avez annuler l'action !");
                    callback.accept(null);
                }
                callback.accept(string);
            }
        }
    }
}
