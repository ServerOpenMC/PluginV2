package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        User userlp = OMCPlugin.getInstance().lpApi.getUserManager().getUser(player.getUniqueId());
        QueryOptions queryOptions = OMCPlugin.getInstance().lpApi.getContextManager().getQueryOptions(userlp).orElse(QueryOptions.defaultContextualOptions());


        event.joinMessage(Component.text("§8[§a§l+§8] §r" + (userlp.getCachedData().getMetaData(queryOptions).getPrefix() != null ? userlp.getCachedData().getMetaData(queryOptions).getPrefix().replace("&", "§") : "") + "" + player.getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        User userlp = OMCPlugin.getInstance().lpApi.getUserManager().getUser(player.getUniqueId());
        QueryOptions queryOptions = OMCPlugin.getInstance().lpApi.getContextManager().getQueryOptions(userlp).orElse(QueryOptions.defaultContextualOptions());


        event.quitMessage(Component.text("§8[§c§l-§8] " + (userlp.getCachedData().getMetaData(queryOptions).getPrefix() != null ? userlp.getCachedData().getMetaData(queryOptions).getPrefix().replace("&", "§") : "") + "" + player.getName()));
    }
}
