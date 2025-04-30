package fr.openmc.core.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UserUnloadListener {

    public static void register(JavaPlugin plugin, LuckPerms luckPerms) {
        EventBus eventBus = luckPerms.getEventBus();

        eventBus.subscribe(plugin, UserUnloadEvent.class, event -> {
            System.out.println("[DEBUG] LuckPerms UserUnloadEvent : " + event.getUser().getUsername());
            System.out.println("*ceci est normal, j'en ai besoin pour capter le bug avec luck perm*");
            new Exception("debug UserUnloadEvent").printStackTrace();
        });
    }
}
