package fr.openmc.core.features.chatanimations.contents.quizz;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.integration.OMCLogger;

import java.sql.SQLException;
import java.util.UUID;

public class QuizSQLManager {

    private static Dao<DBQuizCount, UUID> dao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBQuizCount.class);
        dao = DaoManager.createDao(connectionSource, DBQuizCount.class);
    }

    public static int getCorrectAnswers(UUID uuid) {
        requireInitialized();

        try {
            DBQuizCount data = dao.queryForId(uuid);
            return data == null ? 0 : data.getCorrectAnswers();
        } catch (SQLException e) {
            OMCLogger.error("Failed to get quiz count for {}", uuid, e);
            return 0;
        }
    }

    public static synchronized void addCorrectAnswer(UUID uuid) {
        requireInitialized();

        try {
            DBQuizCount data = dao.queryForId(uuid);

            if (data == null) {
                dao.create(new DBQuizCount(uuid));
                data = dao.queryForId(uuid);
            }

            data.setCorrectAnswers(data.getCorrectAnswers() + 1);
            dao.update(data);

        } catch (SQLException e) {
            OMCLogger.error("Failed to add quiz count for {}", uuid, e);
        }
    }

    private static void requireInitialized() {
        if (dao == null) {
            throw new IllegalStateException(
                    "QuizSQLManager database has not been initialized."
            );
        }
    }
}


