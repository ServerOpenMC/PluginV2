package fr.openmc.core.features.corporation.commands;

import fr.openmc.core.features.corporation.manager.PlayerShopManager;
import fr.openmc.core.features.corporation.manager.ShopManager;
import fr.openmc.core.features.corporation.menu.ShopMenu;
import fr.openmc.core.features.corporation.menu.ShopSearchMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("shop")
@Description("Manage shops")
@CommandPermission("omc.commands.shop")
public class ShopCommand {
    
    @CommandPlaceholder
    public void onCommand(Player player) {
        if (!ShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        new ShopMenu(player).open();
    }

    @Subcommand("help")
    @Description("Explique comment marche un shop")
    @Cooldown(30)
    public void help(Player player) {
        MessagesManager.sendMessage(player, Component.text("""
            §6§lListe des commandes shop :
            
            §e▪ /shop create§7 - Crée un shop si vous regardez un tonneau
            §e▪ /shop sell <prix>§7 - Permet de mettre en vente l'item dans votre main
            §e▪ /shop unsell§7 - Permet de retirer de la vente l'item que vous tenez en main
            §e▪ /shop delete§7 - Permet de supprimer votre shop en le regardant
            §e▪ /shop search§7 - Permet de rechercher des shops par leur nom ou le nom du joueur
            """),
                Prefix.SHOP, MessageType.INFO, false);
    }

    @Subcommand("sell")
    @Description("Sell an item in a shop")
    public void sellItem(Player player, @Named("price") double price) {
    
    }

    @Subcommand("create")
    @Description("Create a shop")
    public void createShop(Player player) {
        if (ShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous avez déjà un shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        if (! EconomyManager.hasEnoughMoney(player.getUniqueId(), 500)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez d'argent pour créer un shop (500" + EconomyManager.getEconomyIcon() + " nécessaires)"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        
        PlayerShopManager.startCreatingShop(player);
    }

    @Subcommand("unsell")
    @Description("Unsell an item of a shop")
    public void unsellItem(Player player, @Named("item number") int itemIndex) {
    
    }

    @Subcommand("delete")
    @Description("Delete a shop")
    public void deleteShop(Player player) {
        if (!ShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        PlayerShopManager.deleteShop(player, false);
    }

    @Subcommand("search")
    @Description("Recherche un shop")
    public void searchShop(Player player){
        new ShopSearchMenu(player).open();
    }
}
