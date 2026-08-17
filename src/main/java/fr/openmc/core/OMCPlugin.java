package fr.openmc.core;

import com.j256.ormlite.logger.LoggerFactory;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.hooks.*;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.hooks.Hooks;
import fr.openmc.core.lifecycle.integration.DatabaseManager;
import fr.openmc.core.lifecycle.integration.ErrorReporter;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.listeners.ItemsAddersListener;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import io.papermc.paper.datapack.Datapack;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Plugin principal OpenMC.
 * Gère le cycle de vie, les features et les hooks globaux.
 */
public class OMCPlugin extends JavaPlugin {
    @Getter
    static OMCPlugin instance;
    @Getter
    static FileConfiguration configs;

    public static final String VANISH_META_KEY = "omcstaff.vanished";

    // ** Registry of OMC Plugin Hooks
    public final List<Hooks> REGISTRY_HOOKS = new ArrayList<>(List.of(
            new ProtocolLibHook(),
            new LuckPermsHook(),
            new PapiHook(),
            new WorldGuardHook(),
            new ItemsAdderHook(),
            new FancyNpcsHook(),
            new GitHubHook()
    ));

    @Override
    public void onLoad() {
        LoggerFactory.setLogBackendFactory(DatabaseManager.ShutUpOrmLite::new);
    }

    /**
     * Initialise la configuration, les hooks, les managers et les features.
     */
    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();
        OMCLogger.setRuntimeLogger(this.getSLF4JLogger());
        DatabaseManager.init();

        /* EXTERNALS */
        MenuLib.init(this);

        /* HOOKS */
        REGISTRY_HOOKS.forEach(Hooks::startInit);

        if (!OMCPlugin.isUnitTestVersion() && ProtocolLibHook.isEnable())
            PacketMenuLib.init(this);

        OMCLogger.logLoadMessage(this);
        if (!OMCPlugin.isUnitTestVersion()) {
            Datapack pack = this.getServer().getDatapackManager().getPack(getPluginMeta().getName() + "/omc");
            if (pack != null) {
                if (pack.isEnabled()) {
                    OMCLogger.successFormatted("Lancement du datapack réussi");
                } else {
                    OMCLogger.error("Lancement du datapack échoué");
                }
            }
        }
        new ErrorReporter();

        /* MANAGERS */
        CommandsManager.init();
        ListenersManager.init();

        /* REGISTRIES */
        OMCRegistry.initAll();

        // * Si ItemsAdder n'est pas présent, alors on charge les dernières features maintenant
        if (!ItemsAdderHook.isEnable()) {
            loadAfterItemsAdder();
        }
    }

    /**
     * Charge les registres et features qui doivent être lancé apres ItemsAdder
     */
    public void loadAfterItemsAdder() {
        ItemsAddersListener.setLoaded(true);

        /* LOAD ITEMS ADDER CONTENTS */
        ItemsAdderHook.loadContents();

        /* REGISTRIES */
        OMCRegistry.postInitAll();

        if (WorldGuardHook.isEnable()) {
            ParticleUtils.spawnParticlesInRegion("spawn", Bukkit.getWorld("world"), Particle.CHERRY_LEAVES, 50, 70, 130);
        }
    }

    /**
     * Sauvegarde l'état des features
     */
    @Override
    public void onDisable() {
        // ** SAVE **
        /* HOOKS */
        REGISTRY_HOOKS.forEach(Hooks::startSave);

        /* REGISTRIES */
        OMCRegistry.stopAll();

        // - Close all inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }

        // If the plugin crashes, shutdown the server
        if (!isUnitTestVersion())
            if (!Bukkit.isStopping())
                Bukkit.shutdown();
    }

    /**
     * Enregistre une liste de listeners Bukkit sur l'instance du plugin.
     *
     * @param listeners Listeners à enregistrer
     */
    public static void registerEvents(ListenerFactory... listeners) {
        ListenersManager.registerEvents(listeners);
    }

    /**
     * Enregistre une liste de listeners Bukkit sur l'instance du plugin.
     *
     * @param listeners Listeners à enregistrer
     */
    public static void registerEvents(Collection<ListenerFactory> listeners) {
        registerEvents(listeners.toArray(new ListenerFactory[0]));
    }

    /**
     * Indique si le plugin tourne dans les tests unitaires.
     *
     * @return True si l'instance serveur correspond à MockBukkit
     */
    public static boolean isUnitTestVersion() {
        return OMCPlugin.instance.getServer().getVersion().contains("MockBukkit");
    }
}
