package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinMessageListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LuckPerms lp = OMCPlugin.getInstance().lpApi;

        User userlp = lp.getUserManager().getUser(player.getUniqueId());
        QueryOptions queryOptions = lp.getContextManager().getQueryOptions(userlp).orElse(QueryOptions.defaultContextualOptions());


        event.joinMessage(Component.text("§8[§a§l+§8] §r" + (userlp.getCachedData().getMetaData(queryOptions).getPrefix() != null ? userlp.getCachedData().getMetaData(queryOptions).getPrefix().replace("&", "§") : "") + "" + player.getName()));
    }
}
