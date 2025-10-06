package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.LocationUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static fr.openmc.core.features.city.sub.war.WarManager.TIME_FIGHT;
import static fr.openmc.core.features.city.sub.war.WarManager.TIME_PREPARATION;

@Getter
public class War {

    public enum WarPhase {PREPARATION, COMBAT, ENDED}

    private final City cityAttacker;
    private final City cityDefender;
    private final List<UUID> attackers;
    private final List<UUID> defenders;
    @Setter
    private WarPhase phase = WarPhase.PREPARATION;

    private int attackersKill;
    private int defendersKill;

    private long startTime;

    /**
     * Creates a new war between two cities.
     *
     * @param cityAttacker The city that is attacking.
     * @param cityDefender The city that is defending.
     * @param attackers    The list of UUIDs of the players in the attacking city.
     * @param defenders    The list of UUIDs of the players in the defending city.
     */
    public War(City cityAttacker, City cityDefender, List<UUID> attackers, List<UUID> defenders) {
        this.cityAttacker = cityAttacker;
        this.cityDefender = cityDefender;
        this.attackers = attackers;
        this.defenders = defenders;

        this.attackersKill = 0;
        this.defendersKill = 0;

        startPreparation();
    }

    /**
     * Starts the preparation phase of the war.
     * This method sends messages to both teams and schedules the start of the combat phase.
     */
    public void startPreparation() {
        this.startTime = System.currentTimeMillis();
        this.phase = WarPhase.PREPARATION;

        String message = String.format("""
                        §8§m                                                     §r
                        §7
                        §c§lGUERRE!§r §7La préparation de la guerre commence§7
                        §8§oPréparez vous pour le combat contre %s
                        §8§oVous avez §c§l%d minutes §8pour vous équiper.
                        §8§oVous serez en §4%d §8VS §4%d
                        §8§oLorsque le combat commencera vous serez §4téléporter §8a votre §cMascotte.
                        §7
                        §8§m                                                     §r""",
                cityAttacker.getName(), TIME_PREPARATION, attackers.size(), defenders.size());

        for (UUID uuid : attackers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (!player.isOnline()) continue;

            player.sendMessage(Component.text(message));
        }

        for (UUID uuid : defenders) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (!player.isOnline()) continue;

            player.sendMessage(Component.text(message));
        }

        LivingEntity mascotAttacker = (LivingEntity) cityAttacker.getMascot().getEntity();
        mascotAttacker.clearActivePotionEffects();

        LivingEntity mascotDefender = (LivingEntity) cityDefender.getMascot().getEntity();
        mascotDefender.clearActivePotionEffects();

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::startCombat, (long) TIME_PREPARATION * 60 * 20);
    }

    /**
     * Gets the remaining time in seconds for the preparation phase.
     *
     * @return The remaining time in seconds, or 0 if not in preparation phase.
     */
    public int getPreparationTimeRemaining() {
        if (phase != WarPhase.PREPARATION) return 0;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        return Math.max(0, TIME_PREPARATION * 60 - (int) elapsed);
    }

    /**
     * Starts the combat phase of the war.
     * This method sends messages to both teams and schedules the end of the war.
     */
    public void startCombat() {
        this.phase = WarPhase.COMBAT;

        String message = """
                §8§m                                                     §r
                §7
                §c§lGUERRE!§r §7Le comabat est imminent!§7
                §8§oBattez vous contre §c%s!
                §8§oVous avez §c§l%d minutes §8§ode combat.
                §8§oSi vous tuez la mascotte de la ville adverse, vous remportez la guerre.
                §7
                §8§m                                                     §r""";

        Location mascotLocDefender = cityDefender.getMascot().getEntity().getLocation();
        Location mascotLocAttacker = cityAttacker.getMascot().getEntity().getLocation();
        for (UUID uuid : attackers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.isOnline()) {
                player.sendMessage(Component.text(String.format(message, cityDefender.getName(), TIME_FIGHT)));
                player.teleportAsync(LocationUtils.getSafeNearbySurface(mascotLocAttacker,3));
            }
        }

        for (UUID uuid : defenders) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.isOnline()) {
                player.sendMessage(Component.text(String.format(message, cityAttacker.getName(), TIME_FIGHT)));
                player.teleportAsync(LocationUtils.getSafeNearbySurface(mascotLocDefender,3));
            }
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::end, (long) TIME_FIGHT * 60 * 20);
    }

    /**
     * Gets the remaining time in seconds for the combat phase.
     *
     * @return The remaining time in seconds, or 0 if not in combat phase.
     */
    public int getCombatTimeRemaining() {
        if (phase != WarPhase.COMBAT) return 0;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        return Math.max(0, TIME_FIGHT * 60 - (int) elapsed);
    }

    /**
     * Ends the war and notifies the WarManager.
     * This method sets the phase to END and calls the endWar method in WarManager.
     */
    public void end() {
        this.phase = WarPhase.ENDED;

        WarManager.endWar(this);
    }

    /**
     * Checks if a player is a participant in the war.
     *
     * @param uuid The UUID of the player.
     * @return true if the player is an attacker or defender, false otherwise.
     */
    public boolean isParticipant(UUID uuid) {
        return attackers.contains(uuid) || defenders.contains(uuid);
    }

    /**
     * Checks if a player is an attacker in the war.
     *
     * @param uuid The UUID of the player.
     * @return true if the player is an attacker, false otherwise.
     */
    public boolean isAttacker(UUID uuid) {
        return attackers.contains(uuid);
    }

    /**
     * Checks if a player is a defender in the war.
     *
     * @param uuid The UUID of the player.
     * @return true if the player is a defender, false otherwise.
     */
    public boolean isDefender(UUID uuid) {
        return defenders.contains(uuid);
    }

    /**
     * Increments the number of kills for the attackers.
     */
    public void incrementAttackerKills() {
        attackersKill++;
    }

    /**
     * Increments the number of kills for the defenders.
     */
    public void incrementDefenderKills() {
        defendersKill++;
    }
}
