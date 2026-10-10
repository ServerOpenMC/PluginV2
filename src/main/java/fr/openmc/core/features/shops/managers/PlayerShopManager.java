package fr.openmc.core.features.shops.managers;

import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.protections.ProtectionsManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.events.PlaceShopEvent;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.registry.features.Feature;
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

public class PlayerShopManager extends Feature {
    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    private final ShopManager shopManager = OMCRegistry.FEATURES.SHOP.get();
    private ShopDatabaseManager shopDatabaseManager;

    @Override
    public void onEnable() {
        shopDatabaseManager = OMCRegistry.SHOP_FEATURES.SHOP_DB;
    }
    
    /**
     * Initiates the shop creation process for the specified player.
     *
     * @param player The player who is initiating the shop creation process.
     */
    public void startCreatingShop(OMCPlayer player) {
        if (!player.economy().withdrawBalance(500)) {
			player.message().send(TranslationManager.translation("feature.shop.player.not_enough_money",
                    Component.text("500 " + economyManager.getEconomyIcon(), NamedTextColor.RED)), Prefix.SHOP, MessageType.ERROR, true);
			return;
        }

        player.inputs().sendLocationInput(
                ItemStack.of(Material.BARREL),
                "shops:shop_creator",
                300,
                "feature.shop.player.creating_begin",
                TranslationManager.translation("feature.shop.player.creating_cancel"),
                location -> {
                    if (location == null) return false;
	                return createShop(player, location);
                },
                () -> {
                    player.economy().addBalance(500, "Canceling shop creation");
                    player.message().send(TranslationManager.translation("feature.shop.player.cancelling_pay",
                            Component.text("500 " + economyManager.getEconomyIcon()).color(NamedTextColor.GREEN)), Prefix.SHOP, MessageType.INFO, true);
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
    private boolean createShop(OMCPlayer player, Location location) {
        Shop shop = new Shop(player.getUniqueId(), location.setRotation(0, 0));
        CityManager cityManager = OMCRegistry.FEATURES.CITY.get();

        if (!location.getWorld().equals(Bukkit.getWorld("world"))) return false;
        if (OMCRegistry.HOOKS.WORLD_GUARD.isRegionConflict(location)) return false;
        ProtectionsManager protectionsManager = OMCRegistry.CITY_FEATURES.PROTECTIONS;
        if (!protectionsManager.canBypassPlayer.contains(player.getUniqueId())) {
            City city = City.ofPlayer(player.getUniqueId());
            if ((cityManager.isChunkClaimed(location.getChunk())
                    && city != null
                    && !city.equals(City.of(location.getChunk())))
            || (cityManager.isChunkClaimed(location.getChunk()) && city == null)) {
                player.message().send(TranslationManager.translation("feature.shop.player.chunk_claimed"), Prefix.SHOP, MessageType.ERROR, true);
                return false;
            }
        }
        
        Block barrel = shop.getMultiblock().stockBlockLoc().getBlock();
        Block cashBlock = shop.getMultiblock().cashBlockLoc().getBlock();
        
        if (barrel.getType() != Material.AIR) {
            player.message().send(TranslationManager.translation("feature.shop.player.cant_create_barrel"), Prefix.SHOP, MessageType.ERROR, true);
            return false;
        }
        
        if (cashBlock.getType() != Material.AIR) {
            player.message().send(TranslationManager.translation("feature.shop.player.cant_create_cash"), Prefix.SHOP, MessageType.ERROR, true);
            return false;
        }
        
        if (shopManager.placeShop(player, shop)) {
            barrel.setType(Material.BARREL);
            BlockData barrelData = barrel.getBlockData();
            if (barrelData instanceof Directional directional) {
                directional.setFacing(WorldUtils.getYaw(player).getOpposite().toBlockFace());
                barrel.setBlockData(barrelData);
            }
            
            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                if (!shopDatabaseManager.saveDBShop(shop)) {
                    player.message().send(TranslationManager.translation("feature.shop.error.cannot_save_location"), Prefix.SHOP, MessageType.ERROR, false);
	                OMCLogger.error("Error when saving shop location for player {}! Trying to remove shop...", player.getName());
                    if (!shopManager.removeShop(shop)) player.message().send(TranslationManager.translation("feature.shop.error.cannot_delete"), Prefix.SHOP, MessageType.ERROR, false);
                    player.message().send(TranslationManager.translation("feature.shop.error.pay_back",
                            Component.text("500 " + economyManager.getEconomyIcon()).color(NamedTextColor.GOLD)), Prefix.SHOP, MessageType.INFO, true);
                }
                else {
                    player.message().send(TranslationManager.translation("feature.shop.player.success_created"), Prefix.SHOP, MessageType.SUCCESS, true);
                    player.message().send(TranslationManager.translation("feature.shop.player.withdraw_money",
                            Component.text("500 " + economyManager.getEconomyIcon()).color(NamedTextColor.RED)), Prefix.SHOP, MessageType.SUCCESS, false);
                }
            });

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new PlaceShopEvent(player)));
            return true;
        } else {
            player.message().send(TranslationManager.translation("feature.shop.error.multiblock"), Prefix.SHOP, MessageType.ERROR, false);
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
    public void deleteShop(OMCPlayer player, Shop shop) {
        if (shop == null) {
            player.message().send(TranslationManager.translation("feature.shop.error.not_found"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        if (shop.getItem() != null && shop.getItem().getAmount() > 0) {
            player.message().send(TranslationManager.translation("feature.shop.player.is_not_empty"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        World world = Bukkit.getWorld("world");
        if (world == null) return;
        
        if (world.getBlockAt(shop.getMultiblock().stockBlockLoc()).getState() instanceof Barrel barrel) {
            if (!barrel.getInventory().isEmpty()) {
                player.message().send(TranslationManager.translation("feature.shop.player.barrel_is_not_empty"), Prefix.SHOP, MessageType.WARNING, false);
                return;
            }
        } else {
            OMCLogger.error("Barrel block is not an instance of barrel");
            player.message().send(TranslationManager.translation("feature.shop.error.barrel"), Prefix.SHOP, MessageType.WARNING, false);
            return;
        }
        
        if (!shopManager.removeShop(shop)) {
            player.message().send(TranslationManager.translation("feature.shop.error.cannot_delete"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            if (!shopDatabaseManager.deleteDBShop(shop)) {
                player.message().send(TranslationManager.translation("feature.shop.error.cannot_remove_furniture"), Prefix.SHOP, MessageType.ERROR, false);
                OMCLogger.error("Error when " + player.getName() + " trying to delete his shop!");
            }
        });

        player.message().send(TranslationManager.translation("feature.shop.player.deleted"), Prefix.SHOP, MessageType.SUCCESS, false);

        player.economy().addBalance(400);
        player.message().send(TranslationManager.translation("feature.shop.player.pay_back",
                Component.text("400 " + economyManager.getEconomyIcon()).color(NamedTextColor.GREEN)), Prefix.SHOP, MessageType.SUCCESS, true);
    }
}
