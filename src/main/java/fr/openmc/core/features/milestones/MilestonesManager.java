package fr.openmc.core.features.milestones;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.milestones.listeners.PlayerJoin;
import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import fr.openmc.core.features.milestones.tutorial.listeners.TutorialBossBarEvent;
import fr.openmc.core.features.quests.objects.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.*;

public class MilestonesManager {
    private static final Set<Milestone> milestones = new HashSet<>();
    ;

    private static Dao<MilestoneModel, String> millestoneDao;

    public MilestonesManager() {
        registerMilestones(
                new TutorialMilestone()
        );

        loadMilestonesData();

        registerMilestoneCommand();

        OMCPlugin.registerEvents(
                new PlayerJoin(),
                new TutorialBossBarEvent()
        );
    }

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MilestoneModel.class);
        millestoneDao = DaoManager.createDao(connectionSource, MilestoneModel.class);
    }

    public static void loadMilestonesData() {
        try {
            List<MilestoneModel> milestoneData = millestoneDao.queryForAll();

            for (MilestoneModel data : milestoneData) {
                MilestoneType type = MilestoneType.valueOf(data.getType());
                Milestone milestone = type.getMilestone();
                milestone.getPlayerData().put(data.getUUID(), data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMilestonesData() {
        try {
            for (Milestone milestone : milestones) {
                for (Map.Entry<UUID, MilestoneModel> entry : milestone.getPlayerData().entrySet()) {
                    MilestoneModel model = entry.getValue();
                    millestoneDao.createOrUpdate(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<UUID, MilestoneModel> getMilestoneData(Milestone milestone) {
        return milestone.getPlayerData();
    }

    public static Map<UUID, MilestoneModel> getMilestoneData(MilestoneType type) {
        return type.getMilestone().getPlayerData();
    }

    public static int getPlayerStep(MilestoneType type, UUID playerUUID) {
        return getMilestoneData(type).get(playerUUID).getStep();
    }

    public static void setPlayerStep(MilestoneType type, UUID playerUUID, int step) {
        getMilestoneData(type).get(playerUUID).setStep(step);
    }

    public static int getPlayerStep(MilestoneType type, Player player) {
        return getPlayerStep(type, player.getUniqueId());
    }

    public static void setPlayerStep(MilestoneType type, Player player, int step) {
        setPlayerStep(type, player.getUniqueId(), step);
    }

    public static Set<Milestone> getRegisteredMilestones() {
        return milestones;
    }

    /**
     * Enregistre tous les milestones
     */
    public void registerMilestones(Milestone... milestonesRegister) {
        for (Milestone milestone : milestonesRegister) {
            if (milestone == null) continue;
            milestones.add(milestone);

            registerQuestMilestone(milestone);
        }
    }

    public void registerMilestoneCommand() {
        CommandsManager.getHandler().register(new MilestoneCommand());
    }

    public void registerQuestMilestone(Milestone milestone) {
        for (Quest quest : milestone.getSteps()) {
            if (quest instanceof Listener listener) {
                OMCPlugin.registerEvents(listener);
            }
        }
    }
}
