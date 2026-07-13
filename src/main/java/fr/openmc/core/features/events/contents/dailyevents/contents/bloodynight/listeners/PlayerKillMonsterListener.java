package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerKillMonsterListener implements Listener {

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof BloodyNightEvent)) return;
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity() instanceof Monster)) return;
        if (!(event.getDamageSource().getDirectEntity() instanceof Player player)) return;

        event.getDrops().clear();

        List<CustomLoot> loots = OMCRegistry.CUSTOM_LOOT_TABLES.BLOODY_MOB.rollLoots(player);

        event.getDrops().clear();
        for (CustomLoot loot : loots) {
            if (!(loot instanceof ItemLoot itemLoot)) continue;

            for (ItemStack item : itemLoot.getItems()) {
                event.getDrops().add(item);
            }
        }
    }
}
