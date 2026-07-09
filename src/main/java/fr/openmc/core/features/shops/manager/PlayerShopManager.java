package fr.openmc.core.features.shops.manager;

import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerShopManager {
    
    /**
     * Initiates the shop creation process for the specified player.
     *
     * @param player The player who is initiating the shop creation process.
     */
    public static void startCreatingShop(Player player) {
        if (!EconomyManager.withdrawBalance(player.getUniqueId(), 500)) {
			MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.not_enough_money", Component.text("500 " + EconomyManager.getEconomyIcon()).color(NamedTextColor.RED)), Prefix.SHOP, MessageType.ERROR, true);
			return;
        }
        
        ItemInteraction.runLocationInteraction(
                player,
                new ItemStack(Material.BARREL),
                "shops:shop_creator",
                300,
                TranslationManager.translation("feature.shop.player.creating_begin"),
                TranslationManager.translation("feature.shop.player.creating_cancel"),
                location -> {
                    if (location == null) return false;
	                return createShop(player, location);
                },
                () -> {
                    EconomyManager.addBalance(player.getUniqueId(), 500, "Canceling shop creation");
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.cancelling_pay", Component.text("500 " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GREEN)), Prefix.SHOP, MessageType.INFO, true);
                }
        );
    }
    
    /**
     * Creates a shop for the specified player at the given location.
     *
     * @param player   The player who is creating the shop.
     * @param location The location where the shop is to be created.
     * @return true if the shop creation is successful, false otherwise.
     */
    private static boolean createShop(Player player, Location location) {
        Shop shop = new Shop(player.getUniqueId(), location.setRotation(0, 0));
        
        if (CityManager.isChunkClaimed(location.getChunk()) && !CityManager.getPlayerCity(player.getUniqueId()).equals(CityManager.getCityFromChunk(location.getChunk()))) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.chunk_claimed"), Prefix.SHOP, MessageType.ERROR, true);
            return false;
        }
        
        Block barrel = shop.getMultiblock().stockBlockLoc().getBlock();
        Block cashBlock = shop.getMultiblock().cashBlockLoc().getBlock();
        
        if (barrel.getType() != Material.AIR) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.cant_create_barrel"), Prefix.SHOP, MessageType.ERROR, true);
            return false;
        }
        
        if (cashBlock.getType() != Material.AIR) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.cant_create_cash"), Prefix.SHOP, MessageType.ERROR, true);
            return false;
        }
        
        if (ShopManager.placeShop(player, shop)) {
            barrel.setType(Material.BARREL);
            BlockData barrelData = barrel.getBlockData();
            if (barrelData instanceof Directional directional) {
                directional.setFacing(WorldUtils.getYaw(player).getOpposite().toBlockFace());
                barrel.setBlockData(barrelData);
            }
            
            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                if (!ShopDatabaseManager.saveDBShop(shop)) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.cannot_save_location"), Prefix.SHOP, MessageType.ERROR, false);
	                OMCLogger.error("Error when saving shop location for player {}! Trying to remove shop...", player.getName());
                    if (!ShopManager.removeShop(shop)) MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.cannot_delete"), Prefix.SHOP, MessageType.ERROR, false);
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.pay_back", Component.text("500 " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD)), Prefix.SHOP, MessageType.INFO, true);
                }
                else {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.success_created"), Prefix.SHOP, MessageType.SUCCESS, true);
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.withdraw_money", Component.text("500 " + EconomyManager.getEconomyIcon()).color(NamedTextColor.RED)), Prefix.SHOP, MessageType.SUCCESS, false);
                }
            });
            return true;
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.multiblock"), Prefix.SHOP, MessageType.ERROR, false);
            return false;
        }
    }
    
    /**
     * Deletes the specified shop for the given player.
     *
     * @param player The player attempting to delete the shop.
     * @param shop   The shop to be deleted. If the shop is null or not empty, the deletion
     *               process will be aborted with a warning message.
     */
    public static void deleteShop(Player player, Shop shop) {
        if (shop == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.not_found"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        if (shop.getItem() != null && shop.getItem().getAmount() > 0) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.is_not_empty"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        World world = Bukkit.getWorld("world");
        if (world == null) return;
        
        if (world.getBlockAt(shop.getMultiblock().stockBlockLoc()).getState() instanceof Barrel barrel) {
            if (!barrel.getInventory().isEmpty()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.barrel_is_not_empty"), Prefix.SHOP, MessageType.WARNING, false);
                return;
            }
        } else {
            OMCLogger.error("Barrel block is not an instance of barrel");
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.barrel"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        if (!ShopManager.removeShop(shop)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.cannot_delete"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            if (!ShopDatabaseManager.deleteDBShop(shop)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.error.cannot_remove_furniture"), Prefix.SHOP, MessageType.ERROR, false);
                OMCLogger.error("Error when " + player.getName() + " trying to delete his shop!");
            }
        });
        
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.deleted"), Prefix.SHOP, MessageType.SUCCESS, false);
        
        EconomyManager.addBalance(player.getUniqueId(), 400);
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.pay_back", Component.text("400 " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GREEN)), Prefix.SHOP, MessageType.SUCCESS, true);
    }
    
    /* public static void adminDeleteShop(OfflinePlayer player, Player admin) {
        Shop shop = ShopManager.getPlayerShop(player.getUniqueId());
        if (shop == null) return;
        
        if (!ShopManager.removeShop(shop)) {
            MessagesManager.sendMessage(admin, Component.text("§cShop introuvable"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        ShopManager.getPlayerShops().remove(player.getUniqueId());
        
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            if (!ShopDatabaseManager.deleteDBShop(shop)) {
                MessagesManager.sendMessage(admin, Component.text("§cErreur lors de la suppression du shop dans la db"), Prefix.SHOP, MessageType.ERROR, false);
            }
        });
        
        MessagesManager.sendMessage(admin, Component.text("§6Le shop a bien été supprimé !"), Prefix.SHOP, MessageType.SUCCESS, false);
        EconomyManager.addBalance(player.getUniqueId(), 400);
        MessagesManager.sendMessage(player, Component.text("§a400" + EconomyManager.getEconomyIcon() + " remboursés sur votre compte personnel"), Prefix.SHOP, MessageType.SUCCESS, true);
    } */
}
