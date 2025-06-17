package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CityUnclaimAction {
    private static final ItemStack ayweniteItemStack = CustomItemRegistry.getByName("omc_items:aywenite").getBest();

    public static int calculatePrice(int chunkCount) {
        return 5000 + ((chunkCount - 1) * 1000) / 3;
    }

    public static int calculateAywenite(int chunkCount) {
        return (chunkCount - 1) / 3;
    }

    public static void startUnclaim(Player sender, int chunkX, int chunkZ) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessage(sender, Component.text("Tu ne peux pas étendre ta ville ici"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasChunk(chunkX, chunkZ)) {
            MessagesManager.sendMessage(sender, Component.text("Vous devez avoir ce claim pour le unclaim!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        System.out.println("Unclaiming chunk at: " + chunkX + ", " + chunkZ);

        System.out.println("Unclaiming chunk at: " + city.getMascot().getChunk().getX() + ", " + city.getMascot().getChunk().getZ());

        if (city.getMascot().getChunk().getX() == chunkX && city.getMascot().getChunk().getZ() == chunkZ) {
            MessagesManager.sendMessage(sender, Component.text("Vous ne pouvez pas unclaim le claim de la mascotte"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = calculatePrice(city.getChunks().size());
        int ayweniteNb = calculateAywenite(city.getChunks().size());

        EconomyManager.addBalance(sender.getUniqueId(), price);
        ItemStack aywenite = ayweniteItemStack.clone();
        aywenite.setAmount(ayweniteNb);
        for (ItemStack item : ItemUtils.splitAmountIntoStack(aywenite)) {
            sender.dropItem(item);
        }

        city.removeChunk(chunkX, chunkZ);

        MessagesManager.sendMessage(sender, Component.text("Vous venez de rétrécir votre ville en supprimant ce claim"), Prefix.CITY, MessageType.SUCCESS, false);
    }
}
