package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks;

import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class VampireAttackTask extends BukkitRunnable {
    private final VampireBoss boss;

    private static final int MIN_ATTACK_DELAY_SECONDS = 8;
    private static final int MAX_ATTACK_DELAY_SECONDS = 16;

    public VampireAttackTask(VampireBoss boss) {
        this.boss = boss;
    }

    private int cooldown = RandomUtils.randomBetween(MIN_ATTACK_DELAY_SECONDS, MAX_ATTACK_DELAY_SECONDS);

    @Override
    public void run() {
        if (!boss.getMannequin().isValid()) {
            cancel();
            return;
        }
        cooldown--;
        if (cooldown <= 0) {
            boss.pickRandomAttack();
            cooldown = RandomUtils.randomBetween(MIN_ATTACK_DELAY_SECONDS, MAX_ATTACK_DELAY_SECONDS);
        }
    }
}
