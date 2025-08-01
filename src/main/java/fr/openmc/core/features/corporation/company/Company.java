package fr.openmc.core.features.corporation.company;

import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corporation.CorpPermission;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.data.TransactionData;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.corporation.models.DBCompany;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopOwner;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.Queue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class Company {

    private final String name;
    private final HashMap<UUID, Set<CorpPermission>> permsCache = new HashMap<>();
    private final Map<UUID, MerchantData> merchants = new HashMap<>();
    private final List<Shop> shops = new ArrayList<>();
    private final Queue<Long, TransactionData> transactions = new Queue<>(150);
    private final double turnover = 0;
    private CompanyOwner owner;
    private final UUID company_uuid;
    @Setter
    private double balance = 0;
    @Setter
    private double cut = 0.25;

    private int shopCounter = 0;

    /**
     * create a company
     *
     * @param name the name of the company
     * @param owner the owner
     * @param company_uuid the uuid of the company if it has one
     */
    public Company(String name, CompanyOwner owner, UUID company_uuid) {
        this.name = name;
        this.owner = owner;
        this.company_uuid = Objects.requireNonNullElseGet(company_uuid, UUID::randomUUID);

        addPermission(owner.getPlayer(), CorpPermission.OWNER);
        addMerchant(owner.getPlayer(), new MerchantData());
    }

    // a revoir je l'utilisais pour les entreprises de ville
    public Company(String name, CompanyOwner owner, UUID company_uuid, boolean newMember) {
        this.name = name;
        this.owner = owner;
        this.company_uuid = Objects.requireNonNullElseGet(company_uuid, UUID::randomUUID);

        if (owner.isCity() && newMember){
            City city = owner.getCity();
            if (city!=null){
                Company company = this;
                for (UUID member : city.getMembers()){
                    company.addMerchant(member, new MerchantData());
                }
            }
        }
        addPermission(owner.getPlayer(), CorpPermission.OWNER);
    }

    /**
     * Create a company object (use to deserialize to DBCompany)
     *
     * @param id The uuid of the company
     * @param name The name of the company
     * @param player The owner
     * @param city The city of the company (can be null)
     * @param cut The cut of the company
     * @param balance The balance of the company
     */
    public Company(UUID id, String name, UUID player, String city, double cut, double balance) {
        this.name = name;
        this.owner = city == null ? new CompanyOwner(player) : new CompanyOwner(CityManager.getCity(city));
        assert id != null;
        this.company_uuid = id;
        this.cut = cut;
        this.balance = balance;

        addPermission(owner.getPlayer(), CorpPermission.OWNER);
    }

    /**
     * convert {@link Company} to {@link fr.openmc.core.features.corporation.models.DBCompany} for database
     *
     * @return the company to be saved to the DB
     */
    public DBCompany serialize() {
        return new DBCompany(company_uuid, name, owner.getPlayer(), owner.getCity(), cut, balance);
    }

    /**
     * load permission in permsCache
     *
     * @param player the uuid of the player
     */
    private void loadPermission(UUID player) {
        if (!permsCache.containsKey(player)) {
            permsCache.put(player, CompanyManager.getPermissions(this, player));
        }
    }

    public Set<CorpPermission> getPermissions(UUID playerUUID) {
        loadPermission(playerUUID);
        return permsCache.get(playerUUID);
    }

    public boolean hasPermission(UUID playerUUID, CorpPermission permission) {
        loadPermission(playerUUID);
        Set<CorpPermission> playerPerms = permsCache.get(playerUUID);

        if (playerPerms.contains(CorpPermission.OWNER)) return true;

        return playerPerms.contains(permission);
    }

    public UUID getPlayerWith(CorpPermission permission) {
        for (UUID player: permsCache.keySet()) {
            if (permsCache.get(player).contains(permission)) {
                return player;
            }
        }
        return null;
    }

    /**
     * remove permission in permsCache and in db
     *
     * @param player the uuid of the player
     * @param permission the permission
     */
    public void removePermission(UUID player, CorpPermission permission) {
        loadPermission(player);
        Set<CorpPermission> playerPerms = permsCache.get(player);

        if (playerPerms == null) {
            return;
        }

        if (playerPerms.contains(permission)) {
            playerPerms.remove(permission);
            permsCache.put(player, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                CompanyManager.removePermissions(this, player, permission);
            });
        }
    }

    /**
     * add permission in permsCache and in db
     *
     * @param player the uuid of the player
     * @param permission the permission
     */
    public void addPermission(UUID player, CorpPermission permission) {
        Set<CorpPermission> playerPerms = permsCache.getOrDefault(player, new HashSet<>());

        if (!playerPerms.contains(permission)) {
            playerPerms.add(permission);
            permsCache.put(player, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                CompanyManager.addPermissions(this, player, permission);
            });
        }
    }

    public double getTurnover() {
        double turnover = 0;
        for (Shop shop : shops) turnover += shop.getTurnover();
        return turnover;
    }

    /**
     * get a shop in the company by its uuid
     *
     * @param uuid the uuid of the shop we check
     * @return A shop if found
     */
    public Shop getShop(UUID uuid) {
        for (Shop shop : shops) {
            if (shop.getUuid().equals(uuid)) {
                return shop;
            }
        }
        return null;
    }

    /**
     * know if the company has a shop by its uuid
     *
     * @param uuid the uuid of the shop we check
     * @return true or false
     */
    public boolean hasShop(UUID uuid){
        for (Shop shop : shops) {
            if (shop.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get a shop by its index
     *
     * @param shop the index use to find a shop
     * @return A shop if found
     */
    public Shop getShop(int shop) {
        for (Shop shopToGet : shops) {
            if (shopToGet.getIndex() == shop) {
                return shopToGet;
            }
        }
        return null;
    }

    /**
     * create a shop in the company
     *
     * @param playerUUID the uuid of the player who create the shop
     * @param barrel the stockage of the shop
     * @param cash the "cash register" use to open the shop menu
     * @return true or false
     */
    public boolean createShop(UUID playerUUID, Block barrel, Block cash) {
        Player whoCreated = Bukkit.getPlayer(playerUUID);
        Company company = this;

        if (whoCreated != null && withdraw(100, whoCreated, "Création de shop")) {
            if (!company.hasPermission(playerUUID, CorpPermission.CREATESHOP)){
                return false;
            }

            Shop newShop = new Shop(new ShopOwner(this), shopCounter);
            EconomyManager.withdrawBalance(whoCreated.getUniqueId(), 100);

            shops.add(newShop);
            CompanyManager.shops.add(newShop);

            ShopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cash.getLocation()));
            ShopBlocksManager.placeShop(newShop, whoCreated, true);

            shopCounter++;
            return true;
        }
        return false;
    }

    /**
     * create a shop in the company without player ( use during database load )
     *
     * @param barrel the stockage of the shop
     * @param cash the "cash register" use to open the shop menu
     * @param shopUUID the uuid of the shop
     */

    public void createShop(Block barrel, Block cash, UUID shopUUID) {
        Shop newShop = new Shop(new ShopOwner(this), shopCounter, shopUUID);
        ShopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cash.getLocation()));
        shopCounter++;
        shops.add(newShop);
        CompanyManager.shops.add(newShop);
    }

        /**
         * delete a shop in the company
         *
         * @param player the player who earn the money
         * @param uuid the shop uuid
         * @return true or false
         */
    public MethodState deleteShop(Player player, UUID uuid) {
        for (Shop shop : shops) {
            if (shop.getUuid().equals(uuid)) {
                if (!shop.getItems().isEmpty()) {
                    return MethodState.WARNING;
                }
                if (!deposit(75, player, "Suppression de shop")) {
                    return MethodState.SPECIAL;
                }
                if (!ShopBlocksManager.removeShop(shop)) {
                    return MethodState.ESCAPE;
                }
                shops.remove(shop);
                CompanyManager.shops.remove(shop);
                EconomyManager.addBalance(player.getUniqueId(), 75);
                return MethodState.SUCCESS;
            }
        }
        return MethodState.ERROR;
    }

    /**
     * get all members of the company
     *
     * @return A list of all the members uuid
     */
    public List<UUID> getAllMembers() {
        List<UUID> members = new ArrayList<>();
        if (owner.isPlayer()) {
            members.add(owner.getPlayer());
        }
        else {
            members.addAll(owner.getCity().getMembers());
        }
        members.addAll(merchants.keySet());
        return members;
    }

    /**
     * get all merchants of teh company
     *
     * @return A list of all the merchants uuid
     */
    public List<UUID> getMerchantsUUID() {
        return new ArrayList<>(merchants.keySet());
    }

    /**
     * get the merchant by an uuid
     *
     * @param uuid the uuid of the merchant we check
     * @return A MerchantData if found
     */
    public MerchantData getMerchant(UUID uuid) {
        return merchants.get(uuid);
    }

    /**
     * add a merchant in the company
     *
     * @param uuid the uuid of the merchant
     * @param data the data of the merchant
     */
    public void addMerchant(UUID uuid, MerchantData data) {
        merchants.put(uuid, data);
    }

    /**
     * fire a merchant of the coppany
     *
     * @param uuid the merchant uuid
     */
    public void fireMerchant(UUID uuid) {
        removeMerchant(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            MessagesManager.sendMessage(player, Component.text("§cVous avez été renvoyé de l'entreprise §6§l" + name), Prefix.ENTREPRISE, MessageType.INFO, false);
        }
    }

    public void removeMerchant(UUID uuid) {
        merchants.remove(uuid);
    }

    /**
     * send a message at the company owner
     *
     * @param message the message
     */
    public void broadCastOwner(String message) {
        if (owner.isPlayer()) {
            Player player = Bukkit.getPlayer(owner.getPlayer());
            if (player != null) player.sendMessage(message);
        }
        else {
            for (UUID uuid : owner.getCity().getMembers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                player.sendMessage(message);
            }
        }
    }

    /**
     * know if an uuid is the owner of the company
     *
     * @param uuid the uuid we check
     * @return true or false
     */
    public boolean isOwner(UUID uuid) {
        if (owner.isPlayer()) {
            return owner.getPlayer().equals(uuid);
        }
        else {
            return owner.getCity().getMembers().contains(uuid);
        }
    }

    /**
     * know if an uuid is the unique owner of the company
     *
     * @param uuid the uuid we check
     * @return true or false
     */
    public boolean isUniqueOwner(UUID uuid) {
        if (owner.isPlayer()) {
            return owner.getPlayer().equals(uuid);
        }
        else {
            return owner.getCity().getPlayerWithPermission(CPermission.OWNER).equals(uuid);
        }
    }

    /**
     * konw if the uuid is in the company
     *
     * @param uuid the uuid we check
     * @return true or false
     */
    public boolean isIn(UUID uuid) {
        if (merchants.containsKey(uuid)) {
            return true;
        }
        return isOwner(uuid);
    }

    /**
     * set a new owner
     *
     * @param uuid the uuid of the new owner
     */
    public void setOwner(UUID uuid) {
        removePermission(owner.getPlayer(), CorpPermission.OWNER);
        owner = new CompanyOwner(uuid);
        addPermission(owner.getPlayer(), CorpPermission.OWNER);
    }

    public ItemStack getHead() {
        if (owner.isPlayer()) {
            return ItemUtils.getPlayerSkull(owner.getPlayer());
        }
        else {
            return ItemUtils.getPlayerSkull(owner.getCity().getPlayerWithPermission(CPermission.OWNER));
        }
    }

    public boolean withdraw(double amount, Player player, String nature) {
        return withdraw(amount, player, nature, "");
    }

    public boolean withdraw(double amount, Player player, String nature, String additionalInfo) {
        if (balance >= amount) {
            balance -= amount;
            if (amount > 0) {
                TransactionData transaction = new TransactionData(-amount, nature, additionalInfo, player.getUniqueId());
                transactions.add(System.currentTimeMillis(), transaction);
                EconomyManager.addBalance(player.getUniqueId(), amount);
            }
            return true;
        }
        return false;
    }

    public void depositWithoutWithdraw(double amount, Player player, String nature, String additionalInfo){
        balance += amount;
        if (amount > 0) {
            TransactionData transaction = new TransactionData(amount, nature, additionalInfo, player.getUniqueId());
            transactions.add(System.currentTimeMillis(), transaction);
        }
    }

    public boolean deposit(double amount, Player player, String nature) {
        return deposit(amount, player, nature, "");
    }

    public boolean deposit(double amount, Player player, String nature, String additionalInfo) {
        if (EconomyManager.withdrawBalance(player.getUniqueId(), amount)) {
            balance += amount;
            if (amount > 0) {
                TransactionData transaction = new TransactionData(amount, nature, additionalInfo, player.getUniqueId());
                transactions.add(System.currentTimeMillis(), transaction);
            }
            return true;
        }
        return false;
    }

}
