package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.attacks;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.bat.AbstractVampireBat;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.registry.mobs.MobAttack;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VampireBatAttack implements MobAttack {

    private static final double TARGET_RADIUS = 25.0;

    private final VampireBoss boss;

    private final List<CustomMobEntry> BATS = new ArrayList<>();

    public VampireBatAttack(VampireBoss boss) {
        this.boss = boss;
    }

    @Override
    public void execute() {
        if (BATS.isEmpty()) {
            BATS.addAll(List.of(
                    OMCRegistry.CUSTOM_MOBS.EXPLOSIVE_VAMPIRE_BAT,
                    OMCRegistry.CUSTOM_MOBS.POISON_VAMPIRE_BAT,
                    OMCRegistry.CUSTOM_MOBS.LEVITATION_VAMPIRE_BAT
            ));
        }

        Mannequin mannequin = boss.getMannequin();

        if (mannequin == null || !mannequin.isValid() || mannequin.isDead()) return;

        List<Player> targets = mannequin.getWorld()
                .getNearbyPlayers(
                        mannequin.getLocation(),
                        TARGET_RADIUS
                )
                .stream()
                .filter(this::canTarget)
                .toList();

        if (targets.isEmpty()) return;

        int amount = ThreadLocalRandom.current().nextInt(3, 7);

        mannequin.getWorld().playSound(
                mannequin.getLocation(),
                Sound.ENTITY_BAT_TAKEOFF,
                SoundCategory.HOSTILE,
                3.0F,
                0.5F
        );

        for (int i = 0; i < amount; i++) {
            Player target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));

            Location spawnLocation = getRandomSpawnLocation(mannequin);
            CustomMobEntry batType = BATS.get(ThreadLocalRandom.current().nextInt(BATS.size()));

            if (!(batType.getMob() instanceof AbstractVampireBat batVampire)) return;
            batVampire.spawn(spawnLocation, target);
        }
    }

    private Location getRandomSpawnLocation(Mannequin mannequin) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        double angle = random.nextDouble(0, Math.PI * 2);
        double radius = random.nextDouble(1.0, 3.0);

        return mannequin.getLocation()
                .clone()
                .add(
                        Math.cos(angle) * radius,
                        mannequin.getHeight() * 0.5,
                        Math.sin(angle) * radius
                );
    }

    private boolean canTarget(Player player) {
        return player.isValid()
                && !player.isDead()
                && player.getGameMode() != GameMode.CREATIVE
                && player.getGameMode() != GameMode.SPECTATOR;
    }
}
