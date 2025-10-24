package fr.openmc.core.features.hdv.commands;

import fr.openmc.core.features.hdv.HDVModule;
import fr.openmc.core.features.economy.EconomyManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HDVSellCommand {

    private final HDVModule module;
    private static final String PREFIX = "§8[§6OpenMC §8> §eHDV§8]§r ";

    public HDVSellCommand(HDVModule module) {
        this.module = module;
    }

    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(PREFIX + "§cUsage: /hdv vendre <prix>");
            return true;
        }

        if (!player.hasPermission("openmc.hdv.sell")) {
            player.sendMessage(PREFIX + "§cVous n'avez pas la permission de vendre des objets !");
            return true;
        }

        try {
            double price = Double.parseDouble(args[1]);

            if (price <= 0) {
                player.sendMessage(PREFIX + "§cLe prix doit être supérieur à 0 !");
                return true;
            }

            if (price > 1000000000) {
                player.sendMessage(PREFIX + "§cLe prix maximum est de 1 milliard !");
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(PREFIX + "§cVous devez tenir un objet dans votre main !");
                return true;
            }

            int maxListings = module.getMaxListingsPerPlayer();
            int currentListings = module.getPlayerListingsCount(player.getUniqueId());

            if (currentListings >= maxListings) {
                player.sendMessage(PREFIX + "§cVous avez atteint le nombre maximum d'objets en vente !");
                player.sendMessage(PREFIX + "§cMaximum: §e" + maxListings);
                return true;
            }

            module.sellItem(player, item.clone(), price);
            player.getInventory().setItemInMainHand(null);

            player.sendMessage(PREFIX + "§aVotre objet a été mis en vente pour " +
                    EconomyManager.getFormattedNumber(price) + " §a!");

        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§cPrix invalide ! Utilisez uniquement des nombres.");
            player.sendMessage(PREFIX + "§cExemple: §e/hdv vendre 100");
        }

        return true;
    }
}