package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.listeners;

import org.bukkit.event.Listener;

public class PlayerKillMonsterListener implements Listener {

//    @EventHandler
//    public void onPlayerKill(EntityDeathEvent event) {
//        if (!DailyEventsManager.isActiveDailyEvent()
//                || !(DailyEventsManager.getActiveDailyEvent() instanceof BloodyNightEvent)) return;
//        if (event.getEntity().getKiller() == null) return;
//        if (!(event.getEntity() instanceof Monster)) return;
//        if (!(event.getDamageSource().getDirectEntity() instanceof Player player)) return;
//
//        event.getDrops().clear();
//
//        List<CustomLoot> loots = OMCRegistry.CUSTOM_LOOT_TABLES.CORRUPTED_MOB.rollLoots(player);
//
//        event.getDrops().clear();
//        for (CustomLoot loot : loots) {
//            if (!(loot instanceof ItemLoot itemLoot)) continue;
//
//            for (ItemStack item : itemLoot.getItems()) {
//                event.getDrops().add(item);
//            }
//        }
//    }
}
