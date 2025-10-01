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

    /**
     * Initialize the database for milestones.
     *
     * @param connectionSource the connection source to the database
     */
    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MilestoneModel.class);
        millestoneDao = DaoManager.createDao(connectionSource, MilestoneModel.class);
    }

    /**
     * Load all milestone data from the database.
     * This method retrieves all MilestoneModel entries and populates the player data for each milestone type.
     */
    public static void loadMilestonesData() {
        try {
            List<MilestoneModel> milestoneData = millestoneDao.queryForAll();

            for (MilestoneModel data : milestoneData) {
                MilestoneType type = MilestoneType.valueOf(data.getType());
                Milestone milestone = type.getMilestone();
                milestone.getPlayerData().put(data.getUUID(), data);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save all milestone data to the database.
     * This method iterates through each milestone and saves the player data for each milestone type.
     */
    public static void saveMilestonesData() {
        try {
            for (Milestone milestone : milestones) {
                for (Map.Entry<UUID, MilestoneModel> entry : milestone.getPlayerData().entrySet()) {
                    MilestoneModel model = entry.getValue();
                    millestoneDao.createOrUpdate(model);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the player data for a specific milestone.
     * @param milestone the milestone to get data for
     * @return a map of player UUIDs to their MilestoneModel
     */
    public static Map<UUID, MilestoneModel> getMilestoneData(Milestone milestone) {
        return milestone.getPlayerData();
    }

    /**
     * Get the player data for a specific milestone type.
     * @param type the type of milestone to get data for
     * @return a map of player UUIDs to their MilestoneModel
     */
    public static Map<UUID, MilestoneModel> getMilestoneData(MilestoneType type) {
        return type.getMilestone().getPlayerData();
    }

    /**
     * Get the player step for a specific milestone type and player UUID.
     * @param type the type of milestone
     * @param playerUUID the UUID of the player
     * @return the step of the milestone for the player
     */
    public static int getPlayerStep(MilestoneType type, UUID playerUUID) {
        return getMilestoneData(type).get(playerUUID).getStep();
    }

    /**
     * Get the player step for a specific milestone type and player.
     *
     * @param type the type of milestone
     * @param player the player to get the step for
     * @return the step of the milestone for the player
     */
    public static int getPlayerStep(MilestoneType type, Player player) {
        return getPlayerStep(type, player.getUniqueId());
    }

    /**
     * Set the player step for a specific milestone type and player UUID.
     * @param type the type of milestone
     * @param playerUUID the UUID of the player
     * @param step the step to set for the player
     */
    public static void setPlayerStep(MilestoneType type, UUID playerUUID, int step) {
        getMilestoneData(type).get(playerUUID).setStep(step);
    }

    /**
     * Set the player step for a specific milestone type and player.
     * @param type the type of milestone
     * @param player the player to set the step for
     * @param step the step to set for the player
     */
    public static void setPlayerStep(MilestoneType type, Player player, int step) {
        setPlayerStep(type, player.getUniqueId(), step);
    }

    /**
     * Get all registered milestones.
     * @return a set of all registered milestones
     */
    public static Set<Milestone> getRegisteredMilestones() {
        return milestones;
    }

    /**
     * Register one or more milestones.
     * This method adds the provided milestones to the internal set and registers their quests.
     * @param milestonesRegister the milestones to register
     */
    public void registerMilestones(Milestone... milestonesRegister) {
        for (Milestone milestone : milestonesRegister) {
            if (milestone == null) continue;
            milestones.add(milestone);

            registerQuestMilestone(milestone);
        }
    }

    /**
     * Register the Milestone command.
     * This method registers the MilestoneCommand with the CommandsManager.
     */
    public void registerMilestoneCommand() {
        CommandsManager.getHandler().register(new MilestoneCommand());
    }

    /**
     * Register quests associated with a milestone.
     * This method iterates through the steps of the milestone and registers any Listener instances.
     * @param milestone the milestone whose quests are to be registered
     */
    public void registerQuestMilestone(Milestone milestone) {
        for (Quest quest : milestone.getSteps()) {
            if (quest instanceof Listener listener) {
                OMCPlugin.registerEvents(listener);
            }
        }
    }
}
