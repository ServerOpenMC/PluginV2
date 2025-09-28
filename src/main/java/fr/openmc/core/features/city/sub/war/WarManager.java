package fr.openmc.core.features.city.sub.war;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.war.commands.AdminWarCommand;
import fr.openmc.core.features.city.sub.war.commands.WarCommand;
import fr.openmc.core.features.city.sub.war.listeners.TntPlaceListener;
import fr.openmc.core.features.city.sub.war.listeners.WarKillListener;
import fr.openmc.core.features.city.sub.war.models.WarHistory;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ChunkPos;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

public class WarManager {
    public static final int TIME_PREPARATION = 5; // in minutes
    public static final int TIME_FIGHT = 30; // in minutes

    public static final long CITY_LOSER_IMMUNITY_FIGHT_COOLDOWN = 2 * 24 * 60 * 60 * 1000L; // 2 jours en millisecondes
    public static final long CITY_WINNER_IMMUNITY_FIGHT_COOLDOWN = 24 * 60 * 60 * 1000L; // 1 jour en millisecondes
    public static final long CITY_DRAW_IMMUNITY_FIGHT_COOLDOWN = 12 * 60 * 60 * 1000L; // 12 heures en millisecondes

    public static final Map<UUID, War> warsByAttacker = new HashMap<>();
    public static final Map<UUID, War> warsByDefender = new HashMap<>();

    private static final Map<UUID, WarPendingDefense> pendingDefenses = new HashMap<>();

    private static Dao<WarHistory, String> warHistoryDeo;

    public static final Map<UUID, WarHistory> warHistory = new HashMap<>();

    /**
     * Initializes the WarManager by registering commands and listeners.
     */
    public WarManager() {
        CommandsManager.getHandler().register(
                new WarCommand(),
                new AdminWarCommand()
        );

        OMCPlugin.registerEvents(
                new WarKillListener(),
                new TntPlaceListener()
        );

        loadWarHistories();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, WarHistory.class);
        warHistoryDeo = DaoManager.createDao(connectionSource, WarHistory.class);

