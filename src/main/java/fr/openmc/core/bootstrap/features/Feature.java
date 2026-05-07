package fr.openmc.core.bootstrap.features;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.*;
import org.bukkit.event.Listener;

import java.sql.SQLException;

/**
 * Base des features OpenMC.
 * Gere le cycle d'initialisation, l'initialisation DB optionnelle et la sauvegarde.
 */
public abstract class Feature {
    protected boolean initialize = false;

    /**
     * Lance l'initialisation avec des règles en fonction des interfaces mises (NotUnitTest, LoadIfEnable<Hook>)
     */
    public final void startInit() {
        // Condition d'initialisation (si feature ne doit pas etre lancé dans les tests ou que elle nécéssite un hook)
        if (this instanceof NotInUnitTest && OMCPlugin.isUnitTestVersion()) {
            OMCPlugin.getInstance().logErrorMessage("Feature " + this.getClass().getSimpleName() + " non initialisée dans les Unit Tests");
            return;
        }
        if (this instanceof LoadIfEnable<?> loadIfEnable && !loadIfEnable.shouldLoad()) {
            OMCPlugin.getInstance().logErrorMessage("Feature " + this.getClass().getSimpleName() + " non initialisée car le hook associé n'est pas activé");
            return;
        }

        try {
            init();

            // Enregistre les listeners
            if (this instanceof HasListeners hasListeners) {
                for (Listener listener : hasListeners.getListeners()) {
                    if (this instanceof NotInUnitTest && OMCPlugin.isUnitTestVersion()) {
                        OMCPlugin.getInstance().logErrorMessage("Listener " + listener.getClass().getSimpleName() + " de Feature " + this.getClass().getSimpleName() + " non chargée dans les Unit Tests");
                        continue;
                    }

                    if (this instanceof LoadIfEnable<?> loadIfEnable && !loadIfEnable.shouldLoad()) {
                        OMCPlugin.getInstance().logErrorMessage("Listener " + listener.getClass().getSimpleName() + " de Feature " + this.getClass().getSimpleName() + " non initialisée car le hook associé n'est pas activé");
                        continue;
                    }

                    OMCPlugin.registerEvents(listener);
                }
            }
            // Enregistre les commands
            if (this instanceof HasCommands hasCommands) {
                    for (Object command : hasCommands.getCommands()) {
                        CommandsManager.getHandler().register(command);
                    }
                }

            initialize = true;
            OMCPlugin.getInstance().logSuccessMessage("Feature " + this.getClass().getSimpleName() + " initialisée correctement.");
        } catch (Exception e) {
            initialize = false;
            OMCPlugin.getInstance().logErrorMessage("Feature " + this.getClass().getSimpleName() + " non initialisée.");
            throw e;
        }
    }

    /**
     * Delegue l'initialisation base de donnees si la feature la supporte.
     *
     * @param connectionSource Source de connexion ORMLite
     * @throws SQLException Si l'initialisation DB échoue
     */
    public final void startDB(ConnectionSource connectionSource) throws SQLException {
        if (this instanceof NotInUnitTest && OMCPlugin.isUnitTestVersion()) return;
        if (this instanceof DatabaseFeature dbF) {
            dbF.initDB(connectionSource);
        }
    }

    /**
     * Sauvegarde la feature si elle a ete initialisee.
     */
    public final void startSave() {
        if (!initialize) return;
        if (this instanceof NotInUnitTest && OMCPlugin.isUnitTestVersion()) return;
        save();
    }

    /**
     * Indique si la feature a ete initialisee avec succes.
     *
     * @return True si l'initialisation a reussi
     */
    public final boolean isInitialized() {
        return initialize;
    }

    /**
     * Initialise la feature.
     */
    protected void init() {
        // doit etre @Override dans les features qui ont une initialisation a faire
    }

    /**
     * Sauvegarde l'état de la feature.
     */
    protected void save() {
        // doit etre @Override dans les features qui ont une save a faire
    }
}
