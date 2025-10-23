package fr.openmc.core.features.dream.mobs.listeners;

import fr.openmc.core.features.dream.mobs.DreamLoot;
import fr.openmc.core.features.dream.mobs.DreamMob;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DreamMobLootListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        DamageSource source = event.getDamageSource();
        if (!(source.getCausingEntity() instanceof Player)) return;

        if (!DreamMobManager.isDreamMob(entity)) return;

        DreamMob dreamMob = DreamMobManager.getFromEntity(entity);
        if (dreamMob == null) return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        for (DreamLoot loot : dreamMob.getDreamLoots()) {
            if (Math.random() < loot.chance()) {
                int amount = loot.minAmount() + (int) (Math.random() * (loot.maxAmount() - loot.minAmount() + 1));
                ItemStack drop = loot.item().getBest().asQuantity(amount);
                entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
            }
        }
    }
}
