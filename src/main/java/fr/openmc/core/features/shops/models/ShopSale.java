package fr.openmc.core.features.shops.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.shops.manager.ShopManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

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
    
    private Player buyer;
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
    
    public Shop getShop() {
        return ShopManager.getShopByUUID(this.shopUUID);
    }
    
    public ShopSale registerVariables() {
        if (this.buyer == null) this.buyer = CacheOfflinePlayer.getOfflinePlayer(this.buyerUUID).getPlayer();
        if (this.item == null) this.item = getShop().getItem().clone().setAmount(this.amount);
        return this;
    }
}
