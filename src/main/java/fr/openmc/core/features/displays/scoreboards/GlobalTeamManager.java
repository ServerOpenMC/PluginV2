package fr.openmc.core.features.displays.scoreboards;

import fr.openmc.api.hooks.LuckPermsHook;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.api.scoreboard.repository.ObjectCacheRepository;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalTeamManager {
    private LuckPerms luckPerms = null;
    private final ObjectCacheRepository<SternalBoard> boardCache;
    private List<Group> sortedGroups;
    private final Map<Group, Component> groupToPrefixCache = new ConcurrentHashMap<>();

    public GlobalTeamManager(ObjectCacheRepository<SternalBoard> boardCache) {
        this.boardCache = boardCache;

        if (LuckPermsHook.isHasLuckPerms()) {
            this.luckPerms = LuckPermsHook.getApi();
            initSortedGroups();
        }
    }

    private void initSortedGroups() {
        sortedGroups = new ArrayList<>(luckPerms.getGroupManager().getLoadedGroups());
        sortedGroups.sort(Comparator.comparing(g -> -g.getWeight().orElse(0)));

        for (Group group : sortedGroups) {
            groupToPrefixCache.put(group, LuckPermsHook.getFormattedPAPIPrefix(group));
        }
    }

    public void updatePlayerTeam(Player player) {
        if (player == null || luckPerms == null) return;

        Group playerGroup = getPlayerHighestWeightGroup(player);
        if (playerGroup == null) return;

        Component prefix = groupToPrefixCache.getOrDefault(playerGroup, Component.empty());

        SternalBoard board = boardCache.find(player.getUniqueId());
        if (board == null) return;

        List<Component> lines = board.getLines();
        if (lines.isEmpty()) return;

        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            if (line.contains(player.name())) {
                lines.set(i, prefix.append(player.name()));
                board.updateLines(lines);
                return;
            }
        }
    }

    private Group getPlayerHighestWeightGroup(Player player) {
        UUID uuid = player.getUniqueId();
        var user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) return null;

        return user.getNodes(NodeType.INHERITANCE).stream()
                .map(NodeType.INHERITANCE::cast)
                .map(node -> luckPerms.getGroupManager().getGroup(node.getGroupName()))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(group -> group.getWeight().orElse(0)))
                .orElse(null);
    }
}
