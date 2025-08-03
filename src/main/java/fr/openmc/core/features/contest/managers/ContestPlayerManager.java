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
     * Retourne le camp du joueur (soit camp1 ou camp2)
     */
    public static String getPlayerCampName(Player player) {
        int campInteger = ContestManager.dataPlayer.get(player.getUniqueId()).getCamp();
        return ContestManager.data.get("camp" + campInteger);
    }

    /**
     * Met a jour le nombre de points du joueur, cela écrase les points précédents
     */
    public static void setPointsPlayer(UUID player, int points) {
        ContestPlayer data = ContestManager.dataPlayer.get(player);
        if (data != null) {
            data.setPoints(points);
        }
    }

    /**
     * Retourne le Titre en fonction du nombre de points
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
     * Retourne le Titre d'une personne
     */
    public static String getTitleContest(Player player) {
        int points = ContestManager.dataPlayer.get(player.getUniqueId()).getPoints();

        return getTitleWithPoints(points);
    }

    /**
     * Retourne les prochains points pour arriver au prochain rang
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
     * Retourne le Rang d'un joueur hors ligne
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
     * Retourne si le joueur est dans l'equipe gagnante
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
     * Retourne le multiplicateur en fonction de son rang
     */
    public static double getMultiplicatorFromRank(int rang) {
        HashMap<Integer, Double> rankToMultiplicatorMoney = new HashMap<>();
        rankToMultiplicatorMoney.put(1, 1.0);
        rankToMultiplicatorMoney.put(2, 1.1);
        rankToMultiplicatorMoney.put(3, 1.3);
        rankToMultiplicatorMoney.put(4, 1.4);
        rankToMultiplicatorMoney.put(5, 1.5);
        rankToMultiplicatorMoney.put(6, 1.6);
        rankToMultiplicatorMoney.put(7, 1.7);
        rankToMultiplicatorMoney.put(8, 1.8);
        rankToMultiplicatorMoney.put(9, 2.0);
        rankToMultiplicatorMoney.put(10, 2.4);

        return rankToMultiplicatorMoney.getOrDefault(rang, 1.0);
    }
}
