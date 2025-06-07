package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static fr.openmc.core.features.city.sub.war.WarManager.TIME_FIGHT;
import static fr.openmc.core.features.city.sub.war.WarManager.TIME_PREPARATION;

public class War {

    public enum WarPhase {PREPARATION, COMBAT, ENDED}

    @Getter
    private final City cityAttacker;
    @Getter
    private final City cityDefender;
    @Getter
    private final List<UUID> attackers;
    @Getter
    private final List<UUID> defenders;
    @Getter
    private WarPhase phase = WarPhase.PREPARATION;
    @Getter
    private long startTime;

    public War(City cityAttacker, City cityDefender, List<UUID> attackers, List<UUID> defenders) {
        this.cityAttacker = cityAttacker;
        this.cityDefender = cityDefender;
        this.attackers = attackers;
        this.defenders = defenders;

        startPreparation();
    }

    public void startPreparation() {
        this.startTime = System.currentTimeMillis();
        this.phase = WarPhase.PREPARATION;

        for (UUID uuid : attackers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player.isOnline()) {
                String message = String.format("""
                                §8§m                                                     §r
                                §7
                                §c§lGUERRE!§r §7La préparation de la guerre commence§7
                                §8§oPréparez vous pour le combat contre %s
                                §8§oVous avez §c§l%d minutes §8pour vous équiper.
                                §8§oVous serez en §4%d §8VS §4%d
                                §7
                                §8§m                                                     §r""",
                        cityDefender.getName(), TIME_PREPARATION, attackers.size(), defenders.size());

                player.sendMessage(Component.text(message));
            }
            ;
        }

        for (UUID uuid : defenders) {
            Player player = Bukkit.getPlayer(uuid);
            if (player.isOnline()) {
                String message = String.format("""
                                §8§m                                                     §r
                                §7
                                §c§lGUERRE!§r §7La préparation de la guerre commence§7
                                §8§oPréparez vous pour le combat contre %s
                                §8§oVous avez §c§l%d minutes §8pour vous équiper.
                                §8§oVous serez en §4%d §8VS §4%d
                                §7
                                §8§m                                                     §r""",
                        cityAttacker.getName(), TIME_PREPARATION, attackers.size(), defenders.size());

                player.sendMessage(Component.text(message));
            }
            ;
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::startCombat, (long) TIME_PREPARATION * 60 * 20);
    }

    public int getPreparationTimeRemaining() {
        if (phase != WarPhase.PREPARATION) return 0;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        return Math.max(0, TIME_PREPARATION * 60 - (int) elapsed);
    }

    public void startCombat() {
        this.phase = WarPhase.COMBAT;

        for (UUID uuid : attackers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player.isOnline()) {
                String message = String.format("""
                                §8§m                                                     §r
                                §7
                                §c§lGUERRE!§r §7Le comabt est imminent!§7
                                §8§oBattez vous contre §c%s!
                                §8§oVous avez §c§l%d minutes §8de combat.
                                §8§oSi vous tuez la mascotte de la ville adverse, vous remportez la guerre.
                                §7
                                §8§m                                                     §r""",
                        cityDefender.getName(), TIME_FIGHT);

                player.sendMessage(Component.text(message));
            }
            ;
        }

        for (UUID uuid : defenders) {
            Player player = Bukkit.getPlayer(uuid);
            if (player.isOnline()) {
                String message = String.format("""
                                §8§m                                                     §r
                                §7
                                §c§lGUERRE!§r §7Le comabt est imminent!§7
                                §8§oBattez vous contre §c%s!
                                §8§oVous avez §c§l%d minutes §8de combat.
                                §8§oSi vous tuez la mascotte de la ville adverse, vous remportez la guerre.
                                §7
                                §8§m                                                     §r""",
                        cityAttacker.getName(), TIME_FIGHT);

                player.sendMessage(Component.text(message));
            }
            ;
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::end, (long) TIME_FIGHT * 60 * 20);
    }

    public int getCombatTimeRemaining() {
        if (phase != WarPhase.COMBAT) return 0;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        return Math.max(0, TIME_FIGHT * 60 - (int) elapsed);
    }

    public void end() {
        this.phase = WarPhase.ENDED;

        WarManager.endWar(this);
    }
}
