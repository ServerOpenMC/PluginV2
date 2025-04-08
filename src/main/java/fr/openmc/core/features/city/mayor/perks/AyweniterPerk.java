package fr.openmc.core.features.city.mayor.perks;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class AyweniterPerk implements Listener {

    private static final double DROP_CHANCE = 0.01; //1%
    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (MayorManager.getInstance().phaseMayor==2) {
            if (!PerkManager.hasPerk(playerCity.getMayor(), 8)) return;

            if (block.getType() == Material.STONE) {

                if (random.nextDouble() < DROP_CHANCE) {
                    ItemStack ayweniteItem = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
                    ayweniteItem.setAmount(2);
                    player.getInventory().addItem(ayweniteItem);
                    player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 10.0F, 0.6F);
                    MessagesManager.sendMessage(player, Component.text("§8§o*la bénédiction!*"), Prefix.MAYOR, MessageType.INFO, false);
                }
            }
        }
    }
}
