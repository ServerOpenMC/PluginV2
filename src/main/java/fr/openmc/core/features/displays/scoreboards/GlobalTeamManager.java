package fr.openmc.core.features.displays.scoreboards;

import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.api.scoreboard.repository.ObjectCacheRepository;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.hooks.LuckPermsHook;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalTeamManager {
    private LuckPerms luckPerms = null;
    private final ObjectCacheRepository<SternalBoard> boardCache;
    private final Map<String, Component> groupToPrefixCache = new ConcurrentHashMap<>();

    private final Map<UUID, Map<String, Component>> clientTeamState = new ConcurrentHashMap<>();

    public GlobalTeamManager(ObjectCacheRepository<SternalBoard> boardCache) {
        this.boardCache = boardCache;

        if (LuckPermsHook.isEnable()) {
            this.luckPerms = LuckPermsHook.getApi();
            initSortedGroups();

            this.luckPerms.getEventBus().subscribe(
                    OMCPlugin.getInstance(),
                    GroupDataRecalculateEvent.class,
                    e -> groupToPrefixCache.remove(e.getGroup().getName())
            );
        }
    }

    private void initSortedGroups() {
        List<Group> sortedGroups = new ArrayList<>(luckPerms.getGroupManager().getLoadedGroups());
        sortedGroups.sort(Comparator.comparing(g -> -g.getWeight().orElse(0)));
        for (Group group : sortedGroups) {
            groupToPrefixCache.put(group.getName(), LuckPermsHook.getFormattedPAPIPrefix(group));
        }
    }

    public void updatePlayerTeam(Player player) {
        if (player == null || luckPerms == null) return;

        Group playerGroup = getPlayerHighestWeightGroup(player);
        if (playerGroup == null) return;

        Component prefix = groupToPrefixCache.computeIfAbsent(
                playerGroup.getName(),
                _ -> LuckPermsHook.getFormattedPAPIPrefix(playerGroup)
        );

        updateScoreboardSidebar(player, prefix);
        updateTabListTeamPacket(player, prefix, playerGroup);
    }

    private void updateTabListTeamPacket(Player player, Component prefix, Group group) {
        int weight = group.getWeight().orElse(0);
        String teamName = "lp_%05d_%s".formatted(10000 - weight, group.getName());

        clientTeamState.putIfAbsent(player.getUniqueId(), new ConcurrentHashMap<>());
        Map<String, Component> playerState = clientTeamState.get(player.getUniqueId());

        Component lastPrefix = playerState.get(teamName);

        if (lastPrefix == null) {
            sendTeamPacket(player, teamName, prefix, true, player.getName()); // player inclus dans le create
            playerState.put(teamName, prefix);
        } else if (!lastPrefix.equals(prefix)) {
            sendTeamPacket(player, teamName, prefix, false, null);
            playerState.put(teamName, prefix);
        }
    }

    private void sendTeamPacket(Player viewer, String teamName, Component prefix, boolean create, String entryToAdd) {
        PlayerTeam team = new PlayerTeam(new Scoreboard(), teamName);
        team.setPlayerPrefix(PaperAdventure.asVanilla(prefix));

        var connection = ((CraftPlayer) viewer).getHandle().connection;

        if (create) {
            if (entryToAdd != null) {
                team.getPlayers().add(entryToAdd);
            }
            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        } else {
            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false));
        }
    }

    public void handlePlayerQuit(UUID playerUUuid) {
        clientTeamState.remove(playerUUuid);
    }

    private void updateScoreboardSidebar(Player player, Component prefix) {
        SternalBoard board = boardCache.find(player.getUniqueId());
        if (board == null) return;

        List<Component> lines = board.getLines();
        if (lines.isEmpty()) return;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(player.name())) {
                lines.set(i, prefix.append(player.name()));
                board.updateLines(lines);
                return;
            }
        }
    }

    private Group getPlayerHighestWeightGroup(Player player) {
        var user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        return user.getNodes(NodeType.INHERITANCE).stream()
                .map(NodeType.INHERITANCE::cast)
                .map(node -> luckPerms.getGroupManager().getGroup(node.getGroupName()))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(g -> g.getWeight().orElse(0)))
                .orElse(null);
    }
}