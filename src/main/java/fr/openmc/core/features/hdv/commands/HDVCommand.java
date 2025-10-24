package fr.openmc.core.features.hdv.commands;

import fr.openmc.core.features.hdv.HDVModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HDVCommand implements CommandExecutor {

    private final HDVModule module;
    private static final String PREFIX = "§8[§6OpenMC §8> §eHDV§8]§r ";

    public HDVCommand(HDVModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être exécutée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            module.openMainMenu(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "vendre":
            case "sell":
                return new HDVSellCommand(module).execute(player, args);

            case "retirer":
            case "remove":
                module.openMyListingsMenu(player);
                return true;

            case "help":
            case "aide":
                sendHelp(player);
                return true;

            default:
                player.sendMessage(PREFIX + "§cCommande inconnue ! Utilisez §e/hdv help");
                return true;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8§m                                                ");
        player.sendMessage("§6§lHDV - Hôtel de Ville");
        player.sendMessage("");
        player.sendMessage("§e/hdv §7- Ouvre le menu principal");
        player.sendMessage("§e/hdv vendre <prix> §7- Met en vente l'objet en main");
        player.sendMessage("§e/hdv retirer §7- Gère vos objets en vente");
        player.sendMessage("§e/hdv help §7- Affiche cette aide");
        player.sendMessage("§8§m                                                ");
    }
}