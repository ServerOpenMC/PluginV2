package fr.openmc.core.features.city.sub.war;

import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.war.commands.AdminWarCommand;
import fr.openmc.core.features.city.sub.war.commands.WarCommand;
import fr.openmc.core.features.city.sub.war.listeners.WarKillListener;
import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

public class WarManager {

    public static int TIME_PREPARATION = 10; // in minutes
    public static int TIME_FIGHT = 30; // in minutes

    public static long CITY_LOSER_IMMUNITY_FIGHT_COOLDOWN = 2 * 24 * 60 * 60 * 1000L; // 2 jours en millisecondes
    public static long CITY_WINNER_IMMUNITY_FIGHT_COOLDOWN = 24 * 60 * 60 * 1000L; // 1 jours en millisecondes
    public static long CITY_DRAW_IMMUNITY_FIGHT_COOLDOWN = 12 * 60 * 60 * 1000L; // 12 heures en millisecondes

    public static final Map<String, War> warsByAttacker = new HashMap<>();
    public static final Map<String, War> warsByDefender = new HashMap<>();

    private static final Map<String, WarPendingDefense> pendingDefenses = new HashMap<>();

    public WarManager() {
        CommandsManager.getHandler().register(
                new WarCommand(),
                new AdminWarCommand()
        );

        OMCPlugin.registerEvents(
                new WarKillListener()
        );
    }

    public static boolean isCityInWar(String cityUUID) {
        return warsByAttacker.containsKey(cityUUID) || warsByDefender.containsKey(cityUUID);
    }

    public static War getWarByCity(String cityUUID) {
        War war = warsByAttacker.get(cityUUID);
        if (war != null) return war;

        war = warsByDefender.get(cityUUID);
        if (war != null) return war;

        return null;
    }

    public static void startWar(City attacker, City defender, List<UUID> attackers, List<UUID> defenders) {
        War war = new War(attacker, defender, attackers, defenders);

        warsByAttacker.put(attacker.getUUID(), war);
        warsByDefender.put(defender.getUUID(), war);
    }

