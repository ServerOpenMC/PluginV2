package fr.openmc.core.features.scoreboards;

import fr.openmc.core.utils.LuckPermsAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalTeamManager {
    private LuckPerms luckPerms = null;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private List<Group> sortedGroups;
    private final Map<Group, String> groupToTeamNameCache = new ConcurrentHashMap<>();

    public GlobalTeamManager(Map<UUID, Scoreboard> playerScoreboards) {
        this.playerScoreboards = playerScoreboards;

        if (LuckPermsAPI.hasLuckPerms()) {
            this.luckPerms = LuckPermsAPI.getApi();
            initSortedGroups();
            createTeams();
        }
    }

    private void initSortedGroups() {
        sortedGroups = new ArrayList<>(luckPerms.getGroupManager().getLoadedGroups());
        sortedGroups.sort(Comparator.comparing(g -> -g.getWeight().orElse(0)));

        for (int i = 0; i < sortedGroups.size(); i++) {
            Group group = sortedGroups.get(i);
            String teamName = String.valueOf((char) ('a' + i));
            groupToTeamNameCache.put(group, teamName);
        }
    }

    public void createTeams() {
        if (sortedGroups == null) initSortedGroups();

        for (Scoreboard scoreboard : playerScoreboards.values()) {
            for (Group group : sortedGroups) {
                String teamName = groupToTeamNameCache.get(group);
                createTeam(scoreboard, teamName, group);
            }
        }
    }

    public void createTeam(Scoreboard scoreboard, String teamName, Group group) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) team = scoreboard.registerNewTeam(teamName);
        team.prefix(LuckPermsAPI.getFormattedPAPIPrefix(group));
    }

    public void updateTeam(Scoreboard scoreboard, String teamName, Group group) {
        Team team = scoreboard.getTeam(teamName);
        if (team != null) team.prefix(LuckPermsAPI.getFormattedPAPIPrefix(group));
    }

    public void updatePlayerTeam(Player player) {
        if (player == null || luckPerms == null) return;

        Group playerGroup = getPlayerHighestWeightGroup(player);
        if (playerGroup == null) return;
        String teamName = groupToTeamNameCache.get(playerGroup);
        if (teamName == null) return;
        for (Scoreboard scoreboard : playerScoreboards.values()) {
            createTeamsIfNotExists(scoreboard);
            for (Team team : scoreboard.getTeams())
                if (team.hasEntry(player.getName())) team.removeEntry(player.getName());

            updateTeam(scoreboard, teamName, playerGroup);

            Team team = scoreboard.getTeam(teamName);
            if (team != null) team.addEntry(player.getName());
        }
    }

    private Group getPlayerHighestWeightGroup(Player player) {
        var user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        if (user == null) return null;

        return user.getNodes(NodeType.INHERITANCE).stream()
                .map(NodeType.INHERITANCE::cast)
                .map(inheritanceNode -> luckPerms.getGroupManager().getGroup((inheritanceNode).getGroupName()))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(group -> group.getWeight().orElse(0)))
                .orElse(null);
    }

    private void createTeamsIfNotExists(Scoreboard scoreboard) {
        for (Group group : sortedGroups) {
            String teamName = groupToTeamNameCache.get(group);
            if (teamName != null && scoreboard.getTeam(teamName) == null) {
                createTeam(scoreboard, teamName, group);
            }
        }
    }
}