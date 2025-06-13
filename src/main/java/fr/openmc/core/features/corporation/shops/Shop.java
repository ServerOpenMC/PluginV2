package fr.openmc.core.features.corporation.shops;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.ItemUtils;
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
     * requirement : item need the uuid of the player who restock the shop

     * quand un item est vendu un partie du profit reviens a celui qui a approvisionner
     * @param shop the shop we want to check the stock
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
                if (dataContainer.has(CompanyManager.SUPPLIER_KEY, PersistentDataType.STRING)) {

                    String supplierUUID = dataContainer.get(CompanyManager.SUPPLIER_KEY, PersistentDataType.STRING);
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
        return owner.isCompany() ? ("Shop #" + index) : CacheOfflinePlayer.getOfflinePlayer(owner.getPlayer()).getName() + "'s Shop";
    }

    public UUID getSupremeOwner() {
        return owner.isCompany() ? owner.getCompany().getOwner().getPlayer() : owner.getPlayer();
    }

    /**
     * know if the uuid is the shop owner
     *
     * @param uuid the uuid we check
     */
    public boolean isOwner(UUID uuid) {
        if (owner.isCompany()) {
            return owner.getCompany().isOwner(uuid);
        }
        return owner.getPlayer().equals(uuid);
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

        Iterator<Map.Entry<Long, Supply>> iterator = suppliers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Supply> entry = iterator.next();
            if (entry.getValue().getItemId().equals(item.getItemID())) {
                iterator.remove();
            }
        }
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
            if (entry.getValue().getSupplierUUID().equals(supplier.getUniqueId())) {
                if (entry.getValue().getItemId().equals(item.getItemID())){
                    amount -= entry.getValue().getAmount();
                    toRemove += entry.getValue().getAmount();
                    if (amount >= 0){
                        iterator.remove();
                    }
                    else {
                        break;
                    }
                }
            }
        }

        if (amount == 0){
            items.remove(item);
            MessagesManager.sendMessage(supplier, Component.text("§aL'item a bien été retiré du shop !"), Prefix.SHOP, MessageType.SUCCESS, false);
        } else {
            item.setAmount(amount);
        }

        return toRemove;
    }

    /**
     * update the amount of all the item in the shop according to the items in the barrel
     */
    public boolean supply(ItemStack item, UUID supplier) {
        for (ShopItem shopItem : items) {
            if (shopItem.getItem().getType().equals(item.getType())) {
                int delay = 0;
                shopItem.setAmount(shopItem.getAmount() + item.getAmount());
                while (suppliers.containsKey(System.currentTimeMillis() + delay)){
                    delay ++;
                }
                suppliers.put(System.currentTimeMillis() + delay, new Supply(supplier, shopItem.getItemID(), item.getAmount()));
                return true;
            }
        }
        return false;
    }

    public void addSupply(long time, Supply supply){
        suppliers.put(time, supply);
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
        if (!ItemUtils.hasAvailableSlot(buyer)) {
            return MethodState.SPECIAL;
        }
        if (amountToBuy > item.getAmount()) {
            return MethodState.WARNING;
        }
        if (isOwner(buyer.getUniqueId())) {
            return MethodState.FAILURE;
        }
        if (!economyManager.withdrawBalance(buyer.getUniqueId(), item.getPrice(amountToBuy))) return MethodState.ERROR;

        double basePrice = item.getPrice(amountToBuy);
        turnover += item.getPrice(amountToBuy);

        if (owner.isCompany()) {

            double price = item.getPrice(amountToBuy);// prix total

            double companyCut = price * owner.getCompany().getCut();// prix après cut

            double suppliersCut = price - companyCut;// prix restant

            boolean supplied = false;

            List<Supply> supplies = new ArrayList<>();
            for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
                if (entry.getValue().getItemId().equals(item.getItemID())) {
                    supplies.add(entry.getValue());
                }
            }

            Map<UUID, Double> forSupplies = new HashMap<>();

            if (!supplies.isEmpty()) {

                supplied = true;

                for (Supply supply : supplies) {
                    int suppliesAmount = supply.getAmount();

                    if (amountToBuy == suppliesAmount){// si la quantité achetée correspond au suppliesAmount ( ex : 32 = 32 )
                        economyManager.addBalance(supply.getSupplier(), suppliersCut);// ajoutez prix restant

                        if (forSupplies.containsKey(supply.getSupplier())){
                            double supCut = forSupplies.get(supply.getSupplier());
                            forSupplies.replace(supply.getSupplier(), supCut + suppliersCut);
                        } else {
                            forSupplies.put(supply.getSupplier(), suppliersCut);
                        }
                        removeLatestSupply();// retirer le supplier
                        break;// arrêter la boucle
                    }

                    if (amountToBuy < suppliesAmount){// si la quantité achetée est inférieure au suppliesAmount ( ex : 32 < 64 )
                        economyManager.addBalance(supply.getSupplier(), suppliersCut);
                        suppliesAmount -= amountToBuy;
                        supply.setAmount(suppliesAmount);

                        if (forSupplies.containsKey(supply.getSupplier())){
                            double supCut = forSupplies.get(supply.getSupplier());
                            forSupplies.replace(supply.getSupplier(), supCut + suppliersCut);
                        } else {
                            forSupplies.put(supply.getSupplier(), suppliersCut);
                        }
                        break;
                    }

                    else {// si la quantité achetée est supérieur au suppliesAmount ( ex : 64 > 32 )
                        double supplierCut = (suppliesAmount * suppliersCut) / amountToBuy;
                        suppliersCut -= supplierCut;
                        amountToBuy -= suppliesAmount;
                        economyManager.addBalance(supply.getSupplier(), supplierCut);

                        if (forSupplies.containsKey(supply.getSupplier())){
                            double supCut = forSupplies.get(supply.getSupplier());
                            forSupplies.replace(supply.getSupplier(), supCut + supplierCut);
                        } else {
                            forSupplies.put(supply.getSupplier(), supplierCut);
                        }
                        removeLatestSupply();
                    }

                }
            }

            if (!supplied) {
                return MethodState.ESCAPE;
            }

            for (Map.Entry<UUID, Double> entry : forSupplies.entrySet()) {
                UUID supplier = entry.getKey();
                double supplierCut = entry.getValue();

                Player player = Bukkit.getPlayer(supplier);
                if (player!=null){
                    MessagesManager.sendMessage(player, Component.text(buyer.getName() + " a acheté " + amountToBuy + " " + item.getItem().getType() + " pour " + basePrice + EconomyManager.getEconomyIcon() + ", vous avez reçu : " + supplierCut + EconomyManager.getEconomyIcon()), Prefix.SHOP, MessageType.SUCCESS, false);
                }
            }

            owner.getCompany().depositWithoutWithdraw(companyCut, buyer, "Vente", getName());
        }

        else {
            economyManager.addBalance(owner.getPlayer(), item.getPrice(amountToBuy));
            Player player = Bukkit.getPlayer(owner.getPlayer());
            if (player!=null){
                MessagesManager.sendMessage(player, Component.text(buyer.getName() + " a acheté " + amountToBuy + " " + item.getItem().getType() + " pour " + item.getPrice(amountToBuy) + EconomyManager.getEconomyIcon() + ", l'argent vous a été transféré !"), Prefix.SHOP, MessageType.SUCCESS, false);
            }
        }

        ItemStack toGive = item.getItem().clone();
        toGive.setAmount(amountToBuy);

        List<ItemStack> stacks = ItemUtils.splitAmountIntoStack(toGive);
        for (ItemStack stack : stacks) {
            buyer.getInventory().addItem(stack);
        }

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
        if (supply != null) {
            suppliers.remove(latest);
        }
    }

    public boolean isSupplier(UUID playerUUID){
        for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
            if (entry.getValue().getSupplierUUID().equals(playerUUID)){
                return true;
            }
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
            itemMeta.setDisplayName("§e§l" + (fromShopMenu ? "Informations" : getName()));
            List<String> lore = new ArrayList<>();
            lore.add("§7■ Chiffre d'affaire : " + EconomyManager.getInstance().getFormattedNumber(turnover));
            lore.add("§7■ Ventes : §f" + sales.size());
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

    /**
     * get the shop with what player looking
     *
     * @param player the player we check
     * @param shopBlocksManager the permission
     * @param onlyCash if we only check the cach register
     */
    public static UUID getShopPlayerLookingAt(Player player, ShopBlocksManager shopBlocksManager, boolean onlyCash) {
        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) return null;

        if (targetBlock.getType() != Material.BARREL && targetBlock.getType() != Material.OAK_SIGN && targetBlock.getType() != Material.BARRIER) return null;
        if (onlyCash) {
            if (targetBlock.getType() != Material.OAK_SIGN && targetBlock.getType() != Material.BARRIER) return null;
        }
        Shop shop = shopBlocksManager.getShop(targetBlock.getLocation());
        if (shop == null) return null;
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