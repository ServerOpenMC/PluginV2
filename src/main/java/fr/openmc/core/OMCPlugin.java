package fr.openmc.core;

import dev.xernas.menulib.MenuLib;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    private DatabaseManager dbManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configs = this.getConfig();

        /* EXTERNALS */
        MenuLib.init(this);

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        new ListenersManager();

        /* MOTD */
        MotdUtils motdChanger = new MotdUtils();
        motdChanger.startMOTD(this);

        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        try {
            dbManager.close();
        } catch (SQLException e) {
            getLogger().severe("Impossible de fermer la connection à la base de données");
        }
        getLogger().info("Plugin désactivé");
    }
}
