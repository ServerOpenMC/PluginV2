package fr.openmc.core.features.events.contents.christmas.listeners;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.inventory.ItemStack;

public class GingerbreadListener implements Listener {

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;
        if (!hasFullGingerbreadArmor(player)) return;

        player.getWorld().dropItemNaturally(player.getLocation(), OMCRegistry.CUSTOM_ITEMS.GINGERBREAD.getBest());
    }

    @EventHandler
    public void onCompleteRaid(RaidFinishEvent event) {
        for (Player player : event.getWinners()) {
            ItemUtils.giveItem(OMCPlayer.of(player), OMCRegistry.CUSTOM_ITEMS.GINGERBREAD.getBest(), 150);
        }
    }

    private boolean hasFullGingerbreadArmor(Player player) {
        ItemStack helmet = player.getEquipment().getHelmet();
        ItemStack chestplate = player.getEquipment().getChestplate();
        ItemStack leggings = player.getEquipment().getLeggings();
        ItemStack boots = player.getEquipment().getBoots();

        return ItemUtils.isSimilar(helmet, OMCRegistry.CUSTOM_ITEMS.GINGERBREAD_HELMET) &&
                ItemUtils.isSimilar(chestplate, OMCRegistry.CUSTOM_ITEMS.GINGERBREAD_CHESTPLATE) &&
                ItemUtils.isSimilar(leggings, OMCRegistry.CUSTOM_ITEMS.GINGERBREAD_LEGGINGS) &&
                ItemUtils.isSimilar(boots, OMCRegistry.CUSTOM_ITEMS.GINGERBREAD_BOOTS);
    }
}
