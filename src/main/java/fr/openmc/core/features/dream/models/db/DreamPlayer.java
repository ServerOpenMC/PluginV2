package fr.openmc.core.features.dream.models.db;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.features.dream.events.DreamTimeEndEvent;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.mecanism.cold.ColdManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class DreamPlayer {
    private final Player player;
    @Setter
    private ItemStack[] oldInventory;
    @Setter
    private Location oldLocation;
    @Setter
    private PlayerInventory dreamInventory;

    @Setter
    private int cold;
    private BukkitTask coldTask;

    private Long dreamTime;
    private BukkitTask timeTask;

    public DreamPlayer(Player player, ItemStack[] oldInv, Location oldLocation, PlayerInventory dreamInv) {
        this.player = player;
        this.oldInventory = oldInv;
        this.oldLocation = oldLocation;
        this.dreamInventory = dreamInv;

        DBDreamPlayer cacheData = DreamManager.getCacheDreamPlayer(player);

        this.dreamTime = cacheData == null ? DreamManager.BASE_DREAM_TIME : cacheData.getMaxDreamTime();

        scheduleTimeTask();
    }

    public void addTime(Long additionalTime) {
        this.dreamTime += additionalTime;
    }

    public void removeTime(Long removedTime) {
        if (dreamTime - removedTime <= 0L) {
            this.dreamTime = 0L;
            return;
        }

        this.dreamTime -= removedTime;
    }

    public long getMaxDreamTime() {
        DBDreamPlayer cacheData = DreamManager.getCacheDreamPlayer(player);

        if (cacheData == null) return DreamManager.BASE_DREAM_TIME;

        return cacheData.getMaxDreamTime();
    }

    public void cancelTimeTask() {
        if (timeTask != null) timeTask.cancel();
    }

    public void scheduleTimeTask() {
        this.timeTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            this.dreamTime -= 1;

            if (dreamTime <= 0) {
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                        Bukkit.getServer().getPluginManager().callEvent(new DreamTimeEndEvent(this.player))
                );
                this.cancelTimeTask();
                return;
            }

            DreamBossBar.update(player, (float) this.getDreamTime() / this.getMaxDreamTime());
        }, 0L, 20L);
    }

    public void cancelColdTask() {
        System.out.println("cancel");
        if (coldTask != null) {
            coldTask.cancel();
            cold = 0;
            ColdManager.applyColdEffects(player, cold);
            coldTask = null;
        }
    }

    public void scheduleColdTask() {
        final int[] tickCounter = {0};
        this.coldTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            tickCounter[0] += 20;
            boolean nearHeat = ColdManager.isNearHeatSource(player);
            boolean isInBaseCamp = DreamStructuresManager.isInsideStructure(player.getLocation(), DreamStructure.DreamType.BASE_CAMP);
            double resistance = ColdManager.calculateColdResistance(player);
            boolean inColdBiome = player.getLocation().getBlock().getBiome().equals(DreamBiome.GLACITE_GROTTO.getBiome());

            if (isInBaseCamp) {
                cold = Math.max(0, cold - 15);
            } else if (nearHeat) {
                if (tickCounter[0] % 40 == 0) {
                    cold = Math.max(0, cold - 1);
                }
            }
            if (!inColdBiome && tickCounter[0] % 40 == 0) {
                cold = Math.max(0, cold - 1);
            }

            if (!nearHeat && !isInBaseCamp && inColdBiome && tickCounter[0] % (60 + (int) (resistance * 10)) == 0) {
                cold = Math.min(100, cold + 1);
            }

            if (!inColdBiome && cold == 0) {
                cancelColdTask();
                return;
            }

            ColdManager.applyColdEffects(player, cold);
        }, 0L, 20L);
    }

    public DBDreamPlayer serialize() {
        return new DBDreamPlayer(this.player.getUniqueId(), this.getMaxDreamTime(), BukkitSerializer.playerInventoryToBase64(this.dreamInventory));
    }

    public DBPlayerSave serializeSave() {
        return new DBPlayerSave(this.player.getUniqueId(), BukkitSerializer.playerInventoryToBase64(oldInventory), oldLocation);
    }

    public void teleportToOldLocation() {
        if (oldLocation != null) {
            player.teleportAsync(oldLocation);
        }
    }
}
