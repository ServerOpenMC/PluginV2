package fr.openmc.core.features.corporation.company;


import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.corporation.CorpPermission;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.data.TransactionData;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopOwner;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.Queue;
import fr.openmc.core.utils.database.DatabaseManager;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class Company {

    private final String name;
    private final ShopBlocksManager shopBlocksManager = ShopBlocksManager.getInstance();
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
     * create a company object (use to serliaze to DBCompany)
     *
     * @param name the name of the company
     * @param owner the owner
     * @param company_uuid the uuid of the company if it has one
     */
    public Company(UUID id, String name, UUID player, UUID city, double cut, double balance) {
        this.name = name;
        this.owner = city == null ? new CompanyOwner(player) : new CompanyOwner(CityManager.getCity(city));
        assert company_uuid != null;
        this.company_uuid = id;
        this.cut = cut;
        this.balance = balance;

        addPermission(owner.getPlayer(), CorpPermission.OWNER);
    }

    /**
     * load permission in permsCache
     *
     * @param playerUUID the uuid of the player
     */
    private void loadPermission(UUID playerUUID) {
        if (!permsCache.containsKey(playerUUID)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM company_perms WHERE company_uuid = ? AND player = ?");
                statement.setString(1, owner.getCity() == null ? owner.getPlayer().toString() : owner.getCity().getUUID());
                statement.setString(2, playerUUID.toString());
                ResultSet rs = statement.executeQuery();

                Set<CorpPermission> plrPerms = permsCache.getOrDefault(playerUUID, new HashSet<>());

                while (rs.next()) {
                    try {
                        plrPerms.add(CorpPermission.valueOf(rs.getString("permission")));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid permission: " + rs.getString("permission"));
                    }
                }

                permsCache.put(playerUUID, plrPerms);
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
     * @param uuid the uuid of the player
     * @param permission the permission
     */
    public void removePermission(UUID uuid, CorpPermission permission) {
        loadPermission(uuid);
        Set<CorpPermission> playerPerms = permsCache.get(uuid);

        if (playerPerms == null) {
            return;
        }

        if (playerPerms.contains(permission)) {
            playerPerms.remove(permission);
            permsCache.put(uuid, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM company_perms WHERE company_uuid = ? AND player = ? AND permission = ?");
                    statement.setString(1, owner.getCity() == null ? owner.getPlayer().toString() : owner.getCity().getUUID());
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * add permission in permsCache and in db
     *
     * @param uuid the uuid of the player
     * @param permission the permission
     */
    public void addPermission(UUID uuid, CorpPermission permission) {
        Set<CorpPermission> playerPerms = permsCache.getOrDefault(uuid, new HashSet<>());

        if (!playerPerms.contains(permission)) {
            playerPerms.add(permission);
            permsCache.put(uuid, playerPerms);

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO company_perms (company_uuid, player, permission) VALUES (?, ?, ?)");
                    statement.setString(1, company_uuid.toString());
                    statement.setString(2, uuid.toString());
                    statement.setString(3, permission.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
     * @param shopUUID the uuid of a shop use at the start of the server
     * @return true or false
     */
    public boolean createShop(UUID playerUUID, Block barrel, Block cash, UUID shopUUID) {
        Player whoCreated = Bukkit.getPlayer(playerUUID);

        if (whoCreated==null && shopUUID != null){
            Shop newShop;
            newShop = new Shop(new ShopOwner(this), shopCounter, shopUUID);
            shopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cash.getLocation()));
            shopCounter++;
            return true;
        }

        Company company = CompanyManager.getCompany(playerUUID);

        if (whoCreated != null && withdraw(100, whoCreated, "Création de shop")) {
            if (company!=null && !company.hasPermission(playerUUID, CorpPermission.CREATESHOP)){
                return false;
            }

            Shop newShop;

            if (shopUUID==null){
                newShop = new Shop(new ShopOwner(this), shopCounter);
                EconomyManager.withdrawBalance(whoCreated.getUniqueId(), 100);
            } else {
                newShop = new Shop(new ShopOwner(this), shopCounter, shopUUID);
            }

                shops.add(newShop);
            CompanyManager.shops.add(newShop);
            shopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cash.getLocation()));

            if (shopUUID==null){
                shopBlocksManager.placeShop(newShop, whoCreated, true);
            }

            shopCounter++;
            return true;
        }
        return false;
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
                if (!shopBlocksManager.removeShop(shop)) {
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
            return owner.getCity().getPlayerWith(CPermission.OWNER).equals(uuid);
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
            return ItemUtils.getPlayerSkull(owner.getCity().getPlayerWith(CPermission.OWNER));
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
