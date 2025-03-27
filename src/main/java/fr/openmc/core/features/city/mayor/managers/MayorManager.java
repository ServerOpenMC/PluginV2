package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.MayorElector;
import fr.openmc.core.features.city.mayor.listeners.PhaseListener;
import fr.openmc.core.features.contest.ContestData;
import fr.openmc.core.features.contest.ContestPlayer;
import fr.openmc.core.features.contest.managers.ColorUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MayorManager {
    @Getter
    static MayorManager instance;

    private final OMCPlugin plugin;

    public int phaseMayor;
    public HashMap<City, Mayor> cityMayor = new HashMap<>();
    public HashMap<City, MayorElector> cityElections = new HashMap<>();
    public HashMap<UUID, City> playerHasVoted = new HashMap<>();

    public MayorManager(OMCPlugin plugin) {
        instance = this;

        this.plugin = plugin;

        // LISTENERS
        new PhaseListener(plugin);
        if (CustomItemRegistry.hasItemsAdder()) {
            OMCPlugin.registerEvents(
                    //make listener for urne
            );
        }

        //COMMANDS
        CommandsManager.getHandler().register(
                //empty
        );

        loadCityMayors();
        loadPlayersHasVoted();
        loadElectorMayors();
    }

    public static void init_db(Connection conn) throws SQLException {
        // create city_mayor : contient l'actuel maire et les réformes actuelles
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_mayor (city_uuid VARCHAR(8) NOT NULL, mayorUUID VARCHAR(36) NOT NULL, mayorName VARCHAR(36) NOT NULL, mayorColor VARCHAR(36) NOT NULL, idPerk1 int(2), idPerk2 int(2), idPerk3 int(2), phase int(1))").executeUpdate();
        // create city_election : contient les membres d'une ville ayant participé pour etre maire
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_election (city_uuid VARCHAR(8) NOT NULL, electorUUID VARCHAR(36) NOT NULL, electorName VARCHAR(36) NOT NULL, electorColor VARCHAR(36) NOT NULL, idChoicePerk2 int(2), idChoicePerk3 int(2), vote int(5))").executeUpdate();
        // create city_voted : contient les membres d'une ville ayant deja voté
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_voted (city_uuid VARCHAR(8) NOT NULL, voterUUID VARCHAR(36) NOT NULL").executeUpdate();

    }

    // Load and Save Data Methods
    public void loadCityMayors() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_mayor WHERE 1")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                City city = CityManager.getCity(city_uuid);
                String mayor_uuid = result.getString("mayorUUID");
                String mayor_name = result.getString("mayorName");
                NamedTextColor mayor_color = NamedTextColor.NAMES.valueOr(result.getString("mayorColor"), NamedTextColor.WHITE);
                int idPerk1 = result.getInt("idPerk1");
                int idPerk2 = result.getInt("idPerk2");
                int idPerk3 = result.getInt("idPerk3");
                int phase = result.getInt("phase");

                cityMayor.put(city, new Mayor(city, mayor_name, mayor_uuid, mayor_color, idPerk1, idPerk2, idPerk3, phase));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCityMayors() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO city_mayor (city_uuid, mayorUUID, mayorName, mayorColor, idPerk1, idPerk2, idPerk3, phase) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "city_uuid = VALUES(city_uuid), mayorUUID = VALUES(mayorUUID), mayorName = VALUES(mayorName), mayorColor = VALUES(mayorColor), idPerk1 = VALUES(idPerk1), idPerk2 = VALUES(idPerk2), idPerk3 = VALUES(idPerk3), phase = VALUES(phase)"
        )) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui sont maire...");
            cityMayor.forEach((city, mayor) -> {
                try {
                    statement.setString(1, city.getUUID());
                    statement.setString(2, mayor.getMayorUUID());
                    statement.setString(3, mayor.getMayorName());
                    statement.setString(4, mayor.getMayorColor().toString());
                    statement.setInt(5, mayor.getIdPerk1());
                    statement.setInt(6, mayor.getIdPerk2());
                    statement.setInt(7, mayor.getIdPerk3());
                    statement.setInt(8, mayor.getPhase());

                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            statement.executeBatch();

            plugin.getLogger().info("Sauvegarde des données des Joueurs qui sont maire réussi.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Echec de la sauvegarde des données des Joueurs qui sont maire.");
            e.printStackTrace();
        }
    }

    public void loadElectorMayors() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_election")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                City city = CityManager.getCity(city_uuid);
                String elector_uuid = result.getString("electorUUID");
                String elector_name = result.getString("electorName");
                NamedTextColor elector_color = NamedTextColor.NAMES.valueOr(result.getString("electorColor"), NamedTextColor.WHITE);
                int idChoicePerk2 = result.getInt("idChoicePerk2");
                int idChoicePerk3 = result.getInt("idChoicePerk3");
                int vote = result.getInt("vote");

                cityElections.put(city, new MayorElector(city, elector_name, elector_uuid, elector_color, idChoicePerk2, idChoicePerk3, vote));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveElectorMayors() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO city_election (city_uuid, electorUUID, electorName, electorColor, idChoicePerk2, idChoicePerk3, vote) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "city_uuid = VALUES(city_uuid), electorUUID = VALUES(electorUUID), electorName = VALUES(electorName), electorColor = VALUES(electorColor), idChoicePerk2 = VALUES(idChoicePerk2), idChoicePerk3 = VALUES(idChoicePerk3), vote = VALUES(vote)"
        )) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui se sont présenté...");
            cityElections.forEach((city, elector) -> {
                try {
                    statement.setString(1, city.getUUID());
                    statement.setString(2, elector.getElectorUUID());
                    statement.setString(3, elector.getElectorName());
                    statement.setString(4, elector.getElectorColor().toString());
                    statement.setInt(5, elector.getIdChoicePerk2());
                    statement.setInt(6, elector.getIdChoicePerk3());
                    statement.setInt(7, elector.getVote());

                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            statement.executeBatch();

            plugin.getLogger().info("Sauvegarde des données des Joueurs qui se sont présenté réussi.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Echec de la sauvegarde des données des Joueurs qui se sont présenté.");
            e.printStackTrace();
        }
    }

    // Load and Save Data Methods
    public void loadPlayersHasVoted() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_voted")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                String voter_uuid = result.getString("voterUUID");

                playerHasVoted.put(UUID.fromString(voter_uuid), CityManager.getCity(city_uuid));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePlayersHasVoted() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO city_voted (city_uuid, voterUUID) " +
                        "VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "city_uuid = VALUES(city_uuid), voterUUID = VALUES(voterUUID)"
        )) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui ont voté pour un maire...");
            playerHasVoted.forEach((voter_uuid, city) -> {
                try {
                    statement.setString(1, voter_uuid.toString());
                    statement.setString(2, city.getUUID());

                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            statement.executeBatch();

            plugin.getLogger().info("Sauvegarde des données des Joueurs qui ont voté pour un maire réussi.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Echec de la sauvegarde des données des Joueurs qui ont voté pour un maire.");
            e.printStackTrace();
        }
    }

    // setup elections
    public void initPhase1() {
        phaseMayor = 1;
        //todo: ouverture des elections

        //todo: si nb player < 4 alors owner 3 pick perk
        // sinon lancer election
    }

    public void initPhase2() {
        phaseMayor = 2;
        //si nb player < 4 alors activé les perk déjà mis dans mayorCity
        // donc faire systeme pour que les perks s'activent que quand phase =2

        //todo: changer de maire

        //todo: si aucune activité alors randomPick et owner maire
    }
}
