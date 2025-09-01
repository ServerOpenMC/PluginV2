package fr.openmc.core.features.contest.managers;

import fr.openmc.core.features.contest.models.ContestPlayer;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter
public class ContestPlayerManager  {

    /**
     * Map reliant un nombre de points à un titre correspondant.
     * Par exemple, 10000 points correspond à "Dictateur en ".
     */
    private static final Map<Integer, String> RANKS = Map.of(
            10000, "Dictateur en ",
            2500, "Colonel en ",
            2000, "Addict en ",
            1500, "Dieu en ",
            1000, "Légende en ",
            750, "Sénior en ",
            500, "Pro en ",
            250, "Semi-Pro en ",
            100, "Amateur en ",
            0, "Noob en "
    );

    /**
     * Map reliant le nombre de points minimum à atteindre pour obtenir le rang suivant
     * au nombre de points à partir desquels ce rang est débloqué.
     * Par exemple, pour 2500 points, le rang suivant commence à 10000 points.
     */
    private static final Map<Integer, Integer> GOAL_POINTS = Map.of(
            10000, 0,
            2500, 10000,
            2000, 2500,
            1500, 2000,
            1000, 1500,
            750, 1000,
            500, 750,
            250, 500,
            100, 250,
            0, 100
    );

    /**
     * Map convertissant le nombre de points en un rang numérique compris entre 1 et 10.
     * Par exemple, 10000 points correspond au rang 10.
     */
    private static final Map<Integer, Integer> POINTS_TO_INT_RANK = Map.of(
            10000, 10,
            2500, 9,
            2000, 8,
            1500, 7,
            1000, 6,
            750, 5,
            500, 4,
            250, 3,
            100, 2,
            0, 1
    );

    /**
     * Map des multiplicateurs d'argent pour la récompense en fonction du rang.
     * Chaque clé est le rang numérique et chaque valeur le multiplicateur correspondant.
     */
    private static final HashMap<Integer, Double> MULTIPLICATOR_MONEY = new HashMap<>();

    static {
        MULTIPLICATOR_MONEY.put(1, 1.0);
        MULTIPLICATOR_MONEY.put(2, 1.1);
        MULTIPLICATOR_MONEY.put(3, 1.3);
        MULTIPLICATOR_MONEY.put(4, 1.4);
        MULTIPLICATOR_MONEY.put(5, 1.5);
        MULTIPLICATOR_MONEY.put(6, 1.6);
        MULTIPLICATOR_MONEY.put(7, 1.7);
        MULTIPLICATOR_MONEY.put(8, 1.8);
        MULTIPLICATOR_MONEY.put(9, 2.0);
        MULTIPLICATOR_MONEY.put(10, 2.4);
    }

    /**
     * Retourne le nom du camp auquel appartient le joueur.
     *
     * @param player Le joueur dont on veut connaître le camp.
     * @return Le nom du camp (camp1 ou camp2).
     */
    public static String getPlayerCampName(Player player) {
        int campInteger = ContestManager.dataPlayer.get(player.getUniqueId()).getCamp();
        return ContestManager.data.get("camp" + campInteger);
    }

    /**
     * Met à jour le nombre de points d’un joueur.
     * Cette opération écrase les points précédemment enregistrés.
     *
     * @param player L’UUID du joueur à mettre à jour.
     * @param points Le nouveau nombre de points du joueur.
     */
    public static void setPointsPlayer(UUID player, int points) {
        ContestPlayer data = ContestManager.dataPlayer.get(player);
        if (data != null) {
            data.setPoints(points);
        }
    }

    /**
     * Retourne le titre associé à un nombre de points donné.
     *
     * @param points Le nombre de points d’un joueur.
     * @return Le titre correspondant aux points.
     */
    public static String getTitleWithPoints(int points) {
        for (Map.Entry<Integer, String> entry : RANKS.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }
        return "";
    }

    /**
     * Retourne le titre du contest d’un joueur en fonction de ses points actuels.
     *
     * @param player Le joueur dont on veut obtenir le titre.
     * @return Le titre correspondant au joueur.
     */
    public static String getTitleContest(Player player) {
        int points = ContestManager.dataPlayer.get(player.getUniqueId()).getPoints();

        return getTitleWithPoints(points);
    }

    /**
     * Retourne le nombre de points nécessaires pour atteindre le rang suivant.
     *
     * @param player Le joueur dont on veut calculer le prochain palier.
     * @return Le nombre de points requis pour monter de rang.
     *         Retourne -1 si aucun palier trouvé.
     */
    public static int getGoalPointsToRankUp(Player player) {
        int points = ContestManager.dataPlayer.get(player.getUniqueId()).getPoints();

        for (Map.Entry<Integer, Integer> entry : GOAL_POINTS.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }

        return -1;
    }

    /**
     * Retourne le rang numérique d’un joueur hors ligne en fonction de ses points.
     *
     * @param player Le joueur hors ligne.
     * @return Le rang sous forme d’un entier (1 à 10).
     */
    public static int getRankContestFromOfflineInt(OfflinePlayer player) {
        int points = ContestManager.dataPlayer.get(player.getUniqueId()).getPoints();

        for (Map.Entry<Integer, Integer> entry : POINTS_TO_INT_RANK.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }

        return 0;
    }

    /**
     * Vérifie si le joueur fait partie de l’équipe gagnante du contest.
     *
     * @param player Le joueur hors ligne à vérifier.
     * @return true si le joueur est dans le camp gagnant, false sinon.
     */
    public static boolean hasWinInCampFromOfflinePlayer(OfflinePlayer player) {
        int playerCamp = ContestManager.dataPlayer.get(player.getUniqueId()).getCamp();

        int points1 = ContestManager.data.getPoints1();
        int points2 = ContestManager.data.getPoints2();


        int vote1 = ContestManager.getVoteTaux(1);
        int vote2 = ContestManager.getVoteTaux(2);
        int totalvote = vote1 + vote2;
        int vote1Taux = (int) (((double) vote1 / totalvote) * 100);
        int vote2Taux = (int) (((double) vote2 / totalvote) * 100);
        int multiplicateurPoint = Math.abs(vote1Taux - vote2Taux)/16;

        if (vote1Taux > vote2Taux) {
            points2*=multiplicateurPoint;
        } else if (vote1Taux < vote2Taux) {
            points1*=multiplicateurPoint;
        }

        if (points1 > points2 && playerCamp == 1) {
            return true;
        }

        return points2 > points1 && playerCamp == 2;
    }

    /**
     * Retourne le multiplicateur de récompense en fonction du rang du joueur.
     *
     * @param rang Le rang numérique du joueur (1 à 10).
     * @return Le multiplicateur correspondant pour le calcul des récompenses.
     */
    public static double getMultiplicatorFromRank(int rang) {
        return MULTIPLICATOR_MONEY.getOrDefault(rang, 1.0);
    }
}
