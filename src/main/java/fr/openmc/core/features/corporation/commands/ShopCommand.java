package fr.openmc.core.features.corporation.commands;

import fr.openmc.core.features.corporation.manager.PlayerShopManager;
import fr.openmc.core.features.corporation.menu.ShopMenu;
import fr.openmc.core.features.corporation.menu.ShopSearchMenu;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopItem;
import fr.openmc.core.features.corporation.shops.Supply;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Iterator;
import java.util.Map;

@Command("shop")
@Description("Manage shops")
@CommandPermission("omc.commands.shop")
public class ShopCommand {
    
    @CommandPlaceholder
    public void onCommand(Player player) {
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        new ShopMenu(player).open();
    }
    
    @Subcommand("manage")
    @Description("Manage a shop")
    public static void manageShop(Player player) {
        
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        ShopMenu shopMenu = new ShopMenu(player);
        shopMenu.open();
    }

    @Subcommand("help")
    @Description("Explique comment marche un shop")
    @Cooldown(30)
    public void help(Player player) {
        MessagesManager.sendMessage(player, Component.text("""
            §6§lListe des commandes entreprise :
            
            §e▪ /shop create§7 - Crée un shop si vous regarder un tonneau
            §e▪ /shop sell <prix>§7 - Permet de mettre en vente l'item dans votre main
            §e▪ /shop unsell§7 - Permet de retirer de la vente l'item que vous tenez en main
            §e▪ /shop delete§7 - Permet de supprimer votre shop en le regardant
            §e▪ /shop manage§7 - Permet de gérer sont shop a distance seulement si vous n'êtes pas dans une entreprise
            §e▪ /shop search§7 - Permet de rechercher des shops par leur nom ou le nom du joueur
            """),
                Prefix.ENTREPRISE, MessageType.INFO, false);
    }

    @Subcommand("sell")
    @Description("Sell an item in your shop")
    public void sellItem(Player player, @Named("price") double price) {
        if (price<=0){
            MessagesManager.sendMessage(player, Component.text("§cVeuillez mettre un prix supérieur à zéro !"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }

        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        Shop shop = PlayerShopManager.getPlayerShop(player.getUniqueId());
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            MessagesManager.sendMessage(player, Component.text("§cVous devez tenir un item dans votre main"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        boolean itemThere = shop.addItem(item, price, 1, null);
        if (itemThere) {
            MessagesManager.sendMessage(player, Component.text("§cCet item est déjà dans le shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§aL'item a bien été ajouté au shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
    }

    @Subcommand("create")
    @Description("Create a shop")
    public void createShop(Player player) {
        if (PlayerShopManager.hasShop(player.getUniqueId())) {
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
    @Description("Unsell an item in your shop")
    public void unsellItem(Player player, @Named("item number") int itemIndex) {
        
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }

        Shop shop = PlayerShopManager.getPlayerShop(player.getUniqueId());
        ShopItem item = shop.getItem(itemIndex - 1);

        if (item == null) {
            MessagesManager.sendMessage(player, Component.text("§cCet item n'est pas dans le shop"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }

        shop.removeItem(item);
        MessagesManager.sendMessage(player, Component.text("§aL'item a bien été retiré du shop !"), Prefix.SHOP, MessageType.SUCCESS, false);

        if (item.getAmount() > 0) {
            ItemStack toGive = item.getItem().clone();
            toGive.setAmount(item.getAmount());
            player.getInventory().addItem(toGive);
            MessagesManager.sendMessage(player, Component.text("§6Vous avez récupéré le stock restant de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
        }
    }

    @Subcommand("delete")
    @Description("Delete a shop")
    public void deleteShop(Player player) {
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
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

    private void recoverStock(Player owner, ShopItem stock, Shop shop){
        if (stock.getAmount() <= 0) {
            shop.removeItem(stock);
            MessagesManager.sendMessage(owner, Component.text("§aL'item a bien été retiré du shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
            owner.closeInventory();
            return;
        }

        int maxPlace = ItemUtils.getFreePlacesForItem(owner, stock.getItem());
        if (maxPlace <= 0) {
            MessagesManager.sendMessage(owner, Component.text("§cVous n'avez pas assez de place"), Prefix.SHOP, MessageType.WARNING, false);
            owner.closeInventory();
            return;
        }

        int toTake = Math.min(stock.getAmount(), maxPlace);

        ItemStack toGive = stock.getItem().clone();
        toGive.setAmount(toTake);
        owner.getInventory().addItem(toGive);
        stock.setAmount(stock.getAmount() - toTake);
        
        if (stock.getAmount() > 0) {
            MessagesManager.sendMessage(owner, Component.text("§6Vous avez récupéré §a" + toTake + "§6 dans le stock de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessage(owner, Component.text("§6Vous avez récupéré le stock restant de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
        }
        

        // Mise à jour des suppliers
        int toRemove = toTake;
        Iterator<Map.Entry<Long, Supply>> iterator = shop.getSuppliers().entrySet().iterator();
        while (iterator.hasNext() && toRemove > 0) {
            Map.Entry<Long, Supply> entry = iterator.next();
            Supply supply = entry.getValue();

            if (!supply.getItemId().equals(stock.getItemID())) continue;

            int supplyAmount = supply.getAmount();

            if (supplyAmount <= toRemove) {
                toRemove -= supplyAmount;
                iterator.remove();
            } else {
                supply.setAmount(supplyAmount - toRemove);
                break;
            }
        }
    }
}
