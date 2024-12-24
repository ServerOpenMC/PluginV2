package fr.openmc.core.features.contest.managers;

import fr.openmc.core.features.contest.ContestPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Random;

@Setter
public class ContestPlayerManager  {
    @Getter static ContestPlayerManager instance;
    private ContestManager contestManager;

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
        int[] pointsRank = {10000, 2500, 2000, 1500, 1000, 750, 500, 250, 100, 0};
        String[] categories = {
                "Dictateur en ",
                "Colonel en ",
                "Addict en ",
                "Dieu en ",
                "Légende en ",
                "Sénior en ",
                "Pro en ",
                "Semi-Pro en ",
                "Amateur en ",
                "Noob en "
        };

        for (int i = 0; i < pointsRank.length; i++) {
            if (points >= pointsRank[i]) {
                return categories[i];
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

        if(points >= 10000) {
            return 0;
        } else if (points >= 2500) {
            return 10000;
        } else if (points >= 2000) {
            return 2500;
        } else if (points >= 1500) {
            return 2000;
        } else if (points >= 1000) {
            return 1500;
        } else if (points >= 750) {
            return 1000;
        } else if (points >= 500) {
            return 750;
        } else if (points >= 250) {
            return 500;
        } else if (points >= 100) {
            return 250;
        } else if (points >= 0) {
            return 100;
        }

        return 0;
    }

    public int getRankContestFromOfflineInt(OfflinePlayer player) {

        int points = contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints();

        if(points >= 10000) {
            return 10;
        } else if (points >= 2500) {
            return 9;
        } else if (points >= 2000) {
            return 8;
        } else if (points >= 1500) {
            return 7;
        } else if (points >= 1000) {
            return 6;
        } else if (points >= 750) {
            return 5;
        } else if (points >= 500) {
            return 4;
        } else if (points >= 250) {
            return 3;
        } else if (points >= 100) {
            return 2;
        } else if (points >= 0) {
            return 1;
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

    public int giveRandomly(Integer min, Integer max) {
        return new Random().nextInt(min, max);
    }

    public double getMultiMoneyFromRang(int rang) {
        if(rang == 10) {
            return 2.4;
        } else if (rang == 9) {
            return 2.0;
        } else if (rang == 8) {
            return 1.8;
        } else if (rang == 7) {
            return 1.7;
        } else if (rang == 6) {
            return 1.6;
        } else if (rang == 5) {
            return 1.5;
        } else if (rang == 4) {
            return 1.4;
        } else if (rang == 3) {
            return 1.3;
        } else if (rang == 2) {
            return 1.1;
        } else if (rang == 1) {
            return 1;
        }

        return 0;
    }
}
