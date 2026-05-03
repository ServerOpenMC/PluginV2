package fr.openmc.core.bootstrap.hooks;

import fr.openmc.core.OMCPlugin;
import org.bukkit.plugin.PluginManager;

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

        PluginManager pluginManager = OMCPlugin.getInstance().getServer().getPluginManager();
        boolean enabled = pluginManager.getPlugin(pluginName) != null
                && pluginManager.isPluginEnabled(pluginName);
        ENABLED.put(getClass(), enabled);
        if (enabled) {
            init();
            OMCPlugin.getInstance().logSuccessMessage("Hook " + pluginName + " activé.");
            return;
        }
        OMCPlugin.getInstance().logErrorMessage("Hook " + pluginName + " non activé.");
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
     * Initialise le hook lorsqu'il est actif.
     */
    protected void init() {
        // a @Override dans les classes si besoin
    };
}
