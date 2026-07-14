package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.tasks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class VampireAttackTask extends BukkitRunnable {
    private final VampireBoss boss;
    private final Random random = ThreadLocalRandom.current();

    public static final long MIN_ATTACK_DELAY = 20L * 10; // 10 sec
    public static final long MAX_ATTACK_DELAY = 20L * 17; // 17 sec

    public VampireAttackTask(VampireBoss boss) {
        this.boss = boss;
    }

    @Override
    public void run() {
        if (!boss.getMannequin().isValid()) {
            cancel();
            return;
        }

        boss.pickRandomAttack();

        long nextDelay = random.nextLong(
                MIN_ATTACK_DELAY,
                MAX_ATTACK_DELAY + 1
        );

        this.cancel();
        this.runTaskLater(OMCPlugin.getInstance(), nextDelay);
    }
}
