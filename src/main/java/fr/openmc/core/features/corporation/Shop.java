package fr.openmc.core.features.corporation;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
public class Shop {

    private final ShopOwner owner;
    private final EconomyManager economyManager = EconomyManager.getInstance();
    private final ShopBlocksManager blocksManager = ShopBlocksManager.getInstance();
    private final List<ShopItem> items = new ArrayList<>();
    private final List<ShopItem> sales = new ArrayList<>();
    private final Map<Long, Supply> suppliers = new HashMap<>();
    private final int index;
    private final UUID uuid;

    private double turnover = 0;

    public Shop(ShopOwner owner, int index) {
        this.owner = owner;
        this.index = index;
        this.uuid  = UUID.randomUUID();
    }

    public Shop(ShopOwner owner, int index, UUID uuid) {
        this.owner = owner;
        this.index = index;
        this.uuid = uuid;
    }

    /**
     * @param shop the shop we want to check the stock
     * requirement : item need the uuid of the player who restock the shop
     *
     * quand un item est vendu un partie du profit reviens a celui qui a approvisionner
     */
    public static void checkStock(Shop shop) {
        ShopBlocksManager blocksManager = ShopBlocksManager.getInstance();
        Multiblock multiblock = blocksManager.getMultiblock(shop.getUuid());

        if (multiblock == null) {
            return;
        }

        Block stockBlock = multiblock.getStockBlock().getBlock();
        if (stockBlock.getType() != Material.BARREL) {
            blocksManager.removeShop(shop);
            return;
        }

        if (stockBlock.getState() instanceof Barrel barrel) {

            Inventory inventory = barrel.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }

                PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                if (dataContainer.has(OMCPlugin.SUPPLIER_KEY, PersistentDataType.STRING)) {

                    String supplierUUID = dataContainer.get(OMCPlugin.SUPPLIER_KEY, PersistentDataType.STRING);
                    if (supplierUUID == null) {
                        continue;
                    }

                    List<UUID> possibleSuppliers = new ArrayList<>();
                    if (shop.getOwner().isCompany()) {
                        possibleSuppliers.addAll(shop.getOwner().getCompany().getAllMembers());
                    }

                    if (shop.getOwner().isPlayer()) {
                        possibleSuppliers.add(shop.getOwner().getPlayer());
                    }

                    if (!possibleSuppliers.contains(UUID.fromString(supplierUUID))) {
                        continue;
                    }

                    boolean supplied = shop.supply(item, UUID.fromString(supplierUUID));
                    if (supplied) inventory.remove(item);
                }
            }
        }
    }


    public String getName() {
        return owner.isCompany() ? ("Shop #" + index) : Bukkit.getOfflinePlayer(owner.getPlayer()).getName() + "'s Shop";
    }

    public UUID getSupremeOwner() {
        return owner.isCompany() ? owner.getCompany().getOwner().getPlayer() : owner.getPlayer();
    }

    public boolean isOwner(UUID uuid) {
        if (owner.isCompany()) {
            return owner.getCompany().isOwner(uuid);
        }
        return owner.getPlayer().equals(uuid);
    }

    public boolean addItem(ItemStack itemStack, double price, int amount) {
        OMCPlugin.getInstance().getLogger().info("items bien detecter");
        ShopItem item = new ShopItem(itemStack, price);
        for (ShopItem shopItem : items) {
            if (shopItem.getItem().isSimilar(itemStack)) {
                return true;
            }
        }
        if (amount>1){
            item.setAmount(amount);
        }
        items.add(item);
        return false;
    }

    public ShopItem getItem(int index) {
        return items.get(index);
    }

    public void removeItem(ShopItem item) {
        items.remove(item);
    }

    /**
     * update the amount of all the item in the shop according to the items in the barrel
     */
    public boolean supply(ItemStack item, UUID supplier) {
        for (ShopItem shopItem : items) {
            if (shopItem.getItem().getType().equals(item.getType())) {
                shopItem.setAmount(shopItem.getAmount() + item.getAmount());
                suppliers.put(System.currentTimeMillis(), new Supply(supplier, shopItem.getItemID(), item.getAmount()));
                return true;
            }
        }
        return false;
    }

    public MethodState buy(ShopItem item, int amount, Player buyer) {
        if (!ItemUtils.hasAvailableSlot(buyer)) {
            return MethodState.SPECIAL;
        }
        if (amount > item.getAmount()) {
            return MethodState.WARNING;
        }
        if (isOwner(buyer.getUniqueId())) {
            return MethodState.FAILURE;
        }
        item.setAmount(item.getAmount() - amount);
        turnover += item.getPrice(amount);
        if (owner.isCompany()) {
            int amountToBuy = amount;
            double price = item.getPrice(amount);
            double companyCut = price * owner.getCompany().getCut();
            double suppliersCut = price - companyCut;
            boolean supplied = false;
            List<Supply> supplies = new ArrayList<>();
            for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
                if (entry.getValue().getItemId().equals(item.getItemID())) {
                    supplies.add(entry.getValue());
                }
            }
            if (!supplies.isEmpty()) {
                supplied = true;
                for (Supply supply : supplies) {
                    if (amountToBuy == 0) break;
                    if (amountToBuy >= supply.getAmount()) {
                        amountToBuy -= supply.getAmount();
                        removeLatestSupply();
                        double supplierCut = suppliersCut * ((double) supply.getAmount() / amount);
                        economyManager.addBalance(supply.getSupplier(), supplierCut);
                    }
                    else {
                        supply.setAmount(supply.getAmount() - amountToBuy);
                        double supplierCut = suppliersCut * ((double) amountToBuy / amount);
                        economyManager.addBalance(supply.getSupplier(), supplierCut);
                        break;
                    }
                }
            }
            if (!supplied) {
                return MethodState.ESCAPE;
            }
            owner.getCompany().deposit(companyCut, buyer, "Vente", getName());
        }
        else {
            if (!economyManager.withdrawBalance(buyer.getUniqueId(), item.getPrice(amount))) return MethodState.ERROR;
            economyManager.addBalance(owner.getPlayer(), item.getPrice(amount));
        }
        //TODO Give certain amount of that item to the buyer
        ItemStack toGive = item.getItem().clone();
        toGive.setAmount(amount);
        List<ItemStack> stacks = ItemUtils.splitAmountIntoStack(toGive);
        for (ItemStack stack : stacks) {
            buyer.getInventory().addItem(stack);
        }
        sales.add(item.copy().setAmount(amount));
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
        if (supply != null) {
            suppliers.remove(latest);
        }
    }

    public ItemBuilder getIcon(Menu menu, boolean fromShopMenu) {
        return new ItemBuilder(menu, fromShopMenu ? Material.GOLD_INGOT : Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName("§e§l" + (fromShopMenu ? "Informations" : getName()));
            List<String> lore = new ArrayList<>();
            lore.add("§7■ Chiffre d'affaires: " + EconomyManager.getInstance().getFormattedNumber(turnover) + "€");
            lore.add("§7■ Ventes: §f" + sales.size());
            if (!fromShopMenu)
                lore.add("§7■ Cliquez pour accéder au shop");
            itemMeta.setLore(lore);
        });
    }

    public int getAllItemsAmount() {
        int amount = 0;
        for (ShopItem item : items) {
            amount += item.getAmount();
        }
        return amount;
    }

    public static UUID getShopPlayerLookingAt(Player player, ShopBlocksManager shopBlocksManager, boolean onlyCash) {
        Block targetBlock = player.getTargetBlockExact(5);
        //TODO ItemsAdder cash register
        if (targetBlock == null || (targetBlock.getType() != Material.BARREL && targetBlock.getType() != Material.OAK_SIGN)) {
            return null;
        }
        if (onlyCash) {
            if (targetBlock.getType() != Material.OAK_SIGN) {
                return null;
            }
        }
        Shop shop = shopBlocksManager.getShop(targetBlock.getLocation());
        if (shop == null) {
            return null;
        }
        return shop.getUuid();
    }

    @Getter
    public static class Multiblock {

        private final Location stockBlock;
        private final Location cashBlock;

        public Multiblock(Location stockBlock, Location cashBlock) {
            this.stockBlock = stockBlock;
            this.cashBlock = cashBlock;
        }
    }
}