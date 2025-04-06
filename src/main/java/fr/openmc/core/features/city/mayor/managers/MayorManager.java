package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.*;
import fr.openmc.core.features.city.mayor.listeners.JoinListener;
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
import java.time.DayOfWeek;
import java.util.*;

public class MayorManager {
    @Getter
    static MayorManager instance;

    private final OMCPlugin plugin;

    public int MEMBER_REQ_ELECTION = 1;

    public static String TABLE_MAYOR = "city_mayor";
    public static String TABLE_ELECTION = "city_election";
    public static String TABLE_VOTE = "city_vote";
    public static String TABLE_CONSTANTS = "mayor_constants";

    private final List<NamedTextColor> LIST_MAYOR_COLOR = List.of(
            NamedTextColor.RED,
            NamedTextColor.GOLD,
            NamedTextColor.YELLOW,
            NamedTextColor.GREEN,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.BLUE,
            NamedTextColor.AQUA,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.WHITE,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY
    );

    public static DayOfWeek PHASE_1_DAY = DayOfWeek.TUESDAY;
    public static DayOfWeek PHASE_2_DAY = DayOfWeek.THURSDAY;

    public int phaseMayor;
    public HashMap<City, Mayor> cityMayor = new HashMap<>();
    public Map<City, List<MayorCandidate>> cityElections = new HashMap<>(){};
    public Map<City, List<MayorVote>> playerVote = new HashMap<>();


    private static final Random RANDOM = new Random();


    public MayorManager(OMCPlugin plugin) {
        instance = this;

        this.plugin = plugin;

        // LISTENERS
        new PhaseListener(plugin);
        OMCPlugin.registerEvents(
                new JoinListener()
        );
        if (CustomItemRegistry.hasItemsAdder()) {
            OMCPlugin.registerEvents(
                    //make listener for urne
            );
        }

        loadMayorConstant();
        loadCityMayors();
        loadMayorCandidates();
        loadPlayersVote();

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("===== MayorManager Debug =====");

                Bukkit.getLogger().info("City Mayors:");
                System.out.println(cityMayor);
                for (Map.Entry<City, Mayor> entry : cityMayor.entrySet()) {
                    Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue().getName() + " " + entry.getValue().getUUID());
                }