        for (WarHistory history : warHistoryDeo.queryForAll()) {
            warHistory.put(history.getCityUUID(), history);
        }
    }

    public static void loadWarHistories() {
        try {
            List<WarHistory> warHistories = warHistoryDeo.queryForAll();

            warHistories.forEach(war -> {
                UUID cityUUID = war.getCityUUID();

                warHistory.computeIfAbsent(cityUUID, k -> war);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveWarHistories() {
        warHistory.forEach((cityUUID, warHistory) -> {
                    try {
                        warHistoryDeo.createOrUpdate(warHistory);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public static WarHistory createOrGetWarHistory(City city) throws SQLException {
        WarHistory history = warHistory.get(city.getUniqueId());

        if (history == null) {
            history = new WarHistory(city.getUniqueId());
            warHistoryDeo.createOrUpdate(history);

            warHistory.put(history.getCityUUID(), history);
        }

        return history;
    }

    /**
     * Checks if a city is currently in war.
     *
     * @param cityUUID The UUID of the city to check.
     * @return true if the city is in war, false otherwise.
     */
    public static boolean isCityInWar(UUID cityUUID) {
        return warsByAttacker.containsKey(cityUUID) || warsByDefender.containsKey(cityUUID);
    }

    /**
     * Retrieves the war associated with a given city UUID.
     *
     * @param cityUUID The UUID of the city.
     * @return The War object if found, null otherwise.
     */
    public static War getWarByCity(UUID cityUUID) {
        War war = warsByAttacker.get(cityUUID);
        if (war != null) return war;

        war = warsByDefender.get(cityUUID);
        return war;
    }

    /**
     * Starts a war between two cities.
     *
     * @param attacker The city that is attacking.
     * @param defender The city that is defending.
     * @param attackers The list of UUIDs of the players in the attacking city.
     * @param defenders The list of UUIDs of the players in the defending city.
     */
    public static void startWar(City attacker, City defender, List<UUID> attackers, List<UUID> defenders) {
        War war = new War(attacker, defender, attackers, defenders);

        warsByAttacker.put(attacker.getUniqueId(), war);
        warsByDefender.put(defender.getUniqueId(), war);
    }

    /**
     * Ends a war between two cities.
     * This method will determine the winner and loser based on various criteria such as mascot death, HP, and kills.
     * It will also handle the transfer of claims and update the power points and balances of the cities involved.
     *
     * @param war The War object representing the war to be ended.
     */
    public static void endWar(War war) {
        War warRemoved = warsByAttacker.remove(war.getCityAttacker().getUniqueId());
        warsByDefender.remove(war.getCityDefender().getUniqueId());

        pendingDefenses.remove(war.getCityAttacker().getUniqueId());
        pendingDefenses.remove(war.getCityDefender().getUniqueId());

        if (warRemoved == null) return;

        Mascot attackerMascot = war.getCityAttacker().getMascot();
        Mascot defenderMascot = war.getCityDefender().getMascot();

        boolean attackerDead = !attackerMascot.isAlive();
        boolean defenderDead = !defenderMascot.isAlive();

        City winner = null;
        City loser = null;
        WinReason winReason = WinReason.DRAW;

        if (attackerDead && !defenderDead) {
            winner = war.getCityDefender();
            loser = war.getCityAttacker();
            winReason = WinReason.MASCOT_DEATH;
        } else if (defenderDead && !attackerDead) {
            winner = war.getCityAttacker();
            loser = war.getCityDefender();
            winReason = WinReason.MASCOT_DEATH;
        } else if (!attackerDead && !defenderDead) {
            LivingEntity attackerEntity = (LivingEntity) attackerMascot.getEntity();
            LivingEntity defenderEntity = (LivingEntity) attackerMascot.getEntity();
            double attackerHP = attackerEntity.getHealth();
            double defenderHP = defenderEntity.getHealth();

            if (attackerHP > defenderHP) {
                winner = war.getCityAttacker();
                loser = war.getCityDefender();
                winReason = WinReason.MASCOT_HP;
            } else if (defenderHP > attackerHP) {
                winner = war.getCityDefender();
                loser = war.getCityAttacker();
                winReason = WinReason.MASCOT_HP;
            } else {
                int attackerKills = war.getAttackersKill();
                int defenderKills = war.getDefendersKill();
                if (attackerKills > defenderKills) {
                    winner = war.getCityAttacker();
                    loser = war.getCityDefender();
                    winReason = WinReason.KILLS;
                } else if (defenderKills > attackerKills) {
                    winner = war.getCityDefender();
                    loser = war.getCityAttacker();
                    winReason = WinReason.KILLS;
                } else {
                    winner = war.getCityDefender();
                    loser = war.getCityAttacker();
                    winReason = WinReason.DRAW;
                }
            }
        }

        try {
            if (winner != null) {
                WarHistory winnerHistory = createOrGetWarHistory(winner);
                winnerHistory.addParticipation();
                winnerHistory.addWin();
                warHistoryDeo.update(winnerHistory);
            }

            if (loser != null) {
                WarHistory loserHistory = createOrGetWarHistory(loser);
                loserHistory.addParticipation();
                warHistoryDeo.update(loserHistory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int claimsWon = -1;
        double amountStolen = -1;
        int powerChange = -1;
        double bonusMoney = 0;
        if (!winReason.equals(WinReason.DRAW)) {
            int powerPointWinner = winner.getPowerPoints() == 0 ? 4 : winner.getPowerPoints();
            int powerPointLoser = loser.getPowerPoints() == 0 ? 4 : loser.getPowerPoints();
            double ratio = powerPointWinner / powerPointLoser;
            ratio = Math.max(0.2, Math.min(2.5, ratio));

            int base = (war.getAttackers().size() + war.getDefenders().size()) / 2;
            powerChange = (int) Math.ceil(base * (1 / ratio));

            winner.updatePowerPoints(powerChange);
            loser.updatePowerPoints(-powerChange);

            amountStolen = loser.getBalance() * 0.15;
            winner.updateBalance(amountStolen);
            loser.updateBalance(-amountStolen);

            boolean mascotKilled = winReason.equals(WinReason.MASCOT_DEATH);
            int level = loser.getMascot().getLevel();

            int totalClaims = loser.getChunks().size();

            double percent = mascotKilled ? 0.10 : 0.05;

            claimsWon = (int) Math.ceil(totalClaims * percent * (1 + (level / 10.0)));

            DynamicCooldownManager.use(loser.getUniqueId(), "city:immunity", CITY_LOSER_IMMUNITY_FIGHT_COOLDOWN);
            DynamicCooldownManager.use(winner.getUniqueId(), "city:immunity", CITY_WINNER_IMMUNITY_FIGHT_COOLDOWN);

            int actualClaims = transferChunksAfterWar(winner, loser, claimsWon);
            if (actualClaims < claimsWon) {
                double missingRatio = (claimsWon - actualClaims) / (double) claimsWon;
                bonusMoney = Math.ceil(amountStolen * 0.5 * missingRatio);
                winner.updateBalance(bonusMoney);
            }
        } else {
            DynamicCooldownManager.use(war.getCityDefender().getUniqueId(), "city:immunity", CITY_DRAW_IMMUNITY_FIGHT_COOLDOWN);
            DynamicCooldownManager.use(war.getCityAttacker().getUniqueId(), "city:immunity", CITY_DRAW_IMMUNITY_FIGHT_COOLDOWN);
        }

        broadcastWarResult(war, winner, loser, winReason, powerChange, amountStolen, bonusMoney, Math.abs(claimsWon));
    }

    /**
     * Broadcasts the result of a war to the members of the winning and losing cities.
     *
     * @param war        The War object containing details about the war.
     * @param winner     The city that won the war.
     * @param loser      The city that lost the war.
     * @param reason     The reason for the win (e.g., mascot death, HP, kills, draw).
     * @param powerChange The change in power points for the winning city.
     * @param amountStolen The amount of money stolen from the losing city.
     * @param bonusMoney Additional bonus money awarded to the winning city.
     * @param claimNumber The number of claims transferred to the winning city.
     */
    public static void broadcastWarResult(War war, City winner, City loser, WinReason reason, int powerChange, double amountStolen, double bonusMoney, int claimNumber) {
        int killsWinner = war.getCityAttacker().equals(winner) ? war.getAttackersKill() : war.getDefendersKill();
        int killsLoser = war.getCityAttacker().equals(loser) ? war.getAttackersKill() : war.getDefendersKill();

        if (reason == WinReason.DRAW) {
            String message = String.format("""
                            §8§m                                                     §r
                            §7
                            §c§lGUERRE!§r §7C'est la fin des combats!§7
                            §8§oIl y a eu égalité !
                            §7
                            §7Statistiques globales:
                            §7 - §cKills de %s : §f%d
                            §7 - §9Kills de %s : §f%d
                            §7
                                    §8§m                                                     §r""",
                    war.getCityAttacker().getName(), killsWinner, war.getCityDefender().getName(), killsLoser);


            for (UUID uuid : winner.getMembers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (player.isOnline()) player.sendMessage(Component.text(message));
            }

            for (UUID uuid : loser.getMembers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (player.isOnline()) player.sendMessage(Component.text(message));
            }
            return;
        }

        String message = """
                §8§m                                                     §r
                §7
                §c§lGUERRE!§r §7C'est la fin des combats!§7
                §8§oVous avez %s contre %s!
                §8§o%s
                §7
                §7Statistiques globales:
                §7 - §cKills de %s : §f%d
                §7 - §9Kills de %s : §f%d
                §7
                %s:
                §7 %s
                §7 %s
                §7 %s
                §7
                §8§m                                                     §r""";


        String winnerMessage = String.format(
                message,
                "gagné",
                loser.getName(),
                switch (reason) {
                    case MASCOT_DEATH -> "Vous avez tué la Mascotte adverse!";
                    case MASCOT_HP -> "Votre Mascotte a eu le plus de points de vie!";
                    case KILLS -> "Votre ville a tué le plus d'adversaires!";
                    case DRAW -> "C'est une égalité!";
                }, winner.getName(), killsWinner, loser.getName(), killsLoser,
                "§6§lRécompenses",
                "+ " + powerChange + " points de puissance",
                "+ " + EconomyManager.getFormattedSimplifiedNumber(amountStolen) + EconomyManager.getEconomyIcon() + " volés à l'adversaire" + ((bonusMoney > 0) ? " + " + EconomyManager.getFormattedSimplifiedNumber(bonusMoney) + EconomyManager.getEconomyIcon() + " bonus" : ""),
                "+ " + claimNumber + " territoire(s) conquis"
        );
        for (UUID uuid : winner.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.isOnline()) player.sendMessage(Component.text(winnerMessage));
        }

        String loserMessage = String.format(
                message,
                "perdu",
                loser.getName(),
                switch (reason) {
                    case MASCOT_DEATH -> "Votre Mascotte a été tuée!";
                    case MASCOT_HP -> "Votre Mascotte a eu le moins de points de vie!";
                    case KILLS -> "L'adversaire a tué le plus de monde!";
                    case DRAW -> "C'est une égalité!";
                }, winner.getName(), killsWinner, loser.getName(), killsLoser,
                "§c§lPertes",
                "- " + powerChange + " points de puissance",
                "- " + EconomyManager.getFormattedSimplifiedNumber(amountStolen) + EconomyManager.getEconomyIcon() + " perdu",
                "- " + claimNumber + " territoire(s) perdus"
        );

        for (UUID uuid : loser.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.isOnline()) player.sendMessage(Component.text(loserMessage));
        }
    }

    /**
     * Transfers chunks from the loser city to the winner city after a war.
     * The transfer is done based on adjacency to the winner's chunks and the mascot's chunk.
     *
     * @param winner      The winning city.
     * @param loser       The losing city.
     * @param claimAmount The number of chunks to transfer.
     * @return The number of chunks actually transferred.
     */
    public static int transferChunksAfterWar(City winner, City loser, int claimAmount) {
        if (claimAmount <= 0) return 0;

        ChunkPos mascotVec = new ChunkPos(
                loser.getMascot().getChunk().getX(),
                loser.getMascot().getChunk().getZ()
        );

        Set<ChunkPos> adjacentChunks = new HashSet<>();
        for (ChunkPos wChunk : winner.getChunks()) {
            int wx = wChunk.x(), wz = wChunk.z();

            ChunkPos[] neighbors = {
                    new ChunkPos(wx + 1, wz),
                    new ChunkPos(wx - 1, wz),
                    new ChunkPos(wx, wz + 1),
                    new ChunkPos(wx, wz - 1)
            };
            for (ChunkPos nb : neighbors) {
                if (nb.equals(mascotVec)) continue;

                if (loser.getChunks().contains(nb)) {
                    adjacentChunks.add(nb);
                }
            }
        }

        final int[] transferred = {0};

        BiConsumer<Queue<ChunkPos>, Set<ChunkPos>> bfsCapture = (queue, visited) -> {
            while (!queue.isEmpty() && transferred[0] < claimAmount) {
                ChunkPos current = queue.poll();
                int cx = current.x(), cz = current.z();

                ChunkPos[] neighs = {
                        new ChunkPos(cx + 1, cz),
                        new ChunkPos(cx - 1, cz),
                        new ChunkPos(cx, cz + 1),
                        new ChunkPos(cx, cz - 1)
                };
                for (ChunkPos nb : neighs) {
                    if (visited.contains(nb) || nb.equals(mascotVec)) continue;

                    if (loser.getChunks().contains(nb)) {
                        loser.removeChunk(nb.x(), nb.z());
                        winner.addChunk(nb.x(), nb.z());
                        visited.add(nb);
                        queue.add(nb);
                        transferred[0]++;
                        if (transferred[0] >= claimAmount) break;
                    }
                }
            }
        };

        if (!adjacentChunks.isEmpty()) {
            List<ChunkPos> toSteal = new ArrayList<>(adjacentChunks);
            int initialSteal = Math.min(toSteal.size(), claimAmount);
            Queue<ChunkPos> queue = new LinkedList<>();
            Set<ChunkPos> visited = new HashSet<>();
            for (int i = 0; i < initialSteal; i++) {
                ChunkPos c = toSteal.get(i);
                loser.removeChunk(c.x(), c.z());
                winner.addChunk(c.x(), c.z());
                queue.add(c);
                visited.add(c);
                transferred[0]++;
            }
            bfsCapture.accept(queue, visited);
        } else {
            // Try from border
            List<ChunkPos> borderChunks = new ArrayList<>();
            for (ChunkPos lChunk : loser.getChunks()) {
                if (lChunk.equals(mascotVec)) continue;
                int lx = lChunk.x(), lz = lChunk.z();
                ChunkPos[] neighs = {
                        new ChunkPos(lx + 1, lz),
                        new ChunkPos(lx - 1, lz),
                        new ChunkPos(lx, lz + 1),
                        new ChunkPos(lx, lz - 1)
                };

                for (ChunkPos nb : neighs) {
                    if (!loser.getChunks().contains(nb)) {
                        borderChunks.add(lChunk);
                        break;
                    }
                }
            }
            if (!borderChunks.isEmpty()) {
                Collections.shuffle(borderChunks);
                ChunkPos seed = borderChunks.getFirst();

                loser.removeChunk(seed.x(), seed.z());
                winner.addChunk(seed.x(), seed.z());
                Queue<ChunkPos> queue = new LinkedList<>();
                Set<ChunkPos> visited = new HashSet<>();
                queue.add(seed);
                visited.add(seed);
                transferred[0]++;

                bfsCapture.accept(queue, visited);
            }
        }

        return transferred[0];
    }

    /**
     * Gets a formatted string representation of the war phase.
     *
     * @param phase The war phase to format.
     * @return A string representing the formatted phase.
     */
    public static String getFormattedPhase(War.WarPhase phase) {
        return switch (phase) {
            case PREPARATION -> "Préparation";
            case COMBAT -> "Combat";
            case ENDED -> "Fin";
        };
    }

    /**
     * Adds a pending defense request for a city.
     *
     * @param defense The WarPendingDefense object containing the defense details.
     */
    public static void addPendingDefense(WarPendingDefense defense) {
        pendingDefenses.put(defense.getDefender().getUniqueId(), defense);
    }

    /**
     * Removes a pending defense request for a city.
     *
     * @param city The city for which the pending defense is to be removed.
     */
    public static WarPendingDefense getPendingDefenseFor(City city) {
        return pendingDefenses.get(city.getUniqueId());
    }

    public enum WinReason {
        MASCOT_DEATH,
        MASCOT_HP,
        KILLS,
        DRAW
    }
}
