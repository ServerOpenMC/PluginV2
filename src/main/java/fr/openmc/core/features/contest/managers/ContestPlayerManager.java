package fr.openmc.core.features.contest.managers;

import fr.openmc.core.features.contest.ContestPlayer;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class ContestPlayerManager  {
    @Getter static ContestPlayerManager instance;
    @Setter private ContestManager contestManager;

    public ContestPlayerManager() {
        instance = this;
        contestManager = ContestManager.getInstance();
    };

    public ResultSet getAllPlayer() {
        try {
            PreparedStatement query = DatabaseManager.getConnection().prepareStatement("SELECT * FROM contest_camps");
            ResultSet rs = query.executeQuery();
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPlayerCampName(Player player) {
        int campInteger = contestManager.dataPlayer.get(player.getUniqueId().toString()).camp();
        return contestManager.data.get("camp" + campInteger);
    }

    public Integer getOfflinePlayerCamp(OfflinePlayer player) {
        String sql = "SELECT * FROM contest_camps WHERE minecraft_uuid = ?";
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            states.setString(1, player.getUniqueId().toString());
            ResultSet result = states.executeQuery();
            if (result.next()) {
                return result.getInt("camps");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public String getOfflinePlayerCampName(OfflinePlayer player) {
        Integer campInteger = getOfflinePlayerCamp(player);
        return contestManager.data.get("camp" + campInteger);
    }

    public ChatColor getOfflinePlayerCampChatColor(OfflinePlayer player) {
        Integer campInteger = getOfflinePlayerCamp(player);
        String color = contestManager.data.get("color" + campInteger);
        return ChatColor.valueOf(color);
    }

    public void setPointsPlayer(Player player, int points) {
        ContestManager manager = ContestManager.getInstance();
        ContestPlayer data = manager.dataPlayer.get(player.getUniqueId().toString());

        manager.dataPlayer.put(player.getUniqueId().toString(), new ContestPlayer(data.name(), points, data.camp(), data.color()));
    }

    public Integer getRankPlayerInContest(Integer pointsDep) {
        String sql = "SELECT COUNT(*) AS rank FROM contest_camps WHERE point_dep > ?";
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            states.setInt(1, pointsDep);
            ResultSet result = states.executeQuery();
            if (result.next()) {
                return result.getInt("rank") + 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public String getRankContest(Player player) {
        int points = contestManager.dataPlayer.get(player.getUniqueId().toString()).points();

        int[] seuils = {10000, 2500, 2000, 1500, 1000, 750, 500, 250, 100, 0};
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
        
        for (int i = 0; i < seuils.length; i++) {
            if (points >= seuils[i]) {
                return categories[i];
            }
        }

        return "";
    }

    public int getRepPointsToRank(Player player) {
        int points = contestManager.dataPlayer.get(player.getUniqueId().toString()).points();

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

    public String getRankContestFromOffline(OfflinePlayer player) {
        String sql = "SELECT * FROM contest_camps WHERE minecraft_uuid = ?";
        int points = 0;
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            states.setString(1, player.getUniqueId().toString());
            ResultSet result = states.executeQuery();
            if (result.next()) {
                points = result.getInt("point_dep");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(points >= 10000) {
            return "Dictateur en  ";
        } else if (points >= 2500) {
            return "Colonel en ";
        } else if (points >= 2000) {
            return "Addict en ";
        } else if (points >= 1500) {
            return "Dieu en ";
        } else if (points >= 1000) {
            return "Légende en ";
        } else if (points >= 750) {
            return "Sénior en ";
        } else if (points >= 500) {
            return "Pro en ";
        } else if (points >= 250) {
            return "Semi-Pro en ";
        } else if (points >= 100) {
            return "Amateur en ";
        } else if (points >= 0) {
            return "Noob en ";
        }

        return "";
    }

    public int getRankContestFromOfflineInt(OfflinePlayer player) {
        String sql = "SELECT * FROM contest_camps WHERE minecraft_uuid = ?";
        int points = 0;
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            states.setString(1, player.getUniqueId().toString());
            ResultSet result = states.executeQuery();
            if (result.next()) {
                points = result.getInt("point_dep");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
        String sql = "SELECT * FROM contest_camps WHERE minecraft_uuid = ?";
        int playerCamp = 0;
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            states.setString(1, player.getUniqueId().toString());
            ResultSet result = states.executeQuery();
            if (result.next()) {
                playerCamp = result.getInt("camps");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
        int moneyGive = new Random().nextInt(min, max);
        return moneyGive;
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
