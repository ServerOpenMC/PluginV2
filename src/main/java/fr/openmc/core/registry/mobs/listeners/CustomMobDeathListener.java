package fr.openmc.core.registry.mobs.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CustomMobDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        DamageSource source = event.getDamageSource();

        if (!CustomMobRegistry.isCustomMob(entity)) return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        if (!(source.getCausingEntity() instanceof Player player)) return;

        CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);
        if (customMob == null) return;

        customMob.onDeath(customMob, event);

        if (customMob.getLoots() == null) return;
        for (CustomLoot loot : customMob.getLoots()) {
            if (Math.random() >= loot.getChance()) continue;
            if (loot instanceof ItemLoot itemLoot) {
                for (ItemStack lootItem : itemLoot.getItems()) {
                    event.getDrops().add(lootItem);
                }
            } else {
                loot.run(player);
            }
        }
    }
}
