package fr.openmc.core.features.singularity.contents.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.singularity.sub.wormhole.WormHoleManager;
import fr.openmc.core.features.singularity.sub.wormhole.models.WormHoleStage;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class WormHole extends CustomMob<ItemDisplay> {
    public WormHole(String id) {
        super(id,
                TranslationManager.translation("feature.singularity.mobs.worm_hole"),
                ItemDisplay.class,
                1,
                1
        );
    }

    @Override
    public ItemDisplay spawn(Location spawnLocation, Consumer<ItemDisplay> consumer) {
        ItemDisplay wormHole = spawnLocation.getWorld().spawn(
                spawnLocation.add(0, 1, 0),
                ItemDisplay.class,
                consumer,
                CreatureSpawnEvent.SpawnReason.CUSTOM
        );

        registerAsCustomMob(wormHole);

        wormHole.setItemStack(DreamItemRegistry.SINGULARITY.getBest());
        wormHole.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);

        new AnimationTask(wormHole)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 10L);

        new LightningTask(wormHole)
                .runTaskTimer(OMCPlugin.getInstance(), 20L, 10L);

        return wormHole;
    }

    private static class LightningTask extends BukkitRunnable {
        private static final double RADIUS_PER_SCALE = 0.6;
        private static final double CHANCE = 0.35;

        private final ItemDisplay itemDisplay;

        public LightningTask(ItemDisplay itemDisplay) {
            this.itemDisplay = itemDisplay;
        }

        @Override
        public void run() {
            WormHoleStage stage = WormHoleManager.getStage(itemDisplay);
            if (!itemDisplay.isValid() || !stage.isThunderOn()) {
                cancel();
                return;
            }

            if (ThreadLocalRandom.current().nextDouble() > CHANCE) return;

            double radius = stage.getScale() * RADIUS_PER_SCALE;

            Location center = itemDisplay.getLocation();

            double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;
            double distance = radius * (0.5 + ThreadLocalRandom.current().nextDouble() * 0.5);

            Location strike = center.clone().add(
                    Math.cos(angle) * distance,
                    ThreadLocalRandom.current().nextDouble(-1, 1),
                    Math.sin(angle) * distance
            );

            center.getWorld().strikeLightningEffect(strike);
        }
    }

    private static class AnimationTask extends BukkitRunnable {
        private final ItemDisplay itemDisplay;
        final float[] angle = {0f};

        public AnimationTask(ItemDisplay itemDisplay) {
            this.itemDisplay = itemDisplay;
        }

        @Override
        public void run() {
            WormHoleStage stage = WormHoleManager.getStage(itemDisplay);

            if (!itemDisplay.isValid()) {
                cancel();
                return;
            }

            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);

            angle[0] += 90f;
            itemDisplay.setInterpolationDelay(0);
            itemDisplay.setInterpolationDuration(stage.getFlipDuration());

            float scale = stage.getScale();
            itemDisplay.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f((float) Math.toRadians(angle[0]), 1, 0, 1),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f()
            ));
        }
    }
}

