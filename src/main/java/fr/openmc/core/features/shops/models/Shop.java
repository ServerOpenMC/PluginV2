package fr.openmc.core.features.shops.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.ShopFurniture;
import fr.openmc.core.features.shops.managers.ShopManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shops")
public class Shop {
    
    @DatabaseField(id = true, columnName = "shop_uuid", canBeNull = false)
    private UUID shopUUID;
    @DatabaseField(columnName = "owner_uuid", canBeNull = false)
    private UUID ownerUUID;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int y;
    @DatabaseField(canBeNull = false)
    private int z;
    @DatabaseField(canBeNull = false)
    @Setter private double turnover = 0;
    
    private ShopItem item;
    private final List<ShopSale> sales = new ArrayList<>();
	
    private Location location;
	private Multiblock multiblock;
    
    @Setter private boolean menuOpened;
    private Timestamp lastWithdrawal;
    
    Shop() {
        // required for ORMLite
    }
    
    public Shop(UUID ownerUUID, int x, int y, int z) {
		this(ownerUUID, new Location(Bukkit.getWorld("world"), x, y, z));
    }
    
    public Shop(UUID ownerUUID, Location location) {
        this.shopUUID = UUID.randomUUID();
        this.ownerUUID = ownerUUID;
	    this.x = location.getBlockX();
	    this.y = location.getBlockY();
	    this.z = location.getBlockZ();
        this.location = location.toBlockLocation();
		this.multiblock = new Multiblock(this.location, this.location.clone().add(0, 1, 0));
    }
    
    /**
     * Retrieves the owner of the shop as an OfflinePlayer.
     *
     * @return the OfflinePlayer instance representing the shop's owner,
     *         retrieved from the cached or default mechanism based on the owner's UUID.
     */
    public OfflinePlayer getOwner() {
        return CacheOfflinePlayer.getOfflinePlayer(ownerUUID);
    }
    
    /**
     * Retrieves the name of the shop by combining the owner's name with a descriptor.
     *
     * @return the name of the shop in the format "<owner's name>'s Shop"
     */
    public String getName() {
        return PlayerNameCache.getName(getOwnerUUID()) + "'s Shop";
    }

    /**
     * Know if the UUID is the shop owner UUID
     *
     * @param uuid the UUID to check
     */
    public boolean isOwner(UUID uuid) {
        return ownerUUID.equals(uuid) || ShopManager.shopBypass.contains(uuid);
    }
    
    /**
     * Know if the player is the shop owner
     *
     * @param player the UUID to check
     */
    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }
    
    /**
     * Records a new sale in the shop by adding a {@code ShopSale} entry.
     *
     * @param player the player who made the purchase
     * @param item   the shop item being purchased
     */
    public void addSale(Player player, ShopItem item) {
        this.sales.add(new ShopSale(this.shopUUID, player.getUniqueId(), item));
    }
    
    /**
     * Registers a new sale in the shop by adding the given {@code ShopSale} entry.
     *
     * @param sale the {@code ShopSale} instance representing the sale to be recorded
     */
    public void registerSale(ShopSale sale) {
        this.sales.add(sale);
    }
    
    /**
     * Adds the specified amount to the shop's turnover.
     *
     * @param amount the amount to be added to the turnover
     */
    public void addTurnover(double amount) {
        this.turnover += amount;
    }
    
    /**
     * Withdraws the current turnover of the shop and adds a percentage of the turnover
     * to the owner's balance.
     */
    public void withdrawTurnover() {
        Player player = CacheOfflinePlayer.getOfflinePlayer(getOwnerUUID()).getPlayer();
        if (player == null) return;
        if (!isOwner(player)) return;
        if (getTurnover() <= 0) return;
        double tempTurnover = getTurnover();
        EconomyManager.addBalance(player.getUniqueId(), tempTurnover * 0.8, "turnover");
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.get_turnover", Component.text(tempTurnover * 0.8 + " " + EconomyManager.getEconomyIcon())), Prefix.SHOP, MessageType.SUCCESS, false);
        setTurnover(0);
    }
    
    /**
     * Handles the purchase process for a given player, allowing them to buy a specified number
     * of items from the shop.
     *
     * @param player the player attempting to make the purchase
     * @param amount the quantity of items the player wants to buy
     */
    public void buy(Player player, int amount) {
        if (isOwner(player)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.is_owner"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        if (this.item.getAmount() < amount) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.not_enough_items"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        if (!ItemUtils.hasEnoughSpace(player, item.getItemStack(), amount)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.not_enough_space"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        double totalPrice = this.item.getPrice(amount);
        if (!EconomyManager.withdrawBalance(player.getUniqueId(), totalPrice, getName() + " buying")) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.not_enough_money"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        player.give(ItemUtils.splitAmountIntoStack(item.clone().getItemStack(), amount));
        addSale(player, this.item.clone().setAmount(amount));
        addTurnover(totalPrice);
        this.item.removeAmount(amount);
    }
    
    /**
     * Sets the multiblock for the shop, ensuring the required block types and configurations are met.
     *
     * @param multiblock the {@code Multiblock} instance representing the structure associated with the shop
     * @return {@code true} if the multiblock configuration is valid and successfully set, {@code false} otherwise
     */
    public boolean setMultiblock(Multiblock multiblock) {
        if (multiblock.stockBlockLoc.getBlock().getType() != Material.BARREL
            || (multiblock.cashBlockLoc.getBlock().getType() != Material.OAK_SIGN
            && !ShopFurniture.hasFurniture(multiblock.cashBlockLoc.getBlock()))) {
            return false;
        }
        this.multiblock = multiblock;
        return true;
    }
    
    /**
     * Sets the specified {@code ShopItem} as the shop's item if it meets certain conditions.
     *
     * @param item the {@code ShopItem} to be set in the shop.
     */
    public void setItem(ShopItem item) {
        if (this.item != null) return;
        if (item.getPricePerItem() < 0) return;
        if (item.getItemStack() == null) return;
        this.item = item;
    }
    
    /**
     * Removes the shop's current item if it meets specific conditions.
     */
    public void removeItem() {
        if (!hasItem()) return;
        if (this.item.getAmount() > 0) return;
        this.item = null;
    }
    
    /**
     * Checks whether the shop currently has an item set.
     *
     * @return {@code true} if the shop has an item, {@code false} otherwise
     */
    public boolean hasItem() {
        return this.item != null;
    }
    
    /**
     * Empties the shop by setting the amount of the associated shop item to zero.
     */
    public void emptyShop() {
        if (!hasItem()) return;
        this.getItem().setAmount(0);
    }
    
    /**
     * Updates the timestamp of the last withdrawal made from the shop.
     * The method sets the {@code lastWithdrawal} field to the current date and time.
     */
    public void setLastWithdrawalToNow() {
        this.lastWithdrawal = Timestamp.valueOf(LocalDateTime.now());
    }

    public record Multiblock(Location stockBlockLoc, Location cashBlockLoc) {}
}
