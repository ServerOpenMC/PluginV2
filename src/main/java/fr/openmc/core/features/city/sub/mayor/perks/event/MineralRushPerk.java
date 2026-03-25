package fr.openmc.core.features.city.sub.mayor.perks.event;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.MaterialUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

public class MineralRushPerk implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (MayorManager.phaseMayor !=2) return;

        Player player = event.getPlayer();

        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (!PerkManager.hasPerk(city.getMayor(), Perks.MINERAL_RUSH.getId())) return;

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:mineral_rush")) {
            MessagesManager.sendMessage(player, Component.text("La réforme d'événement la §eRuée Minière §fest lancée et il reste plus que §c" + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:mineral_rush"))), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    void onTimeEnd(Chronometer.ChronometerEndEvent e) {
        if (MayorManager.phaseMayor !=2) return;

        String chronometerGroup = e.getGroup();
        if (!chronometerGroup.equals("city:mineral_rush")) return;

        City city = CityManager.getCity(e.getEntity().getUniqueId());

        if (city == null) return;

        if (!PerkManager.hasPerk(city.getMayor(), Perks.MINERAL_RUSH.getId())) return;

        for (UUID memberUUID : city.getMembers()) {
            Player player = Bukkit.getPlayer(memberUUID);

            if (player == null || !player.isOnline()) continue;
	        
	        MessagesManager.sendMessage(player, Component.text("La réforme d'évènement la §eRuée Minière §fest terminée !"), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    public void onMineralBreak(BlockBreakEvent event) {
        if (MayorManager.phaseMayor !=2) return;

        Player player = event.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (city.getMayor() == null) return;

        if (!PerkManager.hasPerk(city.getMayor(), Perks.MINERAL_RUSH.getId())) return;

        if (DynamicCooldownManager.isReady(city.getUniqueId(), "city:mineral_rush")) return;

        Block block = event.getBlock();

        if (!MaterialUtils.isOre(block.getType())) return;

        event.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());

        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
            }
        }
    }

    @EventHandler
    public void onAyweniteBreak(CustomBlockBreakEvent event) {
        if (MayorManager.phaseMayor != 2) return;

        Player player = event.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;
        if (!PerkManager.hasPerk(city.getMayor(), Perks.MINERAL_RUSH.getId())) return;
        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:mineral_rush")) return;

        String namespace = event.getNamespacedID();
        if (!namespace.equals("omc_blocks:aywenite_ore") && !namespace.equals("omc_blocks:deepslate_aywenite_ore")) return;

        Block block = event.getBlock();

        block.getWorld().dropItemNaturally(block.getLocation(), CustomItemRegistry.getByName("omc_items:aywenite").getBest());
    }
}
