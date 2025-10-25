package fr.openmc.core.features.hdv.commands;

import fr.openmc.core.features.hdv.HDVModule;
import fr.openmc.core.features.hdv.menu.HDVMainMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.annotation.CommandPlaceholder;

@SuppressWarnings({"unused", "WeakerAccess"})
@Command({"hdv"})
public class HDVCommand {
    private static final String PREFIX = "§6[§eOpenMC-HDV§6] §r";
    private final HDVModule hdvModule = HDVModule.getInstance();

    @CommandPlaceholder()
    public void openMenu(Player player) {
        new HDVMainMenu(player).open();
    }

    @Subcommand("sell")
    @CommandPermission("openmc.hdv.sell")
    public void sell(Player player, String prix) {
        if (player == null) return;
        var itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType().isAir()) {
            player.sendMessage(PREFIX + "§cVous devez tenir un objet en main!");
            return;
        }
        try {
            double price = Double.parseDouble(prix);
            hdvModule.addListing(player, itemInHand, price);
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§cLe prix doit être un nombre valide!");
        }
    }

    @Subcommand("help")
    public void help(Player player) {
        player.sendMessage(PREFIX + "§6=== Aide HDV ===");
        player.sendMessage("§e/hdv §7- Ouvre le menu du HDV");
        player.sendMessage("§e/hdv sell <prix> §7- Met en vente l'objet en main");
        player.sendMessage("§e/hdv help §7- Affiche ce message d'aide");
    }
}
