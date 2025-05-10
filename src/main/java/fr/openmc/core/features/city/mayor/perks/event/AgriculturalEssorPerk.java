package fr.openmc.core.features.city.mayor.perks.event;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.MaterialUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
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

public class AgriculturalEssorPerk implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (MayorManager.getInstance().phaseMayor !=2) return;

        Player player = event.getPlayer();

        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (!PerkManager.hasPerk(city.getMayor(), 11)) return;

        if (!DynamicCooldownManager.isReady(city.getUUID(), "city:agricultural_essor")) {
            MessagesManager.sendMessage(player, Component.text("La réforme d'événement l'§eEssor Agricole §fest lancée et il reste plus que §c" + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUUID(), "city:agricultural_essor"))), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    void onTimeEnd(Chronometer.ChronometerEndEvent e) {
        if (MayorManager.getInstance().phaseMayor !=2) return;

        String chronometerGroup = e.getGroup();
        if (!chronometerGroup.equals("city:agricultural_essor")) return;

        City city = CityManager.getCity(e.getEntity().getUniqueId().toString());

        if (city == null) return;

        if (!PerkManager.hasPerk(city.getMayor(), 11)) return;

        for (UUID memberUUID : city.getMembers()) {
            Player player = Bukkit.getPlayer(memberUUID);

            if (player == null || !player.isOnline()) continue;

            MessagesManager.sendMessage(player, Component.text("La réforme d'événement l'§eEssor Agricole §fest terminée !"), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (MayorManager.getInstance().phaseMayor !=2) return;

        Player player = event.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (!PerkManager.hasPerk(city.getMayor(), 11)) return;

        if (DynamicCooldownManager.isReady(city.getUUID(), "city:agricultural_essor")) return;

        Block block = event.getBlock();

        if (!MaterialUtils.isCrop(block.getType())) return;

        event.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());

        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
            }
        }
    }

    // probleme avec item adder, il ne detecte pas quand on casse une CustomCrop
//    @EventHandler
//    public void onCustomCropBreak(CustomBlockBreakEvent event) {
//        if (MayorManager.getInstance().phaseMayor != 2) return;
//
//        Player player = event.getPlayer();
//        City city = CityManager.getPlayerCity(player.getUniqueId());
//
//        if (city == null) return;
//        if (!PerkManager.hasPerk(city.getMayor(), 11)) return;
//        if (!DynamicCooldownManager.isReady(city.getUUID(), "city:agricultural_essor")) return;
//
//        String namespace = event.getNamespacedID();
//        System.out.println(namespace);
//        if (!MaterialUtils.isCustomCrop(namespace)) return;
//
//        Block block = event.getBlock();
//        System.out.println("1");
//        CustomBlock customBlock = CustomBlock.getInstance(namespace);
//        if (customBlock == null) {
//            System.out.println("CustomBlock non trouvé pour : " + namespace);
//            return;
//        }
//        System.out.println(customBlock);
//        List<ItemStack> drops = customBlock.getLoot(true);
//        System.out.println(drops);
//        if (drops == null || drops.isEmpty()) {
//            System.out.println("Aucun drop défini pour " + namespace);
//            return;
//        }
//
//        for (ItemStack drop : drops) {
//            block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
//        }
//    }
}
