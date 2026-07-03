package fr.openmc.core.features.shops.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.shops.manager.ShopManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shop_sales")
public class ShopSale {
    @DatabaseField(canBeNull = false, id = true, columnName = "sale_uuid")
    private UUID saleUUID;
    @DatabaseField(canBeNull = false, columnName = "shop_uuid")
    private UUID shopUUID;
    @DatabaseField(canBeNull = false, columnName = "player_uuid")
    private UUID buyerUUID;
    @DatabaseField(canBeNull = false)
    private Timestamp date;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;
    
    private OfflinePlayer buyer;
    private ShopItem item;

    ShopSale() {
        // required for ORMLite
    }
    
    public ShopSale(UUID shopUUID, UUID buyerUUID, ShopItem item, Timestamp date) {
        this.saleUUID = UUID.randomUUID();
        this.shopUUID = shopUUID;
        this.buyerUUID = buyerUUID;
        this.price = item.getPrice();
        this.amount = item.getAmount();
        this.date = date;
        this.item = item;
        
        registerVariables();
    }
    
    public ShopSale(UUID shopUUID, UUID buyerUUID, ShopItem item) {
        this(shopUUID, buyerUUID, item, Timestamp.valueOf(LocalDateTime.now()));
    }
    
    /**
     * Retrieves the associated {@code Shop} object for this sale.
     *
     * @return the {@code Shop} corresponding to the {@code shopUUID} of this sale
     */
    public Shop getShop() {
        return ShopManager.getShopByUUID(this.shopUUID);
    }
    
    /**
     * Initializes or updates the state of certain member variables in the {@code ShopSale} instance.
     * This method ensures that the following variables are properly assigned:
     */
    public void registerVariables() {
        if (this.buyer == null) this.buyer = CacheOfflinePlayer.getOfflinePlayer(this.buyerUUID);
        ShopItem shopItem = getShop().getItem();
        if (this.item == null && shopItem != null) this.item = shopItem.clone().setAmount(this.amount);
    }
}
