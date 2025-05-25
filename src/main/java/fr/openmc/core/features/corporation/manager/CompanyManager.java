package fr.openmc.core.features.corporation.manager;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corporation.CorpPermission;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.corporation.commands.CompanyCommand;
import fr.openmc.core.features.corporation.commands.ShopCommand;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.company.CompanyOwner;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.listener.CustomItemsCompanyListener;
import fr.openmc.core.features.corporation.listener.ShopListener;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.features.corporation.shops.ShopItem;
import fr.openmc.core.features.corporation.shops.Supply;
import fr.openmc.core.utils.Queue;
import fr.openmc.core.utils.api.ItemAdderApi;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CompanyManager {

    @Getter static CompanyManager instance;

    // Liste de toutes les entreprises créées
    @Getter public static List<Company> companies = new ArrayList<>();
    @Getter public static List<Shop> shops = new ArrayList<>();

    public static NamespacedKey SUPPLIER_KEY;

    // File d'attente des candidatures en attente, avec une limite de 100
    private final Queue<UUID, Company> pendingApplications = new Queue<>(100);

    public CompanyManager () {
        instance = this;

        /* KEY */
        SUPPLIER_KEY = new NamespacedKey(OMCPlugin.getInstance(), "supplier");

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("company_perms", ((args, sender, command) -> {
            return Arrays.stream(CorpPermission.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
        }));

        CommandsManager.getHandler().register(
                new CompanyCommand(),
                new ShopCommand()
        );

        OMCPlugin.registerEvents(
                new ShopListener()
        );

        if (ItemAdderApi.hasItemAdder()) {
            OMCPlugin.registerEvents(
                    new CustomItemsCompanyListener()
            );
        }

        companies = getAllCompany();
        shops = loadAllShops();
    }

    public static void init_db(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS company_perms (company_uuid VARCHAR(36) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();

            stmt.addBatch("CREATE TABLE IF NOT EXISTS shops (" +
                    "shop_uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "owner VARCHAR(36), " +
                    "city_uuid VARCHAR(36), " +
                    "company_uuid VARCHAR(36), " +
                    "x MEDIUMINT NOT NULL, " +
                    "y MEDIUMINT NOT NULL, " +
                    "z MEDIUMINT NOT NULL)");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS shops_item (" +
                    "item LONGBLOB NOT NULL, " +
                    "shop_uuid VARCHAR(36) NOT NULL, " +
                    "price DOUBLE NOT NULL, " +
                    "amount INT NOT NULL, " +
                    "PRIMARY KEY (shop_uuid, item(255)))");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS company (" +
                    "company_uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "owner VARCHAR(36), " +
                    "cut DOUBLE NOT NULL, " +
                    "balance DOUBLE NOT NULL, " +
                    "city_uuid VARCHAR(36))");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS company_merchants (" +
                    "company_uuid VARCHAR(36), " +
                    "player VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "moneyWon DOUBLE NOT NULL DEFAULT 0)");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS merchants_data (" +
                    "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "content LONGBLOB)");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS shop_supplier (" +
                    "time LONG DEFAULT 0, " +
                    "uuid VARCHAR(36) NOT NULL , " + // uuid du joueur
                    "item_uuid VARCHAR(36) NOT NULL, " + // uuid de l'item qu'il a mis en stock
                    "shop_uuid VARCHAR(36) NOT NULL, " + // uuid du shop pour retrouver son shop
                    "supplier_uuid VARCHAR(36) NOT NULL PRIMARY KEY, " + // uuid pour différencier tous les supply ( car il peut avoir plusieurs fois l'uuid d'un joueur )
                    "amount INT DEFAULT 0)");

            stmt.executeBatch();
        }
    }

    public static List<Company> getAllCompany() {
        OMCPlugin.getInstance().getLogger().info("Chargement des Companies...");
        List<Company> companies = new ArrayList<>();

        String query = "SELECT company_uuid, name, owner, cut, balance, city_uuid FROM company";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String company_uuid = rs.getString("company_uuid");
                String cityUuid = rs.getString("city_uuid");
                UUID owner = UUID.fromString(rs.getString("owner"));
                String name = rs.getString("name");
                double cut = rs.getDouble("cut");
                double balance = rs.getDouble("balance");

                Company company = (cityUuid == null)
                        ? new Company(name, new CompanyOwner(owner), UUID.fromString(company_uuid))
                        : new Company(name, new CompanyOwner(CityManager.getCity(cityUuid)), UUID.fromString(company_uuid));

                company.setCut(cut);
                company.setBalance(balance);

                String merchantQuery = "SELECT player, moneyWon FROM company_merchants WHERE company_uuid = ?";
                try (PreparedStatement merchantStmt = conn.prepareStatement(merchantQuery)) {

                    merchantStmt.setString(1, company_uuid);
                    ResultSet merchantRs = merchantStmt.executeQuery();

                    while (merchantRs.next()) {
                        UUID playerUuid = UUID.fromString(merchantRs.getString("player"));
                        double moneyWon = merchantRs.getDouble("moneyWon");

                        MerchantData merchantData = new MerchantData();
                        merchantData.addMoneyWon(moneyWon);

                        for (ItemStack item : getMerchantItem(playerUuid, conn)) {
                            merchantData.depositItem(item);
                        }

                        company.addMerchant(playerUuid, merchantData);
                    }
                }
                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OMCPlugin.getInstance().getLogger().info("Chargement terminé avec succes");
        return companies;
    }

    public static List<Shop> loadAllShops() {
        OMCPlugin.getInstance().getLogger().info("Chargement des Shops...");
        Map<UUID, List<ShopItem>> shopItems = new HashMap<>();
        List<Shop> allShop = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT item, shop_uuid, price, amount FROM shops_item");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UUID shopUuid = UUID.fromString(rs.getString("shop_uuid"));
                double price = rs.getDouble("price");
                int amount = rs.getInt("amount");
                byte[] itemBytes = rs.getBytes("item");

                if (itemBytes != null) {
                    ItemStack itemStack = ItemStack.deserializeBytes(itemBytes);
                    ShopItem shopItem = new ShopItem(itemStack, price);
                    shopItem.setAmount(amount);

                    shopItems.computeIfAbsent(shopUuid, k -> new ArrayList<>()).add(shopItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT shop_uuid, owner, city_uuid, company_uuid, x, y, z FROM shops");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UUID shopUuid = UUID.fromString(rs.getString("shop_uuid"));
                UUID owner = UUID.fromString(rs.getString("owner"));
                String cityUuid = rs.getString("city_uuid");
                String uuid = rs.getString("company_uuid");
                UUID company_uuid = null;
                if (uuid!=null){
                    company_uuid = UUID.fromString(uuid);
                }
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");


                Block barrel = new Location(Bukkit.getWorld("world"), x, y, z).getBlock();
                Block cashRegister = new Location(Bukkit.getWorld("world"), x, y + 1, z).getBlock();

                if (barrel.getType() == Material.BARREL) {
                    if (cashRegister.getType().toString().contains("SIGN") || cashRegister.getType().equals(Material.BARRIER)){
                        Shop shop;
                        if (company_uuid == null) {
                            PlayerShopManager.getInstance().createShop(owner, barrel, cashRegister, shopUuid);
                            shop = PlayerShopManager.getInstance().getShopByUUID(shopUuid);
                        } else {
                            Company company = getCompany(owner);
                            if (cityUuid==null){
                                company.createShop(owner, barrel, cashRegister, shopUuid);
                            } else {
                                City city = CityManager.getCity(cityUuid);
                                if (city != null) {
                                    company.createShop(owner, barrel, cashRegister, shopUuid);
                                }
                            }
                            shop = company.getShop(shopUuid);
                        }
                        if (shop == null || shopItems.get(shopUuid)==null) {
                            continue;
                        }
                        for (ShopItem shopItem : shopItems.get(shopUuid)) {
                            shop.addItem(shopItem.getItem(), shopItem.getPricePerItem(), shopItem.getAmount());
                        }

                        allShop.add(shop);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT time, uuid, item_uuid, shop_uuid, supplier_uuid, amount FROM shop_supplier");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                long time = rs.getLong("time");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                UUID item_uuid = UUID.fromString(rs.getString("item_uuid"));
                UUID shop_uuid = UUID.fromString(rs.getString("shop_uuid"));
                UUID supplier_uuid = UUID.fromString(rs.getString("supplier_uuid"));
                int amount = rs.getInt("amount");

                for (Shop shop : allShop){
                    if (shop.getUuid().equals(shop_uuid)){
                        shop.getSuppliers().put(time, new Supply(uuid, item_uuid, amount, supplier_uuid));
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        OMCPlugin.getInstance().getLogger().info("Chargement terminé avec succes");

        return allShop;
    }

    @SneakyThrows
    public static void saveAllCompanies() {
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des Companies...");

        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {

            stmt.executeUpdate("TRUNCATE TABLE company;");
            stmt.executeUpdate("TRUNCATE TABLE company_merchants;");
            stmt.executeUpdate("TRUNCATE TABLE merchants_data;");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String queryCompany = "INSERT INTO company (company_uuid, name, owner, cut, balance, city_uuid) VALUES (?, ?, ?, ?, ?, ?)";
        String queryMerchant = "INSERT INTO company_merchants (company_uuid, player, moneyWon) VALUES (?, ?, ?)";
        String queryMerchantData = "INSERT INTO merchants_data (uuid, content) VALUES (?, ?)";

        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmtCompany = conn.prepareStatement(queryCompany);
                PreparedStatement stmtMerchant = conn.prepareStatement(queryMerchant);
                PreparedStatement stmtMerchantData = conn.prepareStatement(queryMerchantData)
        ) {
            for (Company company : companies) {
                City city = company.getOwner().getCity();
                String cityUuid = city == null ? null : city.getUUID();
                String company_uuid = company.getCompany_uuid().toString();
                String name = company.getName();
                UUID owner = city == null ? company.getOwner().getPlayer() : city.getPlayerWith(CPermission.OWNER);
                double cut = company.getCut();
                double balance = company.getBalance();

                stmtCompany.setString(1, company_uuid);
                stmtCompany.setString(2, name);
                stmtCompany.setString(3, owner.toString());
                stmtCompany.setDouble(4, cut);
                stmtCompany.setDouble(5, balance);
                stmtCompany.setString(6, cityUuid);
                stmtCompany.addBatch(); // Adding the company to batch

                for (UUID merchantUUID : company.getMerchantsUUID()) {
                    double moneyWon = company.getMerchant(merchantUUID).getMoneyWon();
                    stmtMerchant.setString(1, company_uuid);
                    stmtMerchant.setString(2, merchantUUID.toString());
                    stmtMerchant.setDouble(3, moneyWon);
                    stmtMerchant.addBatch(); // Adding merchant info to batch

                    ItemStack[] items = company.getMerchants().get(merchantUUID).getDepositedItems().toArray(new ItemStack[0]);
                    byte[] content = BukkitSerializer.serializeItemStacks(items);
                    stmtMerchantData.setString(1, merchantUUID.toString());
                    stmtMerchantData.setBytes(2, content);
                    stmtMerchantData.addBatch(); // Adding merchant data to batch
                }
            }
            stmtCompany.executeBatch();  // Execute batch for companies
            stmtMerchant.executeBatch();  // Execute batch for merchants
            stmtMerchantData.executeBatch();  // Execute batch for merchant data
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des Companies finie.");
    }

    public static void saveAllShop() {
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des Shops...");

        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {

            stmt.executeUpdate("TRUNCATE TABLE shops;");
            stmt.executeUpdate("TRUNCATE TABLE shops_item;");
            stmt.executeUpdate("TRUNCATE TABLE shop_supplier;");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Connection conn = DatabaseManager.getConnection();

        String queryShop = "INSERT INTO shops (shop_uuid, owner, city_uuid, company_uuid, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryShopItem = "INSERT INTO shops_item (item, shop_uuid, price, amount) VALUES (?, ?, ?, ?)";
        String queryShopSupplier = "INSERT INTO shop_supplier (time, uuid, item_uuid, shop_uuid, supplier_uuid, amount) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement stmtShop = conn.prepareStatement(queryShop);
                PreparedStatement stmtShopItem = conn.prepareStatement(queryShopItem);
                PreparedStatement stmtShopSupplier = conn.prepareStatement(queryShopSupplier)
        ) {

            for (Company company : companies) {
                for (Shop shop : company.getShops()) {
                    UUID shopUuid = shop.getUuid();
                    UUID owner = shop.getSupremeOwner();
                    String cityUuid = null;
                    UUID company_uuid = company.getCompany_uuid();

                    if (company.getOwner().isCity()) {
                        cityUuid = company.getOwner().getCity().getUUID();
                    }

                    double x = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockX();
                    double y = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockY();
                    double z = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockZ();

                    for (ShopItem shopItem : shop.getItems()) {
                        byte[] item = shopItem.getItem().serializeAsBytes();
                        double price = shopItem.getPricePerItem();
                        int amount = shopItem.getAmount();

                        stmtShopItem.setBytes(1, item);
                        stmtShopItem.setString(2, shopUuid.toString());
                        stmtShopItem.setDouble(3, price);
                        stmtShopItem.setInt(4, amount);
                        stmtShopItem.addBatch();
                    }

                    stmtShop.setString(1, shopUuid.toString());
                    stmtShop.setString(2, owner.toString());
                    stmtShop.setString(3, cityUuid);
                    stmtShop.setString(4, company_uuid.toString());
                    stmtShop.setDouble(5, x);
                    stmtShop.setDouble(6, y);
                    stmtShop.setDouble(7, z);
                    stmtShop.addBatch();

                    for (Map.Entry<Long, Supply> entry : shop.getSuppliers().entrySet()) {
                        Supply supply = entry.getValue();
                        Long time = entry.getKey();
                        UUID uuid = supply.getSupplier();
                        UUID item_uuid = supply.getItemId();
                        UUID supplier_uuid = supply.getSupplierUUID();
                        int amount = supply.getAmount();

                        stmtShopSupplier.setLong(1, time);
                        stmtShopSupplier.setString(2, uuid.toString());
                        stmtShopSupplier.setString(3, item_uuid.toString());
                        stmtShopSupplier.setString(4, shopUuid.toString());
                        stmtShopSupplier.setString(5, supplier_uuid.toString());
                        stmtShopSupplier.setInt(6, amount);
                        stmtShopSupplier.addBatch();
                    }
                }
            }

            stmtShop.executeBatch();
            stmtShopItem.executeBatch();
            stmtShopSupplier.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (
                PreparedStatement stmtShop = conn.prepareStatement(queryShop);
                PreparedStatement stmtShopItem = conn.prepareStatement(queryShopItem)
        ) {
            PlayerShopManager manager = PlayerShopManager.getInstance();
            if (manager != null) {
                Map<UUID, Shop> playerShops = manager.getPlayerShops();
                if (playerShops != null) {
                    for (Map.Entry<UUID, Shop> entry : playerShops.entrySet()) {
                        Shop shop = entry.getValue();
                        UUID shopUuid = shop.getUuid();
                        UUID owner = entry.getKey();
                        double x = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockX();
                        double y = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockY();
                        double z = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockZ();

                        for (ShopItem shopItem : shop.getItems()) {
                            byte[] item = shopItem.getItem().serializeAsBytes();
                            double price = shopItem.getPricePerItem();
                            int amount = shopItem.getAmount();

                            stmtShopItem.setBytes(1, item);
                            stmtShopItem.setString(2, shopUuid.toString());
                            stmtShopItem.setDouble(3, price);
                            stmtShopItem.setInt(4, amount);
                            stmtShopItem.addBatch();
                        }

                        stmtShop.setString(1, shopUuid.toString());
                        stmtShop.setString(2, owner.toString());
                        stmtShop.setString(3, null);
                        stmtShop.setString(4, null);
                        stmtShop.setDouble(5, x);
                        stmtShop.setDouble(6, y);
                        stmtShop.setDouble(7, z);
                        stmtShop.addBatch();  // Adding shop to batch
                    }
                }
            }

            stmtShop.executeBatch(); // Execute batch for shops
            stmtShopItem.executeBatch();
        } catch (SQLException e) {
           e.printStackTrace();
        }
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des Shops finie.");
    }

    /**
     * get the items of a marchant from the database
     *
     * @param playerUUID the uuid of the player we check
     * @param conn use to have the same connection
     * @return A ItemStack[] from bytes stock in the database
     */
    public static ItemStack[] getMerchantItem(UUID playerUUID, Connection conn) {
        String query = "SELECT content FROM merchants_data WHERE uuid = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, playerUUID.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    byte[] content = rs.getBytes("content");
                    return content != null ? BukkitSerializer.deserializeItemStacks(content) : new ItemStack[54];
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ItemStack[54];
    }

    /**
     * create a new company
     *
     * @param name the name of the company
     * @param owner the owner of the company
     * @param newMember use for the city company ( not working for now )
     * @param company_uuid use to set the company uuid if it's create at the load of the server
     */
    public void createCompany(String name, CompanyOwner owner, boolean newMember, UUID company_uuid) {
        companies.add(new Company(name, owner, company_uuid, newMember));
    }

    /**
     * appling for a company
     *
     * @param playerUUID the uuid of the applier
     * @param company the company where he wants to apply
     */
    public void applyToCompany(UUID playerUUID, Company company) {
        Company playerCompany = getCompany(playerUUID);

        if (playerCompany!=null) return;

        if (!pendingApplications.getQueue().containsKey(playerUUID)){
            pendingApplications.add(playerUUID, company);
        }
    }

    /**
     * accept the application of a player
     *
     * @param playerUUID the uuid of the applier
     * @param company the company which accept the player
     */
    public void acceptApplication(UUID playerUUID, Company company) {
        company.addMerchant(playerUUID, new MerchantData());
        pendingApplications.remove(playerUUID);
    }

    /**
     * know if a player has a pending application for a company
     *
     * @param playerUUID the uuid of the player
     * @param company the company
     * @return true if it has one
     */
    public boolean hasPendingApplicationFor(UUID playerUUID, Company company) {
        return pendingApplications.get(playerUUID) == company;
    }

    /**
     * deny the application of a player
     *
     * @param playerUUID the uuid of the applier
     */
    public void denyApplication(UUID playerUUID) {
        if (pendingApplications.getQueue().containsKey(playerUUID)) {
            pendingApplications.remove(playerUUID);
        }
    }

    /**
     * get the application list of a company
     *
     * @param company the company we check
     * @return A list of all the application
     */
    public List<UUID> getPendingApplications(Company company) {
        List<UUID> players = new ArrayList<>();
        for (UUID player : pendingApplications.getQueue().keySet()) {
            if (hasPendingApplicationFor(player, company)) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * liquidate / remove a company
     *
     * @param company the company we check
     * @return true or false
     */
    public boolean liquidateCompany(Company company) {
        // L'entreprise ne peut pas être liquidée si elle a encore des marchands
        if (!company.getMerchants().isEmpty()) {
            fireAllMerchants(company);
        }
        // L'entreprise ne peut pas être liquidée si elle a encore des fonds
        if (company.getBalance() > 0) {
            return false;
        }
        // L'entreprise ne peut pas être liquidée si elle possède encore des magasins
        if (!company.getShops().isEmpty()) {
            return false;
        }

        // Suppression de l'entreprise
        companies.remove(company);
        return true;
    }

    /**
     * remove a player for the company
     *
     * @param company the company we check
     */
    public void fireAllMerchants(Company company) {
        for (UUID uuid : company.getMerchants().keySet()) {
            company.fireMerchant(uuid);
        }
    }

    /**
     * get the application list of a company
     *
     * @param playerUUID the uuid of the player who want to leave the company
     * @return A different MethodeState
     */
    public MethodState leaveCompany(UUID playerUUID) {
        Company company = getCompany(playerUUID);

        if (company.isOwner(playerUUID)) {
            // Si le joueur est propriétaire et qu'il n'y a pas d'autres marchands
            if (company.getMerchants().isEmpty()) {
                if (company.isUniqueOwner(playerUUID)) {
                    if (!liquidateCompany(company)) {
                        return MethodState.WARNING;
                    }
                    return MethodState.SUCCESS;
                }
                return MethodState.SPECIAL;
            }
            return MethodState.FAILURE;
        }

        // Si ce n'est pas le propriétaire qui quitte, on supprime le marchand
        MerchantData data = company.getMerchant(playerUUID);
        company.removeMerchant(playerUUID);

        // Si plus aucun membre n'est présent après le départ, l'entreprise est liquidée
        if (company.getAllMembers().isEmpty()) {
            if (!liquidateCompany(company)) {
                company.addMerchant(playerUUID, data); // Annulation si liquidation impossible
                return MethodState.WARNING;
            }
        }
        return MethodState.SUCCESS;
    }

    /**
     * get the company by its name
     *
     * @param name the name we check
     * @return A company if found
     */
    public Company getCompany(String name) {
        for (Company company : companies) {
            if (company.getName().equals(name)) {
                return company;
            }
        }
        return null;
    }

    /**
     * get a shop by its uuid
     *
     * @param shopUUID the shop uuid use for the check
     * @return A shop if found
     */
    public Shop getAnyShop(UUID shopUUID) {
        for (Company company : companies) {
            Shop shop = company.getShop(shopUUID);
            if (shop != null) {
                return shop;
            }
        }
        return null;
    }

    /**
     * get a company by an uuid
     *
     * @param uuid the company uuid use for the check
     * @return A shop if found
     */
    public static Company getCompany(UUID uuid) {
        for (Company company : companies) {
            if (company.getMerchants().containsKey(uuid)) {
                return company;
            }
            CompanyOwner owner = company.getOwner();
            if (owner.isPlayer() && owner.getPlayer().equals(uuid)) {
                return company;
            }
            if (owner.isCity() && owner.getCity().getMembers().contains(uuid)) {
                return company;
            }
        }
        return null;
    }

    /**
     * get a company by a city ( not use now )
     *
     * @param city the city us for the check
     * @return A company if found
     */
    public static Company getCompany(City city) {
        for (Company company : companies) {
            if (company.getOwner().getCity() != null && company.getOwner().getCity().equals(city)) {
                return company;
            }
        }
        return null;
    }

    /**
     * know if a player has a company
     *
     * @param playerUUID the uuid of the player we check
     * @return true or false
     */
    public boolean isInCompany(UUID playerUUID) {
        return getCompany(playerUUID) != null;
    }

    /**
     * know if a player is a merchant in a company
     *
     * @param playerUUID the uuid of the player we check
     * @param company the company we check
     * @return true or false
     */
    public boolean isMerchantOfCompany(UUID playerUUID, Company company) {
        return company.getMerchants().containsKey(playerUUID);
    }

    /**
     * know if a company exist by its name
     *
     * @param name the name use for the check
     * @return true or false
     */
    public boolean companyExists(String name) {
        return getCompany(name) != null;
    }
}
