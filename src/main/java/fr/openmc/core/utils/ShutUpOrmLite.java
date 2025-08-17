package fr.openmc.core.utils;

import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.LocalLogBackend;

public class ShutUpOrmLite extends LocalLogBackend {
    private final String classLabel;

    public ShutUpOrmLite(String classLabel) {
        super(classLabel);
        this.classLabel = classLabel;
    }

    @Override
    public boolean isLevelEnabled(Level level) {
        return true;
    }

    @Override
    public void log(Level level, String msg) {
        if (classLabel.contains("com.j256.ormlite.table.TableUtils") || msg.contains("DaoManager created dao for class class"))
            return;

        super.log(level, msg);
    }

    @Override
    public void log(Level level, String msg, Throwable throwable) {
        if (classLabel.contains("com.j256.ormlite.table.TableUtils") || msg.contains("DaoManager created dao for class class"))
            return;

        super.log(level, msg, throwable);
    }
}