                Bukkit.getLogger().info("City Elections:");
                for (Map.Entry<City, List<MayorCandidate>> entry : cityElections.entrySet()) {
                    Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue());
                }

                Bukkit.getLogger().info("Player Votes:");
                for (Map.Entry<City, List<MayorVote>> entry : playerVote.entrySet()) {
                    Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue());
                }

                Bukkit.getLogger().info("================================");
            }
        }.runTaskTimer(plugin, 0, 600L); // 600 ticks = 30 secondes
    }

    public static void init_db(Connection conn) throws SQLException {
        // create city_mayor : contient l'actuel maire et les réformes actuelles
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_MAYOR + " (city_uuid VARCHAR(8) UNIQUE, mayorUUID VARCHAR(36), mayorName VARCHAR(36), mayorColor VARCHAR(36), idPerk1 int(2), idPerk2 int(2), idPerk3 int(2), electionType VARCHAR(36))").executeUpdate();
        // create city_election : contient les membres d'une ville ayant participé pour etre maire
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_ELECTION + " (city_uuid VARCHAR(8) NOT NULL, candidateUUID VARCHAR(36) UNIQUE NOT NULL, candidateName VARCHAR(36) NOT NULL, candidateColor VARCHAR(36) NOT NULL, idChoicePerk2 int(2), idChoicePerk3 int(2), vote int(5))").executeUpdate();
        // create city_voted : contient les membres d'une ville ayant deja voté
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_VOTE + " (city_uuid VARCHAR(8) NOT NULL, voterUUID VARCHAR(36) UNIQUE NOT NULL, candidateUUID VARCHAR(36) NOT NULL)").executeUpdate();
        // create constants : contient une information universelle pour tout le monde
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_CONSTANTS + " (mayorPhase int(1))").executeUpdate();
        PreparedStatement state = conn.prepareStatement("SELECT COUNT(*) FROM " + TABLE_CONSTANTS);
        ResultSet rs = state.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            PreparedStatement states = conn.prepareStatement("INSERT INTO " + TABLE_CONSTANTS + " (mayorPhase) VALUES (1)");
            states.executeUpdate();
        }
    }

    // Load and Save Data Methods
    public void loadMayorConstant() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM " + TABLE_CONSTANTS + " WHERE 1")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                phaseMayor = result.getInt("mayorPhase");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMayorConstant() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("UPDATE " + TABLE_CONSTANTS + " SET mayorPhase = ?")) {
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
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM " + TABLE_MAYOR + " WHERE 1")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                City city = CityManager.getCity(city_uuid);
                String mayorUUIDString = result.getString("mayorUUID");
                UUID mayor_uuid = (mayorUUIDString != null && !mayorUUIDString.isEmpty()) ? UUID.fromString(mayorUUIDString) : null;
                String mayor_name = result.getString("mayorName");
                mayor_name = (mayor_name != null) ? mayor_name : "Inconnu";
                NamedTextColor mayor_color = NamedTextColor.NAMES.valueOr(result.getString("mayorColor"), NamedTextColor.WHITE);
                int idPerk1 = result.getInt("idPerk1");
                int idPerk2 = result.getInt("idPerk2");
                int idPerk3 = result.getInt("idPerk3");
                String electionTypeStr = result.getString("electionType");
                ElectionType electionType = ElectionType.valueOf(electionTypeStr);

                cityMayor.put(city, new Mayor(city, mayor_name, mayor_uuid, mayor_color, idPerk1, idPerk2, idPerk3, electionType));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCityMayors() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO " + TABLE_MAYOR + " (city_uuid, mayorUUID, mayorName, mayorColor, idPerk1, idPerk2, idPerk3, electionType) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "city_uuid = VALUES(city_uuid), mayorUUID = VALUES(mayorUUID), mayorName = VALUES(mayorName), mayorColor = VALUES(mayorColor), idPerk1 = VALUES(idPerk1), idPerk2 = VALUES(idPerk2), idPerk3 = VALUES(idPerk3), electionType = VALUES(electionType)"
        )) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui sont maire...");
            cityMayor.forEach((city, mayor) -> {
                try {
                    statement.setString(1, city.getUUID());
                    statement.setString(2, mayor.getUUID() != null ? mayor.getUUID().toString() : null);
                    statement.setString(3, mayor.getName() != null ? mayor.getName() : null);
                    statement.setString(4, mayor.getMayorColor() != null ? mayor.getMayorColor().toString() : null);
                    statement.setInt(5, mayor.getIdPerk1());
                    statement.setInt(6, mayor.getIdPerk2());
                    statement.setInt(7, mayor.getIdPerk3());
                    statement.setString(8, mayor.getElectionType().toString());

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

    public void loadMayorCandidates() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM " + TABLE_ELECTION)) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                City city = CityManager.getCity(city_uuid);
                UUID candidate_uuid = UUID.fromString(result.getString("candidateUUID"));
                String candidate_name = result.getString("candidateName");
                NamedTextColor candidate_color = NamedTextColor.NAMES.valueOr(result.getString("candidateColor"), NamedTextColor.WHITE);
                int idChoicePerk2 = result.getInt("idChoicePerk2");
                int idChoicePerk3 = result.getInt("idChoicePerk3");
                int vote = result.getInt("vote");

                MayorCandidate mayorCandidate = new MayorCandidate(city, candidate_name, candidate_uuid, candidate_color, idChoicePerk2, idChoicePerk3, vote);

                cityElections.computeIfAbsent(city, k -> new ArrayList<>()).add(mayorCandidate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMayorCandidates() {
        String sql = "INSERT INTO " + TABLE_ELECTION + " (city_uuid, candidateUUID, candidateName, candidateColor, idChoicePerk2, idChoicePerk3, vote) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "candidateName = VALUES(candidateName), candidateColor = VALUES(candidateColor), " +
                "idChoicePerk2 = VALUES(idChoicePerk2), idChoicePerk3 = VALUES(idChoicePerk3), vote = VALUES(vote)";

        try (Connection connection = DatabaseManager.getConnection();

             PreparedStatement statement = connection.prepareStatement(sql)) {
            plugin.getLogger().info("Sauvegarde des données des joueurs qui se sont présentés...");

            for (Map.Entry<City, List<MayorCandidate>> entry : cityElections.entrySet()) {
                City city = entry.getKey();
                List<MayorCandidate> candidates = entry.getValue();

                for (MayorCandidate candidate : candidates) {
                    statement.setString(1, city.getUUID());
                    statement.setString(2, candidate.getUUID().toString());
                    statement.setString(3, candidate.getName());
                    statement.setString(4, candidate.getCandidateColor().toString());
                    statement.setInt(5, candidate.getIdChoicePerk2());
                    statement.setInt(6, candidate.getIdChoicePerk3());
                    statement.setInt(7, candidate.getVote());

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
    public void loadPlayersVote() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM " + TABLE_VOTE)) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String city_uuid = result.getString("city_uuid");
                UUID voter_uuid = UUID.fromString(result.getString("voterUUID"));
                UUID candidate_uuid = UUID.fromString(result.getString("candidateUUID"));

                City city = CityManager.getCity(city_uuid);
                if (city == null) {
                    continue;
                }

                List<MayorCandidate> candidates = cityElections.get(city);
                if (candidates == null) {
                    continue;
                }

                MayorCandidate candidateFound = null;
                for (MayorCandidate candidate : candidates) {
                    if (candidate.getUUID().equals(candidate_uuid)) {
                        candidateFound = candidate;
                        break;
                    }
                }

                if (candidateFound != null) {
                    MayorVote vote = new MayorVote(voter_uuid, candidateFound);
                    playerVote.computeIfAbsent(city, k -> new ArrayList<>()).add(vote);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePlayersVote() {
        String sql = "INSERT INTO " + TABLE_VOTE + " (city_uuid, voterUUID, candidateUUID) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "city_uuid = VALUES(city_uuid), voterUUID = VALUES(voterUUID), candidateUUID = VALUES(candidateUUID)";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql)) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs qui ont voté pour un maire...");

            playerVote.forEach((city, mayorVotes) -> {
                for (MayorVote vote : mayorVotes) {
                    try {
                        statement.setString(1, city.getUUID());
                        statement.setString(2, vote.getVoterUUID().toString());
                        statement.setString(3, vote.getCandidate().getUUID().toString());

                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
    public void initPhase1() throws SQLException {
        // ---OUVERTURE DES ELECTIONS---
        phaseMayor = 1;

        // On vide toutes les tables
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            String deleteSql1 = "DELETE FROM " + TABLE_MAYOR;
            String deleteSql2 = "DELETE FROM " + TABLE_VOTE;
            String deleteSql3 = "DELETE FROM " + TABLE_ELECTION;
            try (Connection connection = DatabaseManager.getConnection()) {
                PreparedStatement deleteStmt1 = connection.prepareStatement(deleteSql1);
                PreparedStatement deleteStmt2 = connection.prepareStatement(deleteSql2);
                PreparedStatement deleteStmt3 = connection.prepareStatement(deleteSql3);

                deleteStmt1.executeUpdate();
                deleteStmt2.executeUpdate();
                deleteStmt3.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Échec du vidage des tables pour les Maires");
                e.printStackTrace();
            }
        });
        cityMayor = new HashMap<>();
        cityElections = new HashMap<>(){};
        playerVote = new HashMap<>();

        for (City city : CityManager.getCities()) {
            if (city.getMembers().size()>=MEMBER_REQ_ELECTION) {
                createMayor(null,null, city, null, null, null, null, ElectionType.ELECTION);
            }
            createMayor(null, null, city, null, null, null, null, ElectionType.OWNER_CHOOSE);
        }
    }

    public void initPhase2() {
        phaseMayor = 2;

        // TRAITEMENT DE CHAQUE VILLE - Complexité de O(n log(n))
        for (City city : CityManager.getCities()) {
            runSetupMayor(city);
        }
        
        //tester si phase 2 marche
    }

    public void runSetupMayor(City city) {
        UUID ownerUUID = city.getPlayerWith(CPermission.OWNER);
        String ownerName = Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();
        //todo: Bukkit.getOfflinePlayer consomme beaucoup, envisager de faire une liste commune pour tout le monde
        // (mise en cache) afin de collecter le name sans redemander la methode
        Mayor mayor = city.getMayor();

        if (getElectionType(city) == ElectionType.OWNER_CHOOSE) {
            // si maire a pas choisis les perks
            if ((mayor.getIdPerk1() != 0) && (mayor.getIdPerk2() != 0) && (mayor.getIdPerk3() != 0)) {
                NamedTextColor color = getRandomMayorColor();
                List<Perks> perks = PerkManager.getRandomPerksAll();
                createMayor(ownerName, ownerUUID, city, perks.getFirst(), perks.get(1), perks.get(2), color, ElectionType.OWNER_CHOOSE);
            }
        } else {
            // si owner a pas choisi perk event
            if (mayor.getIdPerk1() == 0) {
                mayor.setIdPerk1(PerkManager.getRandomPerkEvent().getId());
            }

            if (cityElections.containsKey(city)) { // si y'a des maires qui se sont présenter
                List<MayorCandidate> candidates = cityElections.get(city);

                // Code fait avec ChatGPT pour avoir une complexité de O(n log(n)) au lieu de 0(n²)
                PriorityQueue<MayorCandidate> candidateQueue = new PriorityQueue<>(
                        Comparator.comparingInt(MayorCandidate::getVote).reversed()
                );
                candidateQueue.addAll(candidates);

                MayorCandidate mayorWinner = candidateQueue.peek();
                Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                Perks perk2 = PerkManager.getPerkById(mayorWinner.getIdChoicePerk2());
                Perks perk3 = PerkManager.getPerkById(mayorWinner.getIdChoicePerk3());

                createMayor(mayorWinner.getName(), mayorWinner.getUUID(), city, perk1, perk2, perk3, mayorWinner.getCandidateColor(), ElectionType.ELECTION);

            } else {
                // personne s'est présenté, owner = maire
                NamedTextColor color = getRandomMayorColor();
                List<Perks> perks = PerkManager.getRandomPerksBasic();
                createMayor(ownerName, ownerUUID, city, PerkManager.getPerkById(mayor.getIdPerk1()), perks.getFirst(), perks.get(1), color, ElectionType.ELECTION);

            }
        }


        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                String[] queries = {
                        "DELETE FROM " + TABLE_ELECTION + " WHERE city_uuid = ?",
                        "DELETE FROM " + TABLE_VOTE + " WHERE city_uuid = ?"
                };

                for (String sql : queries) {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql);
                    statement.setString(1, city.getUUID());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // on supprime donc les elections de la ville ou le maire a été élu
        cityElections.remove(city);
        // on supprime donc les votes de la ville ou le maire a été élu
        playerVote.remove(city);
    }

    public void createCandidate(City city, MayorCandidate candidate) {
        List<MayorCandidate> candidates = cityElections.computeIfAbsent(city, key -> new ArrayList<>());

        candidates.add(candidate);
    }

    public MayorCandidate getCandidate(Player player) {
        UUID playerUUID = player.getUniqueId();

        for (List<MayorCandidate> candidates : cityElections.values()) {
            for (MayorCandidate candidate : candidates) {
                if (candidate.getUUID().equals(playerUUID)) {
                    return candidate;
                }
            }
        }

        return null;
    }

    public boolean hasCandidated(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (cityElections.get(playerCity) == null) return false;

        return cityElections.get(playerCity)
                .stream()
                .anyMatch(candidate -> candidate.getUUID().equals(player.getUniqueId()));
    }

    public void removeVotePlayer(Player player) {
        playerVote.forEach((city, votes) ->
                votes.removeIf(vote -> vote.getVoterUUID().equals(player.getUniqueId()))
        );
    }

    public void voteCandidate(City playerCity, Player player, MayorCandidate candidate) {
        candidate.setVote(candidate.getVote() + 1);
        List<MayorVote> votes = playerVote.computeIfAbsent(playerCity, key -> new ArrayList<>());

        votes.add(new MayorVote(player.getUniqueId(), candidate));
    }

    public boolean hasVoted(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerVote.get(playerCity) == null) return false;

        return playerVote.get(playerCity)
                .stream()
                .anyMatch(mayorVote -> mayorVote.getVoterUUID().equals(player.getUniqueId()));
    }

    public MayorCandidate getPlayerVote(Player player) {
        for (List<MayorVote> votes : playerVote.values()) {
            for (MayorVote vote : votes) {
                if (vote.getVoterUUID().equals(player.getUniqueId())) {
                    return vote.getCandidate();
                }
            }
        }

        return null;
    }

    public boolean hasChoicePerkOwner(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        Mayor mayor = cityMayor.get(playerCity);
        if (mayor == null) return false;

        return mayor.getIdPerk1() != 0;
    }

    public boolean hasMayor(City city) {
        Mayor mayor = cityMayor.get(city);
        if (mayor == null) return false;

        return mayor.getUUID() != null;
    }

    public void put1Perk(City city, Perks perk1) {
        Mayor mayor = cityMayor.get(city);
        if (mayor != null) {
            mayor.setIdPerk1(perk1.getId());
        } else { //au cas ou meme si théoriquement c impossible
            cityMayor.put(city, new Mayor(city, null, null, null, perk1.getId(), 0, 0, getElectionType(city)));
        }
    }

    public void createMayor(String playerName, UUID playerUUID, City city, Perks perk1, Perks perk2, Perks perk3, NamedTextColor color, ElectionType type) {
        Mayor mayor = cityMayor.get(city);
        int idPerk1 = perk1 != null ? perk1.getId() : 0;
        int idPerk2 = perk2 != null ? perk2.getId() : 0;
        int idPerk3 = perk3 != null ? perk3.getId() : 0;
        if (mayor != null) {
            mayor.setName(playerName);
            mayor.setUUID(playerUUID);
            mayor.setMayorColor(color);
            mayor.setIdPerk1(idPerk1);
            mayor.setIdPerk2(idPerk2);
            mayor.setIdPerk3(idPerk3);
            mayor.setElectionType(getElectionType(city));
        } else { // au cas ou meme si c théoriquement impossible (on défini tous les maires a la phase 1 et on le crée quand on crée la ville)
            cityMayor.put(city, new Mayor(city, playerName, playerUUID, color, idPerk1, idPerk2, idPerk3, type));
        }
    }

    public ElectionType getElectionType(City city) {
        Mayor mayor = cityMayor.get(city);
        if (mayor == null) return null;

        return mayor.getElectionType();
    }

    public NamedTextColor getRandomMayorColor() {
        return LIST_MAYOR_COLOR.get(RANDOM.nextInt(LIST_MAYOR_COLOR.size()));
    }
}
