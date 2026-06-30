package fr.openmc.core.features.shops.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.ShopFurniture;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
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
    
    private ShopItem item;
    private final List<ShopSale> sales = new ArrayList<>();
	
    private Location location;
	private Multiblock multiblock;

    @Setter
    private double turnover = 0;
    
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
    
    public OfflinePlayer getOwner() {
        return CacheOfflinePlayer.getOfflinePlayer(ownerUUID);
    }

    public String getName() {
        return getOwner().getName() + "'s Shop";
    }

    /**
     * know if the uuid is the shop owner
     *
     * @param uuid the uuid we check
     */
    public boolean isOwner(UUID uuid) {
        return ownerUUID.equals(uuid);
    }
    
    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }
    
    public void addSale(Player player, ShopItem item) {
        this.sales.add(new ShopSale(this.shopUUID, player.getUniqueId(), item));
    }
    
    public void registerSale(ShopSale sale) {
        this.sales.add(sale);
    }
    
    public void addTurnover(double amount) {
        this.turnover += amount;
    }
    
    public void withdrawTurnover() {
        Player player = CacheOfflinePlayer.getOfflinePlayer(getOwnerUUID()).getPlayer();
        if (player == null) return;
        if (!isOwner(player)) return;
        if (getTurnover() <= 0) return;
        double tempTurnover = getTurnover();
        EconomyManager.addBalance(player.getUniqueId(), tempTurnover * 0.8, "turnover");
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.get_turnover", Component.text(tempTurnover + " " + EconomyManager.getEconomyIcon())), Prefix.SHOP, MessageType.SUCCESS, false);
        setTurnover(0);
    }
    
    public void buy(Player player, int amount) {
        /*if (isOwner(player)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.is_owner"), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }*/
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
     * get the shop Icon
     *
     * @param menu the menu
     * @param fromShopMenu know if it from shopMenu
     */
    public ItemMenuBuilder getIcon(Menu menu, boolean fromShopMenu) {
        return new ItemMenuBuilder(menu, fromShopMenu ? Material.GOLD_INGOT : Material.BARREL, itemMeta -> {
            itemMeta.displayName(Component.text("§e§l" + (fromShopMenu ? "Informations" : getName())));
            
            List<Component> lore = new ArrayList<>(List.of(
                    Component.text("§7■ Chiffre d'affaires : " + EconomyManager.getFormattedNumber(turnover)),
                    Component.text("§7■ Ventes : §f" + sales.size())
            ));
            if (!fromShopMenu) lore.add(Component.text("§7■ Cliquez pour accéder au shop")); //TODO a voir si on garde
            itemMeta.lore(lore);
        });
    }
    
    public boolean setMultiblock(Multiblock multiblock) {
        if (multiblock.stockBlockLoc.getBlock().getType() != Material.BARREL
            || (multiblock.cashBlockLoc.getBlock().getType() != Material.OAK_SIGN
            && !ShopFurniture.hasFurniture(multiblock.cashBlockLoc.getBlock()))) {
            return false;
        }
        this.multiblock = multiblock;
        return true;
    }
    
    public void setItem(ShopItem item) {
        if (this.item != null) return;
        if (item.getPricePerItem() < 0) return;
        if (item.getItemStack() == null) return;
        this.item = item;
    }
    
    public void removeItem() {
        if (this.item == null) return;
        if (this.item.getAmount() > 0) return;
        this.item = null;
    }
    
    public boolean hasItem() {
        return this.item != null;
    }
    
    public void emptyShop() {
        this.getItem().setAmount(0);
    }

    public record Multiblock(Location stockBlockLoc, Location cashBlockLoc) {}
}
