package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.MayorElector;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.listeners.PhaseListener;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


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
    public Map<City, List<MayorElector>> cityElections = new HashMap<>(){};
    public Map<UUID, MayorElector> playerHasVoted = new HashMap<>();

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

        loadMayorConstant();
        loadCityMayors();
        loadPlayersHasVoted();
        loadElectorMayors();

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("===== MayorManager Debug =====");

                Bukkit.getLogger().info("City Mayors:");
                System.out.println(cityMayor);
                for (Map.Entry<City, Mayor> entry : cityMayor.entrySet()) {
                    Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue().getMayorName() + " " + entry.getValue().getMayorUUID());
                }

                Bukkit.getLogger().info("City Elections:");
                for (Map.Entry<City, List<MayorElector>> entry : cityElections.entrySet()) {
                    Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue());
                }

                Bukkit.getLogger().info("Player Votes:");
                for (Map.Entry<UUID, MayorElector> entry : playerHasVoted.entrySet()) {
                    Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue().getElectorName());
                }

                Bukkit.getLogger().info("================================");
            }
        }.runTaskTimer(plugin, 0, 600L); // 600 ticks = 30 secondes
    }

    public static void init_db(Connection conn) throws SQLException {
        // create city_mayor : contient l'actuel maire et les réformes actuelles
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_mayor (city_uuid VARCHAR(8), mayorUUID VARCHAR(36), mayorName VARCHAR(36), mayorColor VARCHAR(36), idPerk1 int(2), idPerk2 int(2), idPerk3 int(2), phase int(1))").executeUpdate();
        // create city_election : contient les membres d'une ville ayant participé pour etre maire
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_election (city_uuid VARCHAR(8) NOT NULL, electorUUID VARCHAR(36) UNIQUE NOT NULL, electorName VARCHAR(36) NOT NULL, electorColor VARCHAR(36) NOT NULL, idChoicePerk2 int(2), idChoicePerk3 int(2), vote int(5))").executeUpdate();
        // create city_voted : contient les membres d'une ville ayant deja voté
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_voted (city_uuid VARCHAR(8) NOT NULL, voterUUID VARCHAR(36) UNIQUE NOT NULL, electorUUID VARCHAR(36) NOT NULL)").executeUpdate();
        // create constants : contient une information universelle pour tout le monde
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS mayor_constants (mayorPhase int(1))").executeUpdate();
        PreparedStatement state = conn.prepareStatement("SELECT COUNT(*) FROM mayor_constants");
        ResultSet rs = state.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            PreparedStatement states = conn.prepareStatement("INSERT INTO mayor_constants (mayorPhase) VALUES (1)");
            states.executeUpdate();
        }
    }

    // Load and Save Data Methods
    public void loadMayorConstant() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM mayor_constants WHERE 1")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                phaseMayor = result.getInt("mayorPhase");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMayorConstant() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("UPDATE mayor_constants SET mayorPhase = ?")) {
            plugin.getLogger().info("Sauvegarde des constantes pour les Maires...");
            states.setInt(1, phaseMayor);

            states.executeUpdate();
            plugin.getLogger().info("Sauvegarde des constantes pour les Maires réussi.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Echec de la sauvegarde des constantes pour les Maires.");
            throw new RuntimeException(e);
        }
    }

    public void loadCityMayors() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_mayor WHERE 1")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                City city = CityManager.getCity(city_uuid);
                UUID mayor_uuid = UUID.fromString(result.getString("mayorUUID"));
                String mayor_name = result.getString("mayorName");
                NamedTextColor mayor_color = NamedTextColor.NAMES.valueOr(result.getString("mayorColor"), NamedTextColor.WHITE);
                int idPerk1 = result.getInt("idPerk1");
                int idPerk2 = result.getInt("idPerk2");
                int idPerk3 = result.getInt("idPerk3");

                cityMayor.put(city, new Mayor(city, mayor_name, mayor_uuid, mayor_color, idPerk1, idPerk2, idPerk3));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCityMayors() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO city_mayor (city_uuid, mayorUUID, mayorName, mayorColor, idPerk1, idPerk2, idPerk3) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "city_uuid = VALUES(city_uuid), mayorUUID = VALUES(mayorUUID), mayorName = VALUES(mayorName), mayorColor = VALUES(mayorColor), idPerk1 = VALUES(idPerk1), idPerk2 = VALUES(idPerk2), idPerk3 = VALUES(idPerk3)"
        )) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui sont maire...");
            cityMayor.forEach((city, mayor) -> {
                try {
                    statement.setString(1, city.getUUID());
                    statement.setString(2, mayor.getMayorUUID().toString());
                    statement.setString(3, mayor.getMayorName());
                    statement.setString(4, mayor.getMayorColor().toString());
                    statement.setInt(5, mayor.getIdPerk1());
                    statement.setInt(6, mayor.getIdPerk2());
                    statement.setInt(7, mayor.getIdPerk3());

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
                UUID elector_uuid = UUID.fromString(result.getString("electorUUID"));
                String elector_name = result.getString("electorName");
                NamedTextColor elector_color = NamedTextColor.NAMES.valueOr(result.getString("electorColor"), NamedTextColor.WHITE);
                int idChoicePerk2 = result.getInt("idChoicePerk2");
                int idChoicePerk3 = result.getInt("idChoicePerk3");
                int vote = result.getInt("vote");

                MayorElector mayorElector = new MayorElector(city, elector_name, elector_uuid, elector_color, idChoicePerk2, idChoicePerk3, vote);

                cityElections.computeIfAbsent(city, k -> new ArrayList<>()).add(mayorElector);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveElectorMayors() {
        //String deleteSql = "DELETE FROM city_election";
        String sql = "INSERT INTO city_election (city_uuid, electorUUID, electorName, electorColor, idChoicePerk2, idChoicePerk3, vote) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "electorName = VALUES(electorName), electorColor = VALUES(electorColor), " +
                "idChoicePerk2 = VALUES(idChoicePerk2), idChoicePerk3 = VALUES(idChoicePerk3), vote = VALUES(vote)";

        try (Connection connection = DatabaseManager.getConnection();
             //PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);

             PreparedStatement statement = connection.prepareStatement(sql)) {
            //deleteStmt.executeUpdate();
            plugin.getLogger().info("Sauvegarde des données des joueurs qui se sont présentés...");

            for (Map.Entry<City, List<MayorElector>> entry : cityElections.entrySet()) {
                City city = entry.getKey();
                List<MayorElector> electors = entry.getValue();

                for (MayorElector elector : electors) {
                    statement.setString(1, city.getUUID());
                    statement.setString(2, elector.getElectorUUID().toString());
                    statement.setString(3, elector.getElectorName());
                    statement.setString(4, elector.getElectorColor().toString());
                    statement.setInt(5, elector.getIdChoicePerk2());
                    statement.setInt(6, elector.getIdChoicePerk3());
                    statement.setInt(7, elector.getVote());

                    statement.addBatch();
                }
            }

            statement.executeBatch();
            plugin.getLogger().info("Sauvegarde des données des joueurs qui se sont présentés réussie.");

        } catch (SQLException e) {
            plugin.getLogger().severe("Échec de la sauvegarde des données des joueurs qui se sont présentés.");
            e.printStackTrace();
        }
    }
    public void loadPlayersHasVoted() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_voted")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                String voter_uuid = result.getString("voterUUID");
                String elector_uuid = result.getString("electorUUID");

                City city = CityManager.getCity(city_uuid);
                if (city == null) {
                    continue;
                }

                List<MayorElector> electors = cityElections.get(city);
                if (electors == null) {
                    continue;
                }

                MayorElector electorFound = null;
                for (MayorElector elector : electors) {
                    if (elector.getElectorUUID().equals(elector_uuid)) {
                        electorFound = elector;
                        break;
                    }
                }

                if (electorFound != null) {
                    playerHasVoted.put(UUID.fromString(voter_uuid), electorFound);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePlayersHasVoted() {
        String sql = "INSERT INTO city_voted (city_uuid, voterUUID, electorUUID) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "voterUUID = VALUES(voterUUID), electorUUID = VALUES(electorUUID)";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql)) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui ont voté pour un maire...");

            playerHasVoted.forEach((voterUUID, mayorElector) -> {
                try {
                    statement.setString(1, mayorElector.getCity().getUUID());
                    statement.setString(2, voterUUID.toString());
                    statement.setString(3, mayorElector.getElectorUUID().toString());

                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            statement.executeBatch();
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui ont voté pour un maire réussie.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Échec de la sauvegarde des données des Joueurs qui ont voté pour un maire.");
            e.printStackTrace();
        }
    }

    // setup elections
    public void initPhase1() {
        phaseMayor = 1;
        //todo: ouverture des elections

        //todo: si nb player < 4 alors owner can 3 pick perk
        // sinon lancer election
    }

    public void initPhase2() {
        phaseMayor = 2;
        //si nb player < 4 alors activé les perk déjà mis dans mayorCity
        // donc faire systeme pour que les perks s'activent que quand phase =2

        //todo: changer de maire

        //todo: si aucune activité alors randomPick et owner maire
    }

    public void createElector(City city, MayorElector elector) {
        List<MayorElector> electors = cityElections.computeIfAbsent(city, key -> new ArrayList<>());

        electors.add(elector);
    }

    public MayorElector getElector(Player player) {
        UUID playerUUID = player.getUniqueId();

        for (List<MayorElector> electors : cityElections.values()) {
            for (MayorElector elector : electors) {
                if (elector.getElectorUUID().equals(playerUUID.toString())) {
                    return elector;
                }
            }
        }

        return null;
    }

    public boolean isPlayerElector(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (cityElections.get(playerCity) == null) return false;

        boolean playerAleardyElector = cityElections.get(playerCity)
                .stream()
                .anyMatch(elector -> elector.getElectorUUID().equals(player.getUniqueId().toString()));
        return playerAleardyElector;
    }

    public void removeVotePlayer(Player player) {
        playerHasVoted.remove(player.getUniqueId());
    }

    public void voteElector(Player player, MayorElector elector) {
        elector.setVote(elector.getVote() + 1);
        playerHasVoted.put(player.getUniqueId(), elector);
    }

    public boolean isPlayerVoted(Player player) {
        return playerHasVoted.keySet().contains(player.getUniqueId());
    }

    public MayorElector getPlayerVote(Player player) {
        return playerHasVoted.get(player.getUniqueId());
    }

    public String getElectorNameVotedBy(Player player) {
        MayorElector elector = playerHasVoted.get(player.getUniqueId());
        return (elector != null) ? elector.getElectorName() : "Aucun";
    }

    public NamedTextColor getElectorColorVotedBy(Player player) {
        MayorElector elector = playerHasVoted.get(player.getUniqueId());
        return (elector != null) ? elector.getElectorColor() : NamedTextColor.WHITE;
    }
}
