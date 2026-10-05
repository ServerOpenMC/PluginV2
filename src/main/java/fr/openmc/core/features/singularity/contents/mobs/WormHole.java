package fr.openmc.core.features.singularity.contents.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.singularity.sub.wormhole.WormManager;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.minecraft.world.level.block.BeaconBeamBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;
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

        wormHole.setItemStack(DreamItemRegistry.SINGULARITY.getBest());
        wormHole.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);

        new AnimationTask(wormHole)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 10L);

        return wormHole;
    }

    private static class AnimationTask extends BukkitRunnable {
        private final ItemDisplay itemDisplay;
        final float[] angle = {0f};

        public AnimationTask(ItemDisplay itemDisplay) {
            this.itemDisplay = itemDisplay;
        }

        @Override
        public void run() {
            if (!itemDisplay.isValid()) {
                cancel();
                return;
            }

            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);

            angle[0] += 90f;
            itemDisplay.setInterpolationDelay(0);
            itemDisplay.setInterpolationDuration(10);

            itemDisplay.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f((float) Math.toRadians(angle[0]), 1, 1, 1),
                    new Vector3f(10f, 10f, 10f),
                    new AxisAngle4f()
            ));
        }
    }
}

