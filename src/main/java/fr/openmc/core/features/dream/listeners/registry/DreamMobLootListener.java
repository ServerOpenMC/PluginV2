package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLoot;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
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

        if (!DreamMobsRegistry.isDreamMob(entity)) return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        if (!(source.getCausingEntity() instanceof Player)) return;

        DreamMob dreamMob = DreamMobsRegistry.getFromEntity(entity);
        if (dreamMob == null) return;

        for (DreamLoot loot : dreamMob.getDreamLoots()) {
            if (Math.random() >= loot.chance()) return;

            int amount = loot.minAmount() + (int) (Math.random() * (loot.maxAmount() - loot.minAmount() + 1));
            ItemStack drop = loot.item().getBest().asQuantity(amount);
            entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
        }
    }
}