    public static void endWar(War war) {
        War warRemoved;
        warRemoved = warsByAttacker.remove(war.getCityAttacker().getUUID());
        warRemoved = warsByDefender.remove(war.getCityDefender().getUUID());

        if (warRemoved == null) return;

        war.setPhase(War.WarPhase.ENDED);

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
                    winReason = WinReason.DRAW;
                }
            }
        }

        int claimsWon = -1;
        double amountStolen = -1;
        int powerChange = -1;
        double bonusMoney = 0;
        if (!winReason.equals(WinReason.DRAW)) {
            powerChange = (war.getAttackers().size() + war.getDefenders().size()) / 2;
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

            DynamicCooldownManager.use(loser.getUUID(), "city:immunity", CITY_LOSER_IMMUNITY_FIGHT_COOLDOWN);
            DynamicCooldownManager.use(winner.getUUID(), "city:immunity", CITY_WINNER_IMMUNITY_FIGHT_COOLDOWN);

            int actualClaims = transferChunksAfterWar(winner, loser, claimsWon);
            if (actualClaims < claimsWon) {
                double missingRatio = (claimsWon - actualClaims) / (double) claimsWon;
                bonusMoney = Math.ceil(amountStolen * 0.5 * missingRatio);
                winner.updateBalance(bonusMoney);

                System.out.println("Bonus accordé à " + winner.getName() + " pour claims manquants: " + bonusMoney);
            }
        } else {
            DynamicCooldownManager.use(war.getCityDefender().getUUID(), "city:immunity", CITY_DRAW_IMMUNITY_FIGHT_COOLDOWN);
            DynamicCooldownManager.use(war.getCityAttacker().getUUID(), "city:immunity", CITY_DRAW_IMMUNITY_FIGHT_COOLDOWN);
        }

        System.out.println("War ended between " + war.getCityAttacker().getName() + " and " + war.getCityDefender().getName() +
                " with winner: " + (winner != null ? winner.getName() : "none") +
                ", loser: " + (loser != null ? loser.getName() : "none") +
                ", reason: " + winReason);
        System.out.println("Power change: " + powerChange +
                ", Amount stolen: " + amountStolen +
                ", Base claims: " + claimsWon);

        broadcastWarResult(war, winner, loser, winReason, powerChange, amountStolen, bonusMoney, Math.abs(claimsWon));
    }

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

    public static int transferChunksAfterWar(City winner, City loser, int claimAmount) {
        if (claimAmount <= 0) return 0;

        BlockVector2 mascotVec = BlockVector2.at(
                loser.getMascot().getChunk().getX(),
                loser.getMascot().getChunk().getZ()
        );

        Set<BlockVector2> adjacentChunks = new HashSet<>();
        for (BlockVector2 wChunk : winner.getChunks()) {
            int wx = wChunk.getX(), wz = wChunk.getZ();

            BlockVector2[] neighbors = {
                    BlockVector2.at(wx + 1, wz),
                    BlockVector2.at(wx - 1, wz),
                    BlockVector2.at(wx, wz + 1),
                    BlockVector2.at(wx, wz - 1)
            };
            for (BlockVector2 nb : neighbors) {
                if (nb.equals(mascotVec)) continue;

                if (loser.getChunks().contains(nb)) {
                    adjacentChunks.add(nb);
                }
            }
        }

        final int[] transferred = {0};

        BiConsumer<Queue<BlockVector2>, Set<BlockVector2>> bfsCapture = (queue, visited) -> {
            while (!queue.isEmpty() && transferred[0] < claimAmount) {
                BlockVector2 current = queue.poll();
                int cx = current.getX(), cz = current.getZ();

                BlockVector2[] neighs = {
                        BlockVector2.at(cx + 1, cz),
                        BlockVector2.at(cx - 1, cz),
                        BlockVector2.at(cx, cz + 1),
                        BlockVector2.at(cx, cz - 1)
                };
                for (BlockVector2 nb : neighs) {
                    if (visited.contains(nb) || nb.equals(mascotVec)) continue;

                    if (loser.getChunks().contains(nb)) {
                        loser.removeChunk(nb.getX(), nb.getZ());
                        winner.addChunk(nb.getX(), nb.getZ());
                        visited.add(nb);
                        queue.add(nb);
                        transferred[0]++;
                        if (transferred[0] >= claimAmount) break;
                    }
                }
            }
        };

        if (!adjacentChunks.isEmpty()) {
            List<BlockVector2> toSteal = new ArrayList<>(adjacentChunks);
            int initialSteal = Math.min(toSteal.size(), claimAmount);
            Queue<BlockVector2> queue = new LinkedList<>();
            Set<BlockVector2> visited = new HashSet<>();
            for (int i = 0; i < initialSteal; i++) {
                BlockVector2 c = toSteal.get(i);
                loser.removeChunk(c.getX(), c.getZ());
                winner.addChunk(c.getX(), c.getZ());
                queue.add(c);
                visited.add(c);
                transferred[0]++;
            }
            bfsCapture.accept(queue, visited);
        } else {
            // Try from border
            List<BlockVector2> borderChunks = new ArrayList<>();
            for (BlockVector2 lChunk : loser.getChunks()) {
                if (lChunk.equals(mascotVec)) continue;
                int lx = lChunk.getX(), lz = lChunk.getZ();
                BlockVector2[] neighs = {
                        BlockVector2.at(lx + 1, lz),
                        BlockVector2.at(lx - 1, lz),
                        BlockVector2.at(lx, lz + 1),
                        BlockVector2.at(lx, lz - 1)
                };

                for (BlockVector2 nb : neighs) {
                    if (!loser.getChunks().contains(nb)) {
                        borderChunks.add(lChunk);
                        break;
                    }
                }
            }
            if (!borderChunks.isEmpty()) {
                Collections.shuffle(borderChunks);
                BlockVector2 seed = borderChunks.get(0);

                loser.removeChunk(seed.getX(), seed.getZ());
                winner.addChunk(seed.getX(), seed.getZ());
                Queue<BlockVector2> queue = new LinkedList<>();
                Set<BlockVector2> visited = new HashSet<>();
                queue.add(seed);
                visited.add(seed);
                transferred[0]++;

                bfsCapture.accept(queue, visited);
            }
        }

        return transferred[0];
    }

    public static String getFormattedPhase(War.WarPhase phase) {
        return switch (phase) {
            case PREPARATION -> "Préparation";
            case COMBAT -> "Combat";
            case ENDED -> "Fin";
        };
    }

    public static void addPendingDefense(WarPendingDefense defense) {
        pendingDefenses.put(defense.getDefender().getUUID(), defense);
    }

    public static WarPendingDefense getPendingDefenseFor(City city) {
        return pendingDefenses.get(city.getUUID());
    }

    public enum WinReason {
        MASCOT_DEATH,
        MASCOT_HP,
        KILLS,
        DRAW
    }
}
