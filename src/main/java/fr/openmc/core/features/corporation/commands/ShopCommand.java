package fr.openmc.core.features.corporation.commands;


import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.PlayerShopManager;
import fr.openmc.core.features.corporation.menu.company.ShopManageMenu;
import fr.openmc.core.features.corporation.menu.shop.ShopMenu;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.menu.shop.ShopSearchMenu;
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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Command({"shop", "shops"})
@Description("Manage shops")
@CommandPermission("omc.commands.shop")
public class ShopCommand {

    @DefaultFor("~")
    public void onCommand(Player player) {
        boolean isInCompany = CompanyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            ShopManageMenu shopManageMenu = new ShopManageMenu(player, CompanyManager.getCompany(player.getUniqueId()));
            shopManageMenu.open();
        }
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

    @Subcommand("create")
    @Description("Create a shop")
    public void createShop(Player player) {
        boolean isInCompany = CompanyManager.isInCompany(player.getUniqueId());
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() != Material.BARREL) {
            MessagesManager.sendMessage(player, Component.text("§cVous devez regarder un tonneau pour créer un shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        Block aboveBlock = Objects.requireNonNull(targetBlock.getLocation().getWorld()).getBlockAt(targetBlock.getLocation().clone().add(0, 1, 0));
        if (aboveBlock.getType() != Material.AIR) {
            MessagesManager.sendMessage(player, Component.text("§cVous devez liberer de l'espace au dessus de votre tonneau pour créer un shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        if (isInCompany) {
            Company company = CompanyManager.getCompany(player.getUniqueId());
            if (!company.hasPermission(player.getUniqueId(), CorpPermission.CREATESHOP)) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission pour créer un shop dans l'entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            if (!company.createShop(player.getUniqueId(), targetBlock, aboveBlock)) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez d'argent dans la banque de votre entreprise pour créer un shop (100" + EconomyManager.getEconomyIcon() + ")"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            MessagesManager.sendMessage(player, Component.text("§6[Shop] §c -100" + EconomyManager.getEconomyIcon() + " sur la banque de l'entreprise"), Prefix.SHOP, MessageType.SUCCESS, false);
            MessagesManager.sendMessage(player, Component.text("§aUn shop a bien été crée pour votre entreprise !"), Prefix.SHOP, MessageType.SUCCESS, false);
            return;
        }
        if (PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous avez déjà un shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        if (!PlayerShopManager.createShop(player.getUniqueId(), targetBlock, aboveBlock, null)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez d'argent pour créer un shop (500" + EconomyManager.getEconomyIcon() + ")"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§6[Shop] §c -500" + EconomyManager.getEconomyIcon() +" sur votre compte personnel"), Prefix.SHOP, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(player, Component.text("§aVous avez bien crée un shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
    }

    @Subcommand("sell")
    @Description("Sell an item in your shop")
    public void sellItem(Player player, @Named("price") double price) {
        boolean isInCompany = CompanyManager.isInCompany(player.getUniqueId());

        if (price<=0){
            MessagesManager.sendMessage(player, Component.text("§cVeuillez mettre un prix supérieur à zéro !"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }

        if (isInCompany) {
            UUID shopUUID = Shop.getShopPlayerLookingAt(player, false);
            if (shopUUID == null) {
                MessagesManager.sendMessage(player, Component.text("§cShop non reconnu"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            Shop shop = CompanyManager.getCompany(player.getUniqueId()).getShop(shopUUID);
            if (shop == null) {
                MessagesManager.sendMessage(player, Component.text("§cCe shop n'appartient pas à votre entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
          
            if (!CompanyManager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.SELLER)) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas l'autorisation de vendre un item dans ce shop de l'entrprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            boolean itemThere = shop.addItem(item, price, 1, null);

            if (itemThere) {
                MessagesManager.sendMessage(player, Component.text("§cCet item est déjà dans le shop"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }

            MessagesManager.sendMessage(player, Component.text("§aL'item a bien été ajouté au shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
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

    @Subcommand("unsell")
    @Description("Unsell an item in your shop")
    public void unsellItem(Player player, @Named("item number") int itemIndex) {
        boolean isInCompany = CompanyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            UUID shopUUID = Shop.getShopPlayerLookingAt(player, false);
            if (shopUUID == null) {
                MessagesManager.sendMessage(player, Component.text("§cShop non reconnu"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
          
            Shop shop = CompanyManager.getCompany(player.getUniqueId()).getShop(shopUUID);
            if (shop == null) {
                MessagesManager.sendMessage(player, Component.text("§cCe shop n'appartient pas à votre entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
          
            if (!CompanyManager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.SELLER)) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas l'autorisation de retirer un item en vente dans ce shop de l'entrprise"), Prefix.SHOP, MessageType.INFO, false);
            }

            if (itemIndex < 1 || itemIndex >= shop.getItems().size() + 1) {
                MessagesManager.sendMessage(player, Component.text("§cCet item n'est pas dans le shop"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }

            ShopItem item = shop.getItem(itemIndex - 1);
            if (item == null) {
                MessagesManager.sendMessage(player, Component.text("§cCet item n'est pas dans le shop"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }

            if (CompanyManager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.OWNER)){
                recoverStock(player, item, shop);
                return;
            }

            if (shop.isSupplier(player.getUniqueId()) ){
                int toTake = shop.recoverItemOf(item, player);

                if (toTake == 0) return;

                if (item.getAmount() >= 0) {
                    ItemStack toGive = item.getItem().clone();
                    toGive.setAmount(toTake);
                    player.getInventory().addItem(toGive);
                    if (item.getAmount() > 0){
                        MessagesManager.sendMessage(player, Component.text("§6Vous avez récupéré §a" + toTake + "§6 dans le stock de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
                    } else {
                        MessagesManager.sendMessage(player, Component.text("§6Vous avez récupéré le stock restant de cet item"), Prefix.SHOP, MessageType.SUCCESS, false);
                    }
                    return;
                }
            }

            return;
        }
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }

        Shop shop = PlayerShopManager.getPlayerShop(player.getUniqueId());
        ShopItem item = shop.getItem(itemIndex - 1);

        if (item == null) {
            MessagesManager.sendMessage(player, Component.text("§cCet item n'est pas dans le shop"), Prefix.SHOP, MessageType.INFO, false);
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
        boolean isInCompany = CompanyManager.isInCompany(player.getUniqueId());
        UUID shopUUID = Shop.getShopPlayerLookingAt(player, false);
        if (shopUUID == null) {
            MessagesManager.sendMessage(player, Component.text("§cShop non reconnu"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        if (isInCompany) {
            Shop shop = CompanyManager.getCompany(player.getUniqueId()).getShop(shopUUID);
            if (shop == null) {
                MessagesManager.sendMessage(player, Component.text("§cCe shop n'appartient pas à votre entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            if (!CompanyManager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.DELETESHOP)) {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission pour supprimer un shop de l'entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            MethodState deleteState = CompanyManager.getCompany(player.getUniqueId()).deleteShop(player, shop.getUuid());
            if (deleteState == MethodState.ERROR) {
                MessagesManager.sendMessage(player, Component.text("§cCe shop n'existe pas dans votre entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            if (deleteState == MethodState.WARNING) {
                MessagesManager.sendMessage(player, Component.text("§cCe shop n'est pas vide"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            if (deleteState == MethodState.SPECIAL) {
                MessagesManager.sendMessage(player, Component.text("§cIl vous faut au minimum le nombre d'argent remboursable pour supprimer un shop et obtenir un remboursement dans la banque de votre entreprise"), Prefix.SHOP, MessageType.INFO, false);
                return;
            }
            if (deleteState == MethodState.ESCAPE) {
                MessagesManager.sendMessage(player, Component.text("§cCaisse introuvable (appelez un admin)"), Prefix.SHOP, MessageType.INFO, false);
            }
            MessagesManager.sendMessage(player, Component.text("§a" + shop.getName() + " supprimé !"), Prefix.SHOP, MessageType.SUCCESS, false);
            MessagesManager.sendMessage(player, Component.text("§6[Shop] §a +75" + EconomyManager.getEconomyIcon() + " de remboursés sur la banque de l'entreprise"), Prefix.SHOP, MessageType.SUCCESS, false);
        }
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        MethodState methodState = PlayerShopManager.deleteShop(player.getUniqueId());
        if (methodState == MethodState.WARNING) {
            MessagesManager.sendMessage(player, Component.text("§cVotre shop n'est pas vide"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        if (methodState == MethodState.ESCAPE) {
            MessagesManager.sendMessage(player, Component.text("§cCaisse introuvable (appelez un admin)"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§6Votre shop a bien été supprimé !"), Prefix.SHOP, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(player, Component.text("§6[Shop] §a +400" + EconomyManager.getEconomyIcon() + " de remboursés sur votre compte personnel"), Prefix.SHOP, MessageType.SUCCESS, false);
    }

    @Subcommand("manage")
    @Description("Manage a shop")
    public void manageShop(Player player) {
        boolean isInCompany = CompanyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            ShopManageMenu shopManageMenu = new ShopManageMenu(player, CompanyManager.getCompany(player.getUniqueId()));
            shopManageMenu.open();
            return;
        }
        if (!PlayerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas de shop"), Prefix.SHOP, MessageType.INFO, false);
            return;
        }
        ShopMenu shopMenu = new ShopMenu(player, PlayerShopManager.getPlayerShop(player.getUniqueId()), 0);
        shopMenu.open();
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
            MessagesManager.sendMessage(owner, Component.text("§cVous n'avez pas assez de place"), Prefix.SHOP, MessageType.INFO, false);
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
