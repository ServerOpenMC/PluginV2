package fr.openmc.core.features.contest.managers;

import fr.openmc.core.features.contest.ContestPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Setter
public class ContestPlayerManager  {
    @Getter static ContestPlayerManager instance;
    private ContestManager contestManager;

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

    public ContestPlayerManager() {
        instance = this;
        contestManager = ContestManager.getInstance();
    }

    public String getPlayerCampName(Player player) {
        int campInteger = contestManager.dataPlayer.get(player.getUniqueId().toString()).getCamp();
        return contestManager.data.get("camp" + campInteger);
    }

    public void setPointsPlayer(Player player, int points) {
        ContestManager manager = ContestManager.getInstance();
        ContestPlayer data = manager.dataPlayer.get(player.getUniqueId().toString());

        manager.dataPlayer.put(player.getUniqueId().toString(), new ContestPlayer(data.getName(), points, data.getCamp(), data.getColor()));
    }

    public String getTitleWithPoints(int points) {
        for (Map.Entry<Integer, String> entry : RANKS.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }
        return "";
    }

    public String getTitleContest(Player player) {
        int points = contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints();

        return getTitleWithPoints(points);
    }

    public int getGoalPointsToRankUp(Player player) {
        int points = contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints();

        for (Map.Entry<Integer, Integer> entry : GOAL_POINTS.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }

        return -1;
    }

    public int getRankContestFromOfflineInt(OfflinePlayer player) {
        int points = contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints();

        for (Map.Entry<Integer, Integer> entry : POINTS_TO_INT_RANK.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }

        return 0;
    }

    public boolean hasWinInCampFromOfflinePlayer(OfflinePlayer player) {
        int playerCamp = contestManager.dataPlayer.get(player.getUniqueId().toString()).getCamp();

        int points1 = contestManager.data.getPoint1();
        int points2 = contestManager.data.getPoint2();


        int vote1 = contestManager.getVoteTaux(1);
        int vote2 = contestManager.getVoteTaux(2);
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
        if (points2 > points1 && playerCamp == 2) {
            return true;
        }
        return false;
    }

    public double getMultiplicatorFromRank(int rang) {
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
