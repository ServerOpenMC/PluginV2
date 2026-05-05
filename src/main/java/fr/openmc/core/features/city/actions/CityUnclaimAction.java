package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.World;
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
        World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.claim.cant_claim_here"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasChunk(chunkX, chunkZ)) {
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.unclaim.must_own_claim"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getMascot().getChunk().getX() == chunkX && city.getMascot().getChunk().getZ() == chunkZ) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.unclaim.cant_unclaim_mascot_claim"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        // si on unclaim des claims gratuits on ne rend rien, sinon on rend une partie de l'argent et d'aywenite
        if (city.getChunks().size() > CityCreateAction.FREE_CLAIMS+1) {
            int price = calculatePrice(city.getChunks().size());
            int ayweniteNb = calculateAywenite(city.getChunks().size());

            EconomyManager.addBalance(sender.getUniqueId(), price, "Unclaim de chunk de ville");
            ItemStack aywenite = ayweniteItemStack.clone();
            aywenite.setAmount(ayweniteNb);
            for (ItemStack item : ItemUtils.splitAmountIntoStack(aywenite)) {
                sender.dropItem(item);
            }
        }

        city.removeChunk(chunkX, chunkZ);

        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.unclaim.success"), Prefix.CITY, MessageType.SUCCESS, false);
    }
}
