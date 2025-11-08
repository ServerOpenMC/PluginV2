package fr.openmc.core.features.city.sub.notation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.notation.commands.AdminNotationCommands;
import fr.openmc.core.features.city.sub.notation.commands.NotationCommands;
import fr.openmc.core.features.city.sub.notation.listeners.PlayerJoinListener;
import fr.openmc.core.features.city.sub.notation.models.ActivityTimePlayed;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static fr.openmc.core.features.city.sub.notation.NotationNote.getMaxTotalNote;

/**
 * Gestionnaire des notations des villes.
 *
 * <p>Cette classe s'occupe de la gestion des notes, des récompenses et du calcul des scores
 * de toutes les villes. Elle interagit avec la base de données via ORMLite et planifie des
 * exécutions périodiques pour recalculer les scores et attribuer les récompenses.</p>
 */
public class NotationManager {

    /**
     * Jour d'application de la notation.
     */
    private static final DayOfWeek APPLY_NOTATION_DAY = DayOfWeek.SUNDAY;

    /**
     * Map des notations par semaine (clé : chaine de la semaine, valeur : liste de CityNotation).
     */
    public static final Map<String, List<CityNotation>> notationPerWeek = new HashMap<>();

    /**
     * Map des notations par ville (clé : UUID de la ville, valeur : liste de CityNotation).
     */
    public static final Map<UUID, List<CityNotation>> cityNotations = new HashMap<>();

    /**
     * Map des temps d'activité des joueurs (clé : UUID du joueur, valeur : temps).
     */
    public static final Map<UUID, Long> activityNotation = new HashMap<>();

    /**
     * Ensemble des 10 meilleures villes.
     */
    public static final Set<UUID> top10Cities = new HashSet<>();

    /**
     * DAO pour la table des temps d'activité.
     */
    private static Dao<ActivityTimePlayed, String> activityTimePlayedDao;

    /**
     * DAO pour la table des notations de ville.
     */
    private static Dao<CityNotation, String> notationDao;

    /**
     * Constructeur de NotationManager.
     *
     * <p>Charge les notations, enregistre les commandes et les listeners, et planifie la tâche nocturne.</p>
     */
    public static void init() {
        loadNotations();
        CommandsManager.getHandler().register(
                new NotationCommands(),
                new AdminNotationCommands()
        );
        OMCPlugin.registerEvents(
                new PlayerJoinListener()
        );
        scheduleMidnightTask();
        loadTop10Cities();
    }

