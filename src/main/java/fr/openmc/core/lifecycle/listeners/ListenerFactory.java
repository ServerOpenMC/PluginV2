package fr.openmc.core.lifecycle.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@FunctionalInterface
public interface ListenerFactory {
    Listener create() throws NoClassDefFoundError;

    default Listener create(boolean register) {
        Server server = Bukkit.getServer();
        JavaPlugin plugin = OMCPlugin.getInstance();
        Listener listener = null;

        try {
            listener = this.create();
            if (listener != null && register) {
                server.getPluginManager().registerEvents(listener, plugin);
            }
        } catch (NoClassDefFoundError e) {
            if (listener == null) return null;
            OMCLogger.error("Erreur lors de l'enregistrement du listener " + listener.getClass().getSimpleName());
            OMCLogger.error(e.getMessage());
        }
        return listener;
    }
}