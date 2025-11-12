package fr.openmc.core.features.corporation.shops;

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

import java.util.*;

@Getter
public class Shop {
    
    private final UUID ownerUUID;
    private final List<ShopItem> items = new ArrayList<>();
    private final List<ShopItem> sales = new ArrayList<>();
    private final Map<Long, Supply> suppliers = new HashMap<>();
    private final Location location;

    private double turnover = 0;
    
    public Shop(UUID ownerUUID, Location location) {
        this.ownerUUID = ownerUUID;
        this.location = location;
    }

    /**
     * get the shop with what player looking
     *
     * @param player the player we check
     * @param onlyCash if we only check the cach register
     */
    public static UUID getShopPlayerLookingAt(Player player, boolean onlyCash) {
        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) return null;

        if (targetBlock.getType() != Material.BARREL && targetBlock.getType() != Material.OAK_SIGN && targetBlock.getType() != Material.BARRIER) return null;
        if (onlyCash) if (targetBlock.getType() == Material.BARREL) return null;
        
        Shop shop = ShopManager.getShopAt(targetBlock.getLocation());
        if (shop == null) return null;
        return shop.getOwnerUUID();
    }

    /**
     * requirement : item need the uuid of the player who restock the shop

     * quand un item est vendu un partie du profit reviens a celui qui a approvisionner
     */
    public void checkStock() {
        Multiblock multiblock = ShopManager.getMultiblock(ownerUUID);

        if (multiblock == null) {
            return;
        }

        Block stockBlock = multiblock.stockBlock().getBlock();
        if (stockBlock.getType() != Material.BARREL) {
            ShopManager.removeShop(this);
            return;
        }

        if (stockBlock.getState(false) instanceof Barrel barrel) {

            Inventory inventory = barrel.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }
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
     * remove an item from the shop
     *
     * @param item the item to remove
     */
    public void removeItem(ShopItem item) {
        items.remove(item);
        suppliers.entrySet().removeIf(entry -> entry.getValue().getItemId().equals(item.getItemID()));
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

    public int recoverItemOf(ShopItem item, Player supplier) {
        int amount = item.getAmount();

        if (ItemUtils.getFreePlacesForItem(supplier,item.getItem()) < amount){
            MessagesManager.sendMessage(supplier, Component.text("§cVous n'avez pas assez de place"), Prefix.SHOP, MessageType.INFO, false);
            return 0;
        }

        int toRemove = 0;

        Iterator<Map.Entry<Long, Supply>> iterator = suppliers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Supply> entry = iterator.next();
            
            if (! entry.getValue().getSupplierUUID().equals(supplier.getUniqueId())) continue;
            if (! entry.getValue().getItemId().equals(item.getItemID())) continue;
            
            amount -= entry.getValue().getAmount();
            toRemove += entry.getValue().getAmount();
            
            if (amount < 0) {
                break;
            } else {
                iterator.remove();
            }
        }
        
        if (amount == 0) {
            items.remove(item);
            MessagesManager.sendMessage(supplier, Component.text("§aL'item a bien été retiré du shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
        } else {
            item.setAmount(amount);
        }
        
        return toRemove;
    }

    public void addSupply(long time, Supply supply){
        suppliers.put(time, supply);
    }

    /**
     * update the amount of all the item in the shop according to the items in the barrel
     */
    public boolean supply(ItemStack item, UUID supplier) {
        for (ShopItem shopItem : items) {
            if (! shopItem.getItem().getType().equals(item.getType())) continue;
            
            int delay = 0;
            shopItem.setAmount(shopItem.getAmount() + item.getAmount());
            while (suppliers.containsKey(System.currentTimeMillis() + delay)) delay++;
            
            suppliers.put(System.currentTimeMillis() + delay, new Supply(supplier, shopItem.getItemID(), item.getAmount()));
            return true;
        }
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

    private void removeLatestSupply() {
        long latest = 0;
        Supply supply = null;
        for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
            if (entry.getKey() > latest) {
                latest = entry.getKey();
                supply = entry.getValue();
            }
        }
        if (supply == null) return;
        
        suppliers.remove(latest);
    }

    public boolean isSupplier(UUID playerUUID){
        for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
            if (entry.getValue().getSupplierUUID().equals(playerUUID)) return true;
        }
        return false;
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
                    Component.text("§7■ Chiffre d'affaire : " + EconomyManager.getFormattedNumber(turnover)),
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