    /**
     * Initialise la base de données pour les notations.
     *
     * @param connectionSource la source de connexion de la base de données
     * @throws SQLException en cas d'erreur SQL
     */
    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityNotation.class);
        notationDao = DaoManager.createDao(connectionSource, CityNotation.class);

        TableUtils.createTableIfNotExists(connectionSource, ActivityTimePlayed.class);
        activityTimePlayedDao = DaoManager.createDao(connectionSource, ActivityTimePlayed.class);
        activityTimePlayedDao.queryForAll()
                .forEach(activityTimePlayed -> activityNotation.put(
                        UUID.fromString(activityTimePlayed.getPlayerUUID()),
                        activityTimePlayed.getTimeOnWeekStart()
                ));
    }

    /**
     * Charge les notations depuis la base de données et les répartit dans les maps.
     */
    public static void loadNotations() {
        try {
            List<CityNotation> notations = notationDao.queryForAll();
            for (CityNotation notation : notations) {
                UUID cityUUID = notation.getCityUUID();
                City city = CityManager.getCity(cityUUID);
                if (city == null) continue;

                String weekStr = notation.getWeekStr();
                cityNotations.computeIfAbsent(cityUUID, k -> new ArrayList<>()).add(notation);
                notationPerWeek.computeIfAbsent(weekStr, k -> new ArrayList<>()).add(notation);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sauvegarde toutes les notations dans la base de données.
     */
    public static void saveNotations() {
        try {
            notationDao.delete(notationDao.queryForAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        notationPerWeek.forEach((weekStr, notations) ->
                notations.forEach(notation -> {
                    try {
                        notationDao.createOrUpdate(notation);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    /**
     * Crée ou met à jour une notation pour une ville.
     *
     * @param notation la notation à créer ou mettre à jour
     */
    public static void createOrUpdateNotation(CityNotation notation) {
        try {
            notationDao.createOrUpdate(notation);

            String weekStr = notation.getWeekStr();
            notationPerWeek.compute(weekStr, (k, list) -> {
                if (list == null) list = new ArrayList<>();
                list.removeIf(n -> Objects.equals(n.getCityUUID(), notation.getCityUUID()));
                list.add(notation);
                return list;
            });

            cityNotations.compute(notation.getCityUUID(), (k, list) -> {
                if (list == null) list = new ArrayList<>();
                list.removeIf(n -> Objects.equals(n.getCityUUID(), notation.getCityUUID()));
                list.removeIf(n -> Objects.equals(n.getWeekStr(), notation.getWeekStr()));
                list.add(notation);
                return list;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Charge les 10 meilleures villes en fonction des notations.
     */
    public static void loadTop10Cities() {
        top10Cities.clear();
        for (String weekStr : notationPerWeek.keySet()) {
            getSortedNotationForWeek(weekStr).stream()
                    .limit(10)
                    .map(CityNotation::getCityUUID)
                    .forEach(top10Cities::add);
        }
    }

    /**
     * Retourne la liste triée des notations pour une semaine donnée.
     *
     * @param weekStr la chaîne représentant la semaine
     * @return liste de notations triées par note architecturale et cohérence, ordre décroissant
     */
    public static List<CityNotation> getSortedNotationForWeek(String weekStr) {
        List<CityNotation> notations = notationPerWeek.getOrDefault(weekStr, Collections.emptyList());
        return notations.stream()
                .sorted(Comparator.comparingDouble(
                        CityNotation::getTotalNote
                ).reversed())
                .toList();
    }

    /**
     * Calcule le score d'activité d'une ville.
     *
     * @param city la ville concernée
     * @return le score moyen d'activité des membres de la ville
     * @throws SQLException en cas d'erreur SQL
     */
    public static double getActivityScore(City city) throws SQLException {
        double totalScore = 0;
        int playerCount = 0;
        for (UUID playerUUID : city.getMembers()) {
            OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(playerUUID);
            long currentPlaytime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
            long savedPlaytime = activityNotation.containsKey(playerUUID) ? activityNotation.get(playerUUID) : 0;
            long weeklyPlaytimeTicks = currentPlaytime - savedPlaytime;
            if (weeklyPlaytimeTicks > 0) {
                double weeklyPlaytimeHours = weeklyPlaytimeTicks / 1200.0 / 60.0;
                double playerScore = Math.min(weeklyPlaytimeHours / NotationNote.NOTE_ACTIVITY.getMaxNote(), 1.0) * NotationNote.NOTE_ACTIVITY.getMaxNote();
                totalScore += playerScore;
                playerCount++;
            }
            activityTimePlayedDao.createOrUpdate(new ActivityTimePlayed(playerUUID, currentPlaytime));
        }
        return playerCount == 0 ? 0 : totalScore / playerCount;
    }

    /**
     * Calcule le score militaire d'une ville.
     *
     * @param city la ville concernée
     * @return le score militaire calculé
     */
    public static double getMilitaryScore(City city) {
        int powerPoints = city.getPowerPoints();
        int maxNote = NotationNote.NOTE_MILITARY.getMaxNote();
        int pointsToGetMaxNote = 30;
        double score = ((double) powerPoints / pointsToGetMaxNote) * maxNote;
        if (score > maxNote) {
            score = maxNote;
        }
        if (score < 0) {
            score = 0;
        }
        return score;
    }

    /**
     * Calcule le score économique d'une ville.
     *
     * @param city   la ville concernée
     * @param pibMax le PIB maximal de comparaison
     * @return le score économique calculé
     */
    public static double getEconomyScore(City city, double pibMax) {
        double totalMoney = 0;
        int memberCount = 0;
        for (UUID playerUUID : city.getMembers()) {
            double balance = EconomyManager.getBalance(playerUUID);
            totalMoney += balance;
            memberCount++;
        }
        if (memberCount == 0 || pibMax == 0) return 0;
        double pib = totalMoney / memberCount;
        double score = (Math.log10(pib + 1) / Math.log10(pibMax + 1)) * NotationNote.NOTE_PIB.getMaxNote();
        return Math.min(score, NotationNote.NOTE_PIB.getMaxNote());
    }

    /**
     * Retourne le PIB maximal parmi une liste de villes.
     *
     * @param cities la liste des villes
     * @return le PIB maximal trouvé
     */
    public static double getMaxPib(List<City> cities) {
        double maxPib = 0;
        for (City city : cities) {
            double totalMoney = 0;
            int memberCount = city.getMembers().size();
            for (UUID playerUUID : city.getMembers()) {
                totalMoney += EconomyManager.getBalance(playerUUID);
            }
            if (memberCount > 0) {
                double pib = totalMoney / memberCount;
                maxPib = Math.max(maxPib, pib);
            }
        }
        return maxPib;
    }

    /**
     * Calcule le score complet de chaque ville pour une semaine donnée.
     *
     * @param weekStr la chaîne représentant la semaine
     * @throws SQLException en cas d'erreur SQL
     */
    public static void calculateAllCityScore(String weekStr) throws SQLException {
        List<CityNotation> notationsCopy = new ArrayList<>(
                notationPerWeek.getOrDefault(weekStr, Collections.emptyList())
        );

        for (CityNotation notation : notationsCopy) {
            City city = CityManager.getCity(notation.getCityUUID());
            notation.setNoteActivity(getActivityScore(city));
            notation.setNoteMilitary(getMilitaryScore(city));
            double economyScore = getEconomyScore(
                    city,
                    getMaxPib(cityNotations.get(city.getUniqueId()).stream()
                            .map(CityNotation::getCityUUID)
                            .map(CityManager::getCity)
                            .collect(Collectors.toList()))
            );
            notation.setNoteEconomy(Math.floor(economyScore));
            createOrUpdateNotation(notation);
        }
    }

    /**
     * Calcule la récompense à attribuer à une ville en fonction de sa notation totale.
     *
     * @param notation la notation de la ville
     * @return le montant de la récompense
     */
    public static double calculateReward(CityNotation notation) {
        double points = notation.getTotalNote();
        double money = points * (45000.0 / getMaxTotalNote());

        BigDecimal rounded = BigDecimal.valueOf(money).setScale(2, RoundingMode.HALF_UP);

        notation.setMoney(rounded.doubleValue());
        return rounded.doubleValue();
    }

    /**
     * Attribue les récompenses aux villes pour une semaine donnée.
     *
     * @param weekStr la chaîne représentant la semaine
     */
    public static void giveReward(String weekStr) {
        List<CityNotation> notations = notationPerWeek.getOrDefault(weekStr, Collections.emptyList());

        for (CityNotation notation : notations) {
            City city = CityManager.getCity(notation.getCityUUID());
            if (city != null) {
                city.setBalance(city.getBalance() + calculateReward(notation));
            }
        }
    }

    /**
     * Planifie l'exécution de la tâche de minuit qui calcule les scores et attribue les récompenses.
     */
    private static void scheduleMidnightTask() {
        long delayInTicks = DateUtils.getSecondsUntilDayOfWeekMidnight(APPLY_NOTATION_DAY) * 20;
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            String weekStr = DateUtils.getWeekFormat();
            try {
                calculateAllCityScore(weekStr);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            giveReward(weekStr);
            scheduleMidnightTask();
        }, delayInTicks);
    }
}
