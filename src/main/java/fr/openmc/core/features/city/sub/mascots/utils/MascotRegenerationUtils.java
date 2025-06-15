package fr.openmc.core.features.city.sub.mascots.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MascotRegenerationUtils {
    public static final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private static final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();

    public static void startRegenCooldown(Mascot mascots) {
        UUID mascotsUUID = mascots.getMascotUUID();
        if (cooldownTasks.containsKey(mascotsUUID)) {
            cooldownTasks.get(mascotsUUID).cancel();
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                mascotsRegeneration(mascots);
                cooldownTasks.remove(mascotsUUID);
            }
        };

        cooldownTasks.put(mascotsUUID, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 10 * 60 * 20L);
    }

    public static void mascotsRegeneration(Mascot mascot) {
        if (mascot == null) return;
        if (regenTasks.containsKey(mascot.getMascotUUID())) return;

        LivingEntity mob = (LivingEntity) mascot.getEntity();
        if (mob == null) {
            regenTasks.remove(mascot.getMascotUUID());
            return;
        }

        PersistentDataContainer data = mob.getPersistentDataContainer();
        if (!data.has(MascotsManager.mascotsKey, PersistentDataType.STRING)) return;

        if (!mascot.isAlive()) return;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (mascot == null) {
                    this.cancel();
                    return;
                }

                if (mascot.getCity().isInWar()) return;

                LivingEntity mascots = (LivingEntity) mascot.getEntity();
                if (mascots == null || mascots.isDead()) {
                    regenTasks.remove(mascot.getMascotUUID());
                    this.cancel();
                    return;
                }


                if (mascots.getHealth() >= mascots.getMaxHealth()) {

                    mascots.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                            mascot.getCity().getName(),
                            mascots.getHealth(),
                            mascots.getMaxHealth()
                    )));
                    regenTasks.remove(mascot.getMascotUUID());
                    this.cancel();
                    return;
                }

                double newHealth = Math.min(mascots.getHealth() + 1, mascots.getMaxHealth());
                mascots.setHealth(newHealth);
                mascots.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                        mascot.getCity().getName(),
                        mascots.getHealth(),
                        mascots.getMaxHealth()
                )));
            }
        };

        regenTasks.put(mascot.getMascotUUID(), task);
        task.runTaskTimer(OMCPlugin.getInstance(), 0L, 60L);
    }
}
