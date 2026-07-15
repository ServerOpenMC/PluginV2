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
import net.minecraft.server.MinecraftServer;
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
    private record TeamState(Set<String> members, Component prefix) {}
    private final Map<String, TeamState> teams = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerTeam = new ConcurrentHashMap<>();

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

        UUID uuid = player.getUniqueId();
        boolean firstSync = !playerTeam.containsKey(uuid);

        Group playerGroup = getPlayerHighestWeightGroup(player);
        if (playerGroup == null) return;

        Component prefix = groupToPrefixCache.computeIfAbsent(
                playerGroup.getName(),
                _ -> LuckPermsHook.getFormattedPAPIPrefix(playerGroup)
        );

        int weight = playerGroup.getWeight().orElse(0);
        String newTeamName = "lp_%05d_%s".formatted(10000 - weight, playerGroup.getName());
        String entry = player.getName();

        if (firstSync) syncExistingTeamsTo(player);

        String oldTeamName = playerTeam.get(uuid);
        if (oldTeamName != null && !oldTeamName.equals(newTeamName)) {
            removePlayerFromTeam(oldTeamName, entry);
        }

        TeamState state = teams.get(newTeamName);

        if (state == null) {
            Set<String> members = ConcurrentHashMap.newKeySet();
            members.add(entry);
            teams.put(newTeamName, new TeamState(members, prefix));
            broadcastCreateTeam(newTeamName, prefix, entry);
        } else if (!state.members().contains(entry)) {
            state.members().add(entry);
            broadcastAddPlayerToTeam(newTeamName, prefix, entry);
        } else if (!state.prefix().equals(prefix)) {
            teams.put(newTeamName, new TeamState(state.members(), prefix));
            broadcastUpdateTeamPrefix(newTeamName, prefix);
        }

        playerTeam.put(uuid, newTeamName);

        updateScoreboardSidebar(player, prefix);
    }

    private void removePlayerFromTeam(String teamName, String entry) {
        TeamState state = teams.get(teamName);
        if (state == null) return;

        state.members().remove(entry);

        PlayerTeam team = new PlayerTeam(new Scoreboard(), teamName);
        broadcast(ClientboundSetPlayerTeamPacket.createPlayerPacket(team, entry, ClientboundSetPlayerTeamPacket.Action.REMOVE));

        if (state.members().isEmpty()) {
            teams.remove(teamName);
            broadcast(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
        }
    }

    private void broadcastCreateTeam(String teamName, Component prefix, String entry) {
        PlayerTeam team = new PlayerTeam(new Scoreboard(), teamName);
        team.setPlayerPrefix(PaperAdventure.asVanilla(prefix));
        team.getPlayers().add(entry);
        broadcast(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
    }

    private void broadcastAddPlayerToTeam(String teamName, Component prefix, String entry) {
        PlayerTeam team = new PlayerTeam(new Scoreboard(), teamName);
        team.setPlayerPrefix(PaperAdventure.asVanilla(prefix));
        broadcast(ClientboundSetPlayerTeamPacket.createPlayerPacket(team, entry, ClientboundSetPlayerTeamPacket.Action.ADD));
    }

    private void broadcastUpdateTeamPrefix(String teamName, Component prefix) {
        PlayerTeam team = new PlayerTeam(new Scoreboard(), teamName);
        team.setPlayerPrefix(PaperAdventure.asVanilla(prefix));
        broadcast(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false));
    }

    private void syncExistingTeamsTo(Player player) {
        var connection = ((CraftPlayer) player).getHandle().connection;

        for (var entry : teams.entrySet()) {
            TeamState state = entry.getValue();
            if (state.members().isEmpty()) continue;

            PlayerTeam team = new PlayerTeam(new Scoreboard(), entry.getKey());
            team.setPlayerPrefix(PaperAdventure.asVanilla(state.prefix()));
            team.getPlayers().addAll(state.members());

            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        }
    }

    private void broadcast(ClientboundSetPlayerTeamPacket packet) {
        MinecraftServer.getServer().getPlayerList().broadcastAll(packet);
    }

    public void handlePlayerQuit(Player player) {
        String teamName = playerTeam.remove(player.getUniqueId());
        if (teamName != null) {
            removePlayerFromTeam(teamName, player.getName());
        }
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