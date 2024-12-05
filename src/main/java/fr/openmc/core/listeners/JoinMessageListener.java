package fr.openmc.core.listeners;

import fr.openmc.core.utils.LuckPermsAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinMessageListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String prefix = LuckPermsAPI.getPrefix(player);

        event.joinMessage(Component.text("§8[§a§l+§8] §r" + prefix.replace("&", "§") + player.getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String prefix = LuckPermsAPI.getPrefix(player);


        event.quitMessage(Component.text("§8[§c§l-§8] §r" + prefix.replace("&", "§") + player.getName()));
    }
}
