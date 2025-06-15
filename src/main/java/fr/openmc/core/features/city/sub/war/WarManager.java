package fr.openmc.core.features.city.sub.war;

import com.sk89q.worldedit.math.BlockVector2;
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
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WarManager {

    public static int TIME_PREPARATION = 10; // in minutes
    public static int TIME_FIGHT = 30; // in minutes

    public static long CITY_LOSER_IMMUNITY_FIGHT_COOLDOWN = 2 * 24 * 60 * 60 * 1000L; // 2 jours en millisecondes
    public static long CITY_WINNER_IMMUNITY_FIGHT_COOLDOWN = 24 * 60 * 60 * 1000L; // 1 jours en millisecondes

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

        int baseClaims = -1;
        double amountStolen = -1;
        int powerChange = -1;
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

            int claimsWon = (int) Math.ceil(totalClaims * percent * (1 + (level / 10.0)));

            transferChunksAfterWar(winner, loser, claimsWon);
        }


        broadcastWarResult(war, winner, loser, winReason, powerChange, Math.abs(amountStolen), baseClaims);
    }

    public static void broadcastWarResult(War war, City winner, City loser, WinReason reason, int powerChange, double amountStolen, int claimNumber) {
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
                    §8§m                                                     §r""", winner.getName(), killsWinner, loser.getName(), killsLoser);


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
                "+ " + amountStolen + EconomyManager.getEconomyIcon() + " volés à l'adversaire",
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
                "- " + amountStolen + EconomyManager.getEconomyIcon() + " perdu",
                "- " + claimNumber + " territoire(s) perdus"
        );

        for (UUID uuid : loser.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.isOnline()) player.sendMessage(Component.text(loserMessage));
        }
    }

    public static void transferChunksAfterWar(City winner, City loser, int claimAmount) {
        Chunk mascotChunk = loser.getMascot().getChunk();
        BlockVector2 mascotVec = BlockVector2.at(mascotChunk.getX(), mascotChunk.getZ());

        Set<BlockVector2> losableChunks = loser.getChunks().stream()
                .filter(chunk -> !chunk.equals(mascotVec))
                .collect(Collectors.toSet());

        if (losableChunks.isEmpty()) return;

        Set<BlockVector2> winnerChunks = winner.getChunks();
        Set<BlockVector2> neighborChunks = new HashSet<>();

        for (BlockVector2 winnerChunk : winnerChunks) {
            for (BlockFace face : BlockFace.values()) {
                BlockVector2 adjacent = winnerChunk.add(face.getModX(), face.getModZ());
                if (losableChunks.contains(adjacent)) {
                    neighborChunks.add(adjacent);
                }
            }
        }

        List<BlockVector2> claimsToTake = new ArrayList<>();

        if (!neighborChunks.isEmpty()) {
            List<BlockVector2> neighbors = new ArrayList<>(neighborChunks);
            Collections.shuffle(neighbors);

            for (BlockVector2 vec : neighbors) {
                if (claimsToTake.size() >= claimAmount) break;
                claimsToTake.add(vec);
            }
        }

        if (claimsToTake.size() < claimAmount) {
            List<BlockVector2> randomChunks = new ArrayList<>(losableChunks);
            randomChunks.removeAll(claimsToTake);
            Collections.shuffle(randomChunks);

            for (BlockVector2 vec : randomChunks) {
                if (claimsToTake.size() >= claimAmount) break;
                claimsToTake.add(vec);
            }
        }

        for (BlockVector2 vec : claimsToTake) {
            boolean removed = loser.removeChunk(vec.getBlockX(), vec.getBlockZ());
            if (removed) {
                Chunk chunk = Bukkit.getWorld("world").getChunkAt(vec.getBlockX(), vec.getBlockZ());
                winner.addChunk(chunk);
            }
        }

        Bukkit.getLogger().info(winner.getName() + " a volé " + claimsToTake.size() + " chunks à " + loser.getName());
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
