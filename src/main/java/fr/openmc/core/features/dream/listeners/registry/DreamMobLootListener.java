package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
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

        if (dreamMob.getLoots() == null) return;

        for (CustomLoot loot : dreamMob.getLoots()) {
            if (Math.random() >= loot.getChance()) return;

            int amount = loot.getMinAmount() + (int) (Math.random() * (loot.getMaxAmount() - loot.getMinAmount() + 1));
            ItemStack drop = loot.getItem().asQuantity(amount);
            entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
        }
    }
}
