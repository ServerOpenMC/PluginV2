package fr.openmc.core.bootstrap.hooks;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.NotLoadInUnitTest;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.plugin.PluginManager;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base pour les hooks vers des plugins externes.
 * Detecte l'etat d'activation et cache le resultat par type de hook.
 */
public abstract class Hooks {
    private static final Map<Class<? extends Hooks>, Boolean> ENABLED = new ConcurrentHashMap<>();

    public boolean arePluginEnabled() {
        if (!(this instanceof HttpsHook)) return true;
        boolean enabled = true;

        Set<String> pluginsName = getPluginsName();
        for (String pluginName : pluginsName) {
            PluginManager pluginManager = OMCPlugin.getInstance().getServer().getPluginManager();
            boolean pluginEnable = pluginManager.getPlugin(pluginName) != null
                    && pluginManager.isPluginEnabled(pluginName);

            if (!pluginEnable) return false;
        }

        return enabled;
    }
    /**
     * Verifie la presence du plugin cible, puis initialise le hook si actif.
     */
    public void startInit() {

        if (this instanceof HttpsHook httpsHook) {
            String hookName = httpsHook.getName();
            try {
                httpsHook.init();
                OMCLogger.successFormatted("Hook " + hookName + " activé.");
            } catch (Exception e) {
                OMCLogger.errorFormatted("Hook " + hookName + " non activé.");
                OMCLogger.error(e.getMessage(), e);
            }
            return;
        }

        String hookName = this.getClass().getSimpleName();
        try {
            boolean enabled = arePluginEnabled();
            if (enabled) {
                ENABLED.put(getClass(), enabled);
                this.init();
                    OMCLogger.successFormatted("Hook " + hookName + " activé.");
            }
        } catch (Throwable e) {
            ENABLED.remove(getClass());
            OMCLogger.errorFormatted("Hook " + hookName + " non activé.");
            OMCLogger.error(e.getMessage(), e);
        }
    }

    /**
     * Verifie la presence du plugin cible, puis save le hook si actif.
     */
    public void startSave() {
        if (this instanceof HttpsHook httpsHook) {
            String hookName = httpsHook.getName();
            try {
                httpsHook.save();
                OMCLogger.successFormatted("Hook " + hookName + " desactivé avec succes.");
            } catch (Exception e) {
                OMCLogger.errorFormatted("Hook " + hookName + " a rencontré une erreur lors du save.");
                OMCLogger.error(e.getMessage(), e);
            }
            return;
        }

        boolean enabled = arePluginEnabled() && ENABLED.get(getClass()) != null
                && ENABLED.get(getClass());

        String hookName = this.getClass().getSimpleName();
        if (enabled) {
            ENABLED.remove(getClass());
            save();
            OMCLogger.successFormatted("Hook " + hookName + " désactivé avec succès.");
            return;
        }
        OMCLogger.errorFormatted("Hook " + hookName + " désactivé avec échec.");
    }

    /**
     * Delegue l'initialisation base de donnees si le hook la supporte.
     *
     * @param connectionSource Source de connexion ORMLite
     * @throws SQLException Si l'initialisation DB échoue
     */
    public final void startDB(ConnectionSource connectionSource) throws SQLException {
        if (this instanceof NotLoadInUnitTest && OMCPlugin.isUnitTestVersion()) return;
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
    protected abstract Set<String> getPluginsName();

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
