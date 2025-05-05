package fr.openmc.core.features.city.mayor.perks.event;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.MaterialUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.impl.CraftCrop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

public class AgriculturalEssorPerk implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (!DynamicCooldownManager.isReady(city.getUUID(), "city:agricultural_essor")) {
            MessagesManager.sendMessage(player, Component.text("La réforme d'événement l'§eEssor Agricole §fest lancée et il reste plus que §c" + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUUID(), "city:agricultural_essor"))), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    void onTimeEnd(Chronometer.ChronometerEndEvent e) {
        String chronometerGroup = e.getGroup();
        if (!chronometerGroup.equals("city:agricultural_essor")) return;

        City city = CityManager.getCity(e.getEntity().getUniqueId().toString());

        for (UUID memberUUID : city.getMembers()) {
            Player player = Bukkit.getPlayer(memberUUID);

            if (player == null || !player.isOnline()) continue;

            MessagesManager.sendMessage(player, Component.text("La réforme d'événement l'§eEssor Agricole §fest terminée !"), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (DynamicCooldownManager.isReady(city.getUUID(), "city:agricultural_essor")) return;

        Block block = event.getBlock();

        if (!MaterialUtils.isCrop(block.getType())) return;

        event.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        System.out.println(drops);
        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
            }
        }
    }
}
