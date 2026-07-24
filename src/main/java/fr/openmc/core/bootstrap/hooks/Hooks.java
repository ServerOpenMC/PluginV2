package fr.openmc.core.bootstrap.hooks;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.NotInUnitTest;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.plugin.PluginManager;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base pour les hooks vers des plugins externes.
 * Detecte l'etat d'activation et cache le resultat par type de hook.
 */
public abstract class Hooks {
    private static final Map<Class<? extends Hooks>, Boolean> ENABLED = new ConcurrentHashMap<>();

    /**
     * Verifie la presence du plugin cible, puis initialise le hook si actif.
     */
    public void startInit() {
        String pluginName = getPluginName();

        if (this instanceof HttpsHook httpsHook) {
            try {
                httpsHook.init();
            } catch (Exception e) {
                OMCLogger.errorFormatted("Hook " + pluginName + " non activé.");
                OMCLogger.error(e.getMessage(), e);
            }
            OMCLogger.successFormatted("Hook " + pluginName + " activé.");
            return;
        }

        PluginManager pluginManager = OMCPlugin.getInstance().getServer().getPluginManager();
        boolean enabled = pluginManager.getPlugin(pluginName) != null
                && pluginManager.isPluginEnabled(pluginName);
        ENABLED.put(getClass(), enabled);
        if (enabled) {
            init();
            OMCLogger.successFormatted("Hook " + pluginName + " activé.");
            return;
        }
        OMCLogger.errorFormatted("Hook " + pluginName + " non activé.");
    }

    /**
     * Verifie la presence du plugin cible, puis save le hook si actif.
     */
    public void startSave() {
        String pluginName = getPluginName();

        PluginManager pluginManager = OMCPlugin.getInstance().getServer().getPluginManager();
        boolean enabled = pluginManager.getPlugin(pluginName) != null
                && pluginManager.isPluginEnabled(pluginName)
                && ENABLED.get(getClass()) != null
                && ENABLED.get(getClass());

        if (enabled) {
            save();
            OMCLogger.successFormatted("Hook " + pluginName + " désactivé avec succès.");
            return;
        }
        OMCLogger.errorFormatted("Hook " + pluginName + " désactivé avec échec.");
    }

    /**
     * Delegue l'initialisation base de donnees si le hook la supporte.
     *
     * @param connectionSource Source de connexion ORMLite
     * @throws SQLException Si l'initialisation DB échoue
     */
    public final void startDB(ConnectionSource connectionSource) throws SQLException {
        if (this instanceof NotInUnitTest && OMCPlugin.isUnitTestVersion()) return;
        if (this instanceof HasDatabase dbHook) {
            dbHook.initDB(connectionSource);
        }
    }

    /**
     * Retourne l'etat d'activation en cache pour un hook.
     *
     * @param hookClass Type de hook
     * @return True si le hook est actif
     */
    public static boolean isEnabled(Class<? extends Hooks> hookClass) {
        return ENABLED.getOrDefault(hookClass, false);
    }

    /**
     * Nom du plugin externe a verifier.
     *
     * @return Nom du plugin cible
     */
    protected abstract String getPluginName();

    /**
     * Initialise les méthodes du hook lorsqu'il est actif.
     */
    protected void init() {
        // a @Override dans les classes si besoin
    }

    /**
     * Sauvegarde les méthodes du hook lorsqu'il est actif.
     */
    protected void save() {
        // a @Override dans les classes si besoin
    }
}
