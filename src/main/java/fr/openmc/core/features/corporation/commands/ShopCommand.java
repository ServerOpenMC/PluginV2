package fr.openmc.core.features.corporation.commands;


import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.corporation.menu.company.ShopManageMenu;
import fr.openmc.core.features.corporation.menu.shop.ShopMenu;
import fr.openmc.core.features.city.MethodState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;
import java.util.UUID;

@Command({"shop", "shops"})
@Description("Manage shops")
@CommandPermission("ayw.command.shop")
public class ShopCommand {

    private final CompanyManager companyManager = CompanyManager.getInstance();
    private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();
    private final ShopBlocksManager shopBlocksManager = ShopBlocksManager.getInstance();

    @DefaultFor("~")
    public void onCommand(Player player) {
        boolean isInCompany = companyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            ShopManageMenu shopManageMenu = new ShopManageMenu(player, companyManager.getCompany(player.getUniqueId()));
            shopManageMenu.open();
            return;
        }
        if (!playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("Usage: /shop <create | manage | sell | unsell | delete> <shop>");
            return;
        }
        ShopMenu shopMenu = new ShopMenu(player, companyManager, playerShopManager, playerShopManager.getPlayerShop(player.getUniqueId()), 0);
        shopMenu.open();
    }

    @Subcommand("id")
    public void giveID(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType()!=Material.AIR){
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.getPersistentDataContainer().set(OMCPlugin.SUPPLIER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
            item.setItemMeta(itemMeta);
        }
    }

    @Subcommand("create")
    @Description("Create a shop")
    public void createShop(Player player) {
        boolean isInCompany = companyManager.isInCompany(player.getUniqueId());
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() != Material.BARREL) {
            player.sendMessage("§cVous devez regarder un tonneau pour créer un shop");
            return;
        }
        Block aboveBlock = Objects.requireNonNull(targetBlock.getLocation().getWorld()).getBlockAt(targetBlock.getLocation().clone().add(0, 1, 0));
        if (aboveBlock.getType() != Material.AIR) {
            player.sendMessage("§cVous devez liberer de l'espace au dessus de votre tonneau pour créer un shop");
            return;
        }
        if (isInCompany) {
            Company company = companyManager.getCompany(player.getUniqueId());
            if (!company.isOwner(player.getUniqueId())) {
                player.sendMessage("§cVous devez être un des propriétaires de l'entreprise pour créer un shop");
                return;
            }
            if (!company.createShop(player.getUniqueId(), targetBlock, aboveBlock, null)) {
                player.sendMessage("§cVous n'avez pas assez d'argent dans la banque de votre entreprise pour créer un shop (100€)");
                return;
            }
            player.sendMessage("§6[Shop] §c -100€ sur la banque de l'entreprise");
            player.sendMessage("§aUn shop a bien été crée pour votre entreprise !");
            return;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous avez déjà un shop");
            return;
        }
        if (!playerShopManager.createShop(player.getUniqueId(), targetBlock, aboveBlock, null)) {
            player.sendMessage("§cVous n'avez pas assez d'argent pour créer un shop (500€)");
            return;
        }
        player.sendMessage("§6[Shop] §c -500€ sur votre compte personnel");
        player.sendMessage("§aVous avez bien crée un shop !");
    }

    @Subcommand("sell")
    @Description("Sell an item in your shop")
    public void sellItem(Player player, @Named("price") double price) {
        boolean isInCompany = companyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            UUID shopUUID = Shop.getShopPlayerLookingAt(player, shopBlocksManager, false);
            if (shopUUID == null) {
                player.sendMessage("§cShop non reconnu");
                return;
            }
            Shop shop = companyManager.getCompany(player.getUniqueId()).getShop(shopUUID);
            if (shop == null) {
                player.sendMessage("§cCe shop n'appartient pas à votre entreprise");
                return;
            }
            if (!shop.isOwner(player.getUniqueId())) {
                player.sendMessage("§cVous n'êtes pas un des propriétaires de ce shop");
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            boolean itemThere = shop.addItem(item, price, 1);
            if (itemThere) {
                player.sendMessage("§cCet item est déjà dans le shop");
                return;
            }
            player.sendMessage("§aL'item a bien été ajouté au shop !");
            return;
        }
        if (!playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous n'avez pas de shop");
            return;
        }
        Shop shop = playerShopManager.getPlayerShop(player.getUniqueId());
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage("§cVous devez tenir un item dans votre main");
            return;
        }
        boolean itemThere = shop.addItem(item, price, 1);
        if (itemThere) {
            player.sendMessage("§cCet item est déjà dans le shop");
            return;
        }
        player.sendMessage("§aL'item a bien été ajouté au shop !");
    }

    @Subcommand("unsell")
    @Description("Unsell an item in your shop")
    public void unsellItem(Player player, @Named("item number") int itemIndex) {
        boolean isInCompany = companyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            UUID shopUUID = Shop.getShopPlayerLookingAt(player, shopBlocksManager, false);
            if (shopUUID == null) {
                player.sendMessage("§cShop non reconnu");
                return;
            }
            Shop shop = companyManager.getCompany(player.getUniqueId()).getShop(shopUUID);
            if (shop == null) {
                player.sendMessage("§cCe shop n'appartient pas à votre entreprise");
                return;
            }
            if (!shop.isOwner(player.getUniqueId())) {
                player.sendMessage("§cVous n'êtes pas un des propriétaires de ce shop");
                return;
            }
            if (itemIndex < 1 || itemIndex >= shop.getItems().size() + 1) {
                player.sendMessage("§cCet item n'est pas dans le shop");
                return;
            }
            ShopItem item = shop.getItem(itemIndex - 1);
            if (item == null) {
                player.sendMessage("§cCet item n'est pas dans le shop");
                return;
            }
            shop.removeItem(item);
            player.sendMessage("§aL'item a bien été retiré du shop !");
            if (item.getAmount() > 0) {
                ItemStack toGive = item.getItem().clone();
                toGive.setAmount(item.getAmount());
                player.getInventory().addItem(toGive);
                player.sendMessage("§6Vous avez récupéré le stock restant de cet item");
            }
            return;
        }
        if (!playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous n'avez pas de shop");
            return;
        }
        Shop shop = playerShopManager.getPlayerShop(player.getUniqueId());
        ShopItem item = shop.getItem(itemIndex - 1);
        if (item == null) {
            player.sendMessage("§cCet item n'est pas dans le shop");
            return;
        }
        shop.removeItem(item);
        player.sendMessage("§aL'item a bien été retiré du shop !");
        if (item.getAmount() > 0) {
            ItemStack toGive = item.getItem().clone();
            toGive.setAmount(item.getAmount());
            player.getInventory().addItem(toGive);
            player.sendMessage("§6Vous avez récupéré le stock restant de cet item");
        }
    }

    @Subcommand("delete")
    @Description("Delete a shop")
    public void deleteShop(Player player) {
        boolean isInCompany = companyManager.isInCompany(player.getUniqueId());
        UUID shopUUID = Shop.getShopPlayerLookingAt(player, shopBlocksManager, false);
        if (shopUUID == null) {
            player.sendMessage("§cShop non reconnu");
            return;
        }
        if (isInCompany) {
            Shop shop = companyManager.getCompany(player.getUniqueId()).getShop(shopUUID);
            if (shop == null) {
                player.sendMessage("§cCe shop n'appartient pas à votre entreprise");
                return;
            }
            if (!companyManager.getCompany(player.getUniqueId()).isOwner(player.getUniqueId())) {
                player.sendMessage("§cVous devez être un des propriétaires de l'entreprise pour supprimer un shop");
                return;
            }
            if (shop.isOwner(player.getUniqueId())) {
                player.sendMessage("§cVous n'êtes pas un des propriétaires de ce shop");
                return;
            }
            MethodState deleteState = companyManager.getCompany(player.getUniqueId()).deleteShop(player, shop.getUuid());
            if (deleteState == MethodState.ERROR) {
                player.sendMessage("§cCe shop n'existe pas dans votre entreprise");
                return;
            }
            if (deleteState == MethodState.WARNING) {
                player.sendMessage("§cCe shop n'est pas vide");
                return;
            }
            if (deleteState == MethodState.SPECIAL) {
                player.sendMessage("§cIl vous faut au minimum le nombre d'argent remboursable pour supprimer un shop et obtenir un remboursement dans la banque de votre entreprise");
                return;
            }
            if (deleteState == MethodState.ESCAPE) {
                player.sendMessage("§cCaisse introuvable (appelez un admin)");
            }
            player.sendMessage("§a" + shop.getName() + " supprimé !");
            player.sendMessage("§6[Shop] §a +75€ de remboursés sur la banque de l'entreprise");
        }
        if (!playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous n'avez pas de shop");
            return;
        }
        MethodState methodState = playerShopManager.deleteShop(player.getUniqueId());
        if (methodState == MethodState.WARNING) {
            player.sendMessage("§cVotre shop n'est pas vide");
            return;
        }
        if (methodState == MethodState.ESCAPE) {
            player.sendMessage("§cCaisse introuvable (appelez un admin)");
            return;
        }
        player.sendMessage("§6Votre shop a bien été supprimé !");
        player.sendMessage("§6[Shop] §a +400€ de remboursés sur votre compte personnel");
    }

    @Subcommand("manage")
    @Description("Manage a shop")
    public void manageShop(Player player) {
        boolean isInCompany = companyManager.isInCompany(player.getUniqueId());
        if (isInCompany) {
            ShopManageMenu shopManageMenu = new ShopManageMenu(player, companyManager.getCompany(player.getUniqueId()));
            shopManageMenu.open();
            return;
        }
        if (!playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous n'avez pas de shop");
            return;
        }
        ShopMenu shopMenu = new ShopMenu(player, companyManager, playerShopManager, playerShopManager.getPlayerShop(player.getUniqueId()), 0);
        shopMenu.open();
    }

}
