package fr.openmc.core.features.city.sub.mayor.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static fr.openmc.core.features.city.sub.mayor.menu.MayorLawMenu.COOLDOWN_TIME_WARP;

public class MayorSetWarpAction {
    public static void setWarp(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        Mayor mayor = city.getMayor();

        if (mayor == null) return;

        if (!player.getUniqueId().equals(mayor.getMayorUUID()) && !city.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
	        MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas le maire de la ville"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "mayor:law-move-warp")) {
            return;
        }
        CityLaw law = city.getLaw();

        ItemInteraction.runLocationInteraction(
                player,
                getWarpWand(),
                "mayor:wait-set-warp",
                300,
		        "§7Vous avez 300s pour sélectionner votre point de spawn",
		        "§7Vous n'avez pas eu le temps de poser votre warp",
                locationClick -> {
                    if (locationClick == null) return true;
                    Chunk chunk = locationClick.getChunk();

                    if (!city.hasChunk(chunk.getX(), chunk.getZ())) {
	                    MessagesManager.sendMessage(player, Component.text("§cImpossible de mettre le warp ici car ce n'est pas dans votre ville"), Prefix.CITY, MessageType.ERROR, false);
                        return false;
                    }

                    DynamicCooldownManager.use(city.getUniqueId(), "mayor:law-move-warp", COOLDOWN_TIME_WARP);
                    law.setWarp(locationClick);
                    MessagesManager.sendMessage(player, Component.text("Vous venez de mettre le §9warp de votre ville §fen : \n §8- §fx=§6" + locationClick.x() + "\n §8- §fy=§6" + locationClick.y() + "\n §8- §fz=§6" + locationClick.z()), Prefix.CITY, MessageType.SUCCESS, false);
                    return true;
                },
                null
        );
    }

    public static ItemStack getWarpWand() {
        List<Component> loreItemInterraction = List.of(
		        Component.text("§7Cliquez sur l'endroit où vous voulez mettre le §9warp")
        );
        ItemStack item = CustomItemRegistry.getByName("omc_items:warp_stick").getBest();
        ItemMeta itemMeta = item.getItemMeta();
	    
	    itemMeta.displayName(Component.text("§7Séléction du §9warp"));
        itemMeta.lore(loreItemInterraction);
        item.setItemMeta(itemMeta);
        return item;
    }
}
