package fr.openmc.core.features.milestones;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import fr.openmc.core.features.quests.objects.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.*;

public class MilestonesManager {
    private final Set<Milestone> milestones;

    private static Dao<DBMilestone, String> millestoneDao;

    public MilestonesManager() {
        this.milestones = new HashSet<>();

        registerMilestones(
                new TutorialMilestone()
        );

        loadMilestoneData();
    }

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBMilestone.class);
        millestoneDao = DaoManager.createDao(connectionSource, DBMilestone.class);
    }

    public static void loadMilestoneData() {
        try {
            List<DBMilestone> milestoneData = millestoneDao.queryForAll();

            for (DBMilestone data : milestoneData) {
                MilestoneType type = MilestoneType.valueOf(data.getType());
                Milestone milestone = type.getMilestone();
                milestone.getPlayerData().put(data.getUUID(), data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<UUID, DBMilestone> getMilestoneData(Milestone milestone) {
        return milestone.getPlayerData();
    }

    public static Map<UUID, DBMilestone> getMilestoneData(MilestoneType type) {
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

    /**
     * Enregistre tous les milestones
     */
    public void registerMilestones(Milestone... milestones) {
        for (Milestone milestone : milestones) {
            if (milestone != null) {
                this.milestones.add(milestone);
                registerQuestMilestone(milestone);
            }
        }
    }

    public void registerMilestoneCommand() {
        CommandsManager.getHandler().register(new MilestoneCommand(this.milestones));
    }

    public void registerQuestMilestone(Milestone milestone) {
        for (Quest quest : milestone.getSteps()) {
            if (quest instanceof Listener listener) {
                Bukkit.getPluginManager().registerEvents(listener, OMCPlugin.getInstance());
            }
        }
    }
}
