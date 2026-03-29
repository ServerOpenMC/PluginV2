package fr.openmc.core.utils.init;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;

import java.sql.SQLException;

public abstract class Feature {
    protected boolean initialize = false;

    public void startInit() {
        if (this instanceof NotUnitTestFeature && OMCPlugin.isUnitTestVersion()) return;
        try {
            init();
            initialize = true;
        } catch (Exception e) {
            initialize = false;
            throw e;
        }
    }

    public final void startDB(ConnectionSource connectionSource) throws SQLException {
        if (this instanceof NotUnitTestFeature && OMCPlugin.isUnitTestVersion()) return;
        if (this instanceof DatabaseFeature dbF) {
            dbF.initDB(connectionSource);
        }
    }

    public final void startSave() {
        if (!initialize) return;
        save();
    }

    public final boolean isInitialized() {
        return initialize;
    }

    protected abstract void init();
    protected abstract void save();
}
