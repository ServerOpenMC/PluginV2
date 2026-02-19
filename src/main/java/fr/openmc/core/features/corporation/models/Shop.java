package fr.openmc.core.features.corporation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.manager.ShopManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shops")
public class Shop {
    
    @DatabaseField(id = true, columnName = "owner_uuid", canBeNull = false)
    private UUID ownerUUID;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int y;
    @DatabaseField(canBeNull = false)
    private int z;
    
    private final List<ShopItem> items = new ArrayList<>();
    private final List<ShopItem> sales = new ArrayList<>();
	
    private Location location;
	private Multiblock multiblock;

    private double turnover = 0;
    
    Shop() {
        // required for ORMLite
    }
    
    public Shop(UUID ownerUUID, int x, int y, int z) {
		this(ownerUUID, new Location(Bukkit.getWorld("world"), x, y, z));
    }
    
    public Shop(UUID ownerUUID, Location location) {
        this.ownerUUID = ownerUUID;
	    this.x = location.getBlockX();
	    this.y = location.getBlockY();
	    this.z = location.getBlockZ();
        this.location = location.toBlockLocation();
		this.multiblock = new Multiblock(this.location, this.location.clone().add(0, 1, 0));
    }

    /**
     * requirement : item need the uuid of the player who restock the shop

     * quand un item est vendu un partie du profit reviens a celui qui a approvisionner
     */
    public void checkStock() {
        Multiblock multiblock = getMultiblock();

        if (multiblock == null) return;

        Block stockBlock = multiblock.stockBlock().getBlock();
        if (stockBlock.getType() != Material.BARREL) {
            ShopManager.removeShop(this);
            return;
        }

        if (stockBlock.getState(false) instanceof Barrel barrel) {

            Inventory inventory = barrel.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item == null || item.getType() == Material.AIR) continue;
				
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) continue;
            }
        }
    }

    public String getName() {
        return CacheOfflinePlayer.getOfflinePlayer(ownerUUID).getName() + "'s Shop";
    }

    /**
     * know if the uuid is the shop owner
     *
     * @param uuid the uuid we check
     */
    public boolean isOwner(UUID uuid) {
        return ownerUUID.equals(uuid);
    }

    public void addItem(ShopItem item){
        items.add(item);
    }

    public void addSales(ShopItem item){
        sales.add(item);
    }

    /**
     * get an item from the shop
     *
     * @param index index of the item
     */
    public ShopItem getItem(int index) {
        return items.get(index);
    }

    /**
     * add an item to the shop
     *
     * @param itemStack the item
     * @param price the price
     * @param amount the amount of it
     */
    public boolean addItem(ItemStack itemStack, double price, int amount, UUID itemID) {
        ShopItem item = itemID == null ? new ShopItem(itemStack, price) : new ShopItem(itemStack, price, itemID);
        for (ShopItem shopItem : items) if (shopItem.getItem().isSimilar(itemStack)) return true;
        
        if (amount > 1) item.setAmount(amount);
        
        items.add(item);
        return false;
    }

    /**
     * buy an item in the shop
     *
     * @param item the item to buy
     * @param amountToBuy the amount of it
     * @param buyer the player who buy
     * @return a MethodState
     */
    public MethodState buy(ShopItem item, int amountToBuy, Player buyer) {
        if (! ItemUtils.hasAvailableSlot(buyer)) return MethodState.SPECIAL;
        if (amountToBuy > item.getAmount()) return MethodState.WARNING;
        if (isOwner(buyer.getUniqueId())) return MethodState.FAILURE;
        
        if (!EconomyManager.withdrawBalance(buyer.getUniqueId(), item.getPrice(amountToBuy))) return MethodState.ERROR;
        double basePrice = item.getPrice(amountToBuy);
        turnover += item.getPrice(amountToBuy);
        
        EconomyManager.addBalance(ownerUUID, item.getPrice(amountToBuy));
        Player player = Bukkit.getPlayer(ownerUUID);
        if (player != null) {
            MessagesManager.sendMessage(player, Component.text(buyer.getName() + " a acheté " + amountToBuy + " " + item.getItem().getType() + " pour " + item.getPrice(amountToBuy) + EconomyManager.getEconomyIcon() + ", l'argent vous a été transféré !"), Prefix.SHOP, MessageType.SUCCESS, false);
        }
        
        ItemStack toGive = item.getItem().clone();
        toGive.setAmount(amountToBuy);

        List<ItemStack> stacks = ItemUtils.splitAmountIntoStack(toGive);
        for (ItemStack stack : stacks) buyer.getInventory().addItem(stack);
        
        sales.add(item.copy().setAmount(amountToBuy));
        item.setAmount(item.getAmount() - amountToBuy);

        return MethodState.SUCCESS;
    }

    /**
     * get the shop Icon
     *
     * @param menu the menu
     * @param fromShopMenu know if it from shopMenu
     */
    public ItemBuilder getIcon(Menu menu, boolean fromShopMenu) {
        return new ItemBuilder(menu, fromShopMenu ? Material.GOLD_INGOT : Material.BARREL, itemMeta -> {
            itemMeta.displayName(Component.text("§e§l" + (fromShopMenu ? "Informations" : getName())));
            
            List<Component> lore = new ArrayList<>(List.of(
                    Component.text("§7■ Chiffre d'affaires : " + EconomyManager.getFormattedNumber(turnover)),
                    Component.text("§7■ Ventes : §f" + sales.size())
            ));
            if (! fromShopMenu) lore.add(Component.text("§7■ Cliquez pour accéder au shop"));
            itemMeta.lore(lore);
        });
    }

    public int getAllItemsAmount() {
        int amount = 0;
        for (ShopItem item : items) amount += item.getAmount();
        return amount;
    }

    public record Multiblock(Location stockBlock, Location cashBlock) {}
}
