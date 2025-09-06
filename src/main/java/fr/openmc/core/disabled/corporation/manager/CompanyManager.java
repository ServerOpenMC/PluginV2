package fr.openmc.core.disabled.corporation.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.disabled.corporation.CorpPermission;
import fr.openmc.core.disabled.corporation.MethodState;
import fr.openmc.core.disabled.corporation.commands.CompanyCommand;
import fr.openmc.core.disabled.corporation.commands.ShopCommand;
import fr.openmc.core.disabled.corporation.company.Company;
import fr.openmc.core.disabled.corporation.company.CompanyOwner;
import fr.openmc.core.disabled.corporation.data.MerchantData;
import fr.openmc.core.disabled.corporation.listener.CustomItemsCompanyListener;
import fr.openmc.core.disabled.corporation.listener.ShopListener;
import fr.openmc.core.disabled.corporation.models.*;
import fr.openmc.core.disabled.corporation.shops.Shop;
import fr.openmc.core.disabled.corporation.shops.ShopItem;
import fr.openmc.core.disabled.corporation.shops.Supply;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.Queue;
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

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CompanyManager {

    // Liste de toutes les entreprises créées
    @Getter
    public static List<Company> companies = new ArrayList<>();
    @Getter
    public static List<Shop> shops = new ArrayList<>();

    public static final NamespacedKey SUPPLIER_KEY = new NamespacedKey(OMCPlugin.getInstance(), "supplier");

    // File d'attente des candidatures en attente, avec une limite de 100
    private static final Queue<UUID, Company> pendingApplications = new Queue<>(100);

    public CompanyManager() {
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("company_perms",
                ((args, sender, command) -> {
                    return Arrays.stream(CorpPermission.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                }));

        CommandsManager.getHandler().register(
                new CompanyCommand(),
                new ShopCommand());

        OMCPlugin.registerEvents(
                new ShopListener());

        if (ItemsAdderHook.hasItemAdder()) {
            OMCPlugin.registerEvents(
                    new CustomItemsCompanyListener());
        }

        companies = getAllCompany();
        shops = loadAllShops();
    }

    private static Dao<CompanyPermission, UUID> permissionsDao;
    private static Dao<DBShop, UUID> shopsDao;
    private static Dao<DBShopItem, UUID> itemsDao;
    private static Dao<DBShopSale, UUID> salesDao;
    private static Dao<DBCompany, UUID> companiesDao;
    private static Dao<CompanyMerchant, UUID> companyMerchantsDao;
    private static Dao<Merchant, UUID> merchantsDao;
    private static Dao<ShopSupplier, UUID> suppliersDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CompanyPermission.class);
        permissionsDao = DaoManager.createDao(connectionSource, CompanyPermission.class);

        TableUtils.createTableIfNotExists(connectionSource, DBShop.class);
        shopsDao = DaoManager.createDao(connectionSource, DBShop.class);

        TableUtils.createTableIfNotExists(connectionSource, DBShopSale.class);
        salesDao = DaoManager.createDao(connectionSource, DBShopSale.class);

        TableUtils.createTableIfNotExists(connectionSource, DBShopItem.class);
        itemsDao = DaoManager.createDao(connectionSource, DBShopItem.class);

        TableUtils.createTableIfNotExists(connectionSource, DBCompany.class);
        companiesDao = DaoManager.createDao(connectionSource, DBCompany.class);

        TableUtils.createTableIfNotExists(connectionSource, CompanyMerchant.class);
        companyMerchantsDao = DaoManager.createDao(connectionSource, CompanyMerchant.class);

        TableUtils.createTableIfNotExists(connectionSource, Merchant.class);
        merchantsDao = DaoManager.createDao(connectionSource, Merchant.class);

        TableUtils.createTableIfNotExists(connectionSource, ShopSupplier.class);
        suppliersDao = DaoManager.createDao(connectionSource, ShopSupplier.class);
    }

    public static List<Company> getAllCompany() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Loading companies...");
        List<Company> companies = new ArrayList<>();

        try {
            List<DBCompany> dbCompanies = companiesDao.queryForAll();
            for (DBCompany dbCompany : dbCompanies) {
                Company company = dbCompany.deserialize();

                QueryBuilder<CompanyMerchant, UUID> query = companyMerchantsDao.queryBuilder();
                query.where().eq("company", company.getCompany_uuid());
                List<CompanyMerchant> merchants = companyMerchantsDao.query(query.prepare());
                for (CompanyMerchant merchant : merchants) {
                    MerchantData merchantData = new MerchantData();
                    merchantData.addMoneyWon(merchant.getMoneyWon());

                    for (ItemStack item : getMerchantItem(merchant.getPlayer())) {
                        merchantData.depositItem(item);
                    }

                    company.addMerchant(merchant.getPlayer(), merchantData);
                }
                companies.add(company);
            }
            OMCPlugin.getInstance().getSLF4JLogger().info("Companies loaded successfully.");
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading companies from database: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return companies;
    }

    public static List<Shop> loadAllShops() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Loading shops...");
        Map<UUID, List<ShopItem>> shopItems = new HashMap<>();
        Map<UUID, List<ShopItem>> shopSales = new HashMap<>();
        List<Shop> allShop = new ArrayList<>();

        try {
            List<DBShopItem> dbShopItems = itemsDao.queryForAll();
            for (DBShopItem dbShopItem : dbShopItems) {
                byte[] itemBytes = dbShopItem.getItems();

                if (itemBytes == null)
                    continue;

                shopItems.computeIfAbsent(dbShopItem.getShop(), k -> new ArrayList<>()).add(dbShopItem.deserialize());
            }
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading shop items from database: {}", e.getMessage(), e);
        }

        try {
            List<DBShopSale> dbShopSales = salesDao.queryForAll();
            for (DBShopSale dbShopSale : dbShopSales) {
                byte[] itemBytes = dbShopSale.getItems();

                if (itemBytes == null)
                    continue;

                shopSales.computeIfAbsent(dbShopSale.getShop(), k -> new ArrayList<>()).add(dbShopSale.deserialize());
            }
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading shop sales from database: {}", e.getMessage(), e);
        }

        try {
            List<DBShop> dbShops = shopsDao.queryForAll();
            for (DBShop dbShop : dbShops) {
                Block barrel = new Location(Bukkit.getWorld("world"), dbShop.getX(), dbShop.getY(), dbShop.getZ())
                        .getBlock();
                Block cashRegister = new Location(Bukkit.getWorld("world"), dbShop.getX(), dbShop.getY() + 1,
                        dbShop.getZ()).getBlock();

                if (barrel.getType() != Material.BARREL)
                    continue;

                if (!cashRegister.getType().toString().contains("SIGN")
                        && !cashRegister.getType().equals(Material.BARRIER))
                    continue;

                Shop shop;
                if (dbShop.getCompany() == null) {
                    PlayerShopManager.createShop(dbShop.getOwner(), barrel, cashRegister, dbShop.getId());
                    shop = PlayerShopManager.getShopByUUID(dbShop.getId());
                } else {
                    Company company = getCompany(dbShop.getOwner());
                    if (dbShop.getCity() == null) {
                        company.createShop(barrel, cashRegister, dbShop.getId());
                    } else {
                        City city = CityManager.getCity(dbShop.getCity());
                        if (city != null) {
                            company.createShop(barrel, cashRegister, dbShop.getId());
                        }
                    }
                    shop = company.getShop(dbShop.getId());
                }
                if (shop == null || shopItems.get(dbShop.getId()) == null) {
                    continue;
                }

                if (!shopItems.isEmpty()) {
                    for (ShopItem shopItem : shopItems.get(shop.getUuid())) {
                        shop.addItem(shopItem);
                    }
                }

                if (!shopSales.isEmpty()) {
                    for (ShopItem shopItem : shopSales.get(shop.getUuid())) {
                        shop.addSales(shopItem);
                    }
                }

                allShop.add(shop);
            }
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading shops from database: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        try {
            List<ShopSupplier> suppliers = suppliersDao.queryForAll();
            for (ShopSupplier supplier : suppliers) {
                for (Shop shop : allShop) {
                    if (shop.getUuid().equals(supplier.getShop())) {
                        shop.addSupply(supplier.getTime(), new Supply(supplier.getPlayer(), supplier.getItem(),
                                supplier.getAmount(), supplier.getId()));
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading shop suppliers from database: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        OMCPlugin.getInstance().getSLF4JLogger().info("Shops loaded successfully.");

        return allShop;
    }

    @SneakyThrows
    public static void saveAllCompanies() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Saving company data...");

        try {
            ConnectionSource connectionSource = DatabaseManager.getConnectionSource();
            TableUtils.clearTable(connectionSource, DBCompany.class);
            TableUtils.clearTable(connectionSource, CompanyMerchant.class);
            TableUtils.clearTable(connectionSource, Merchant.class);
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error clearing company tables: {}", e.getMessage(), e);
        }

        List<DBCompany> dbCompanies = new ArrayList<>();
        List<CompanyMerchant> dbCompanyMerchants = new ArrayList<>();
        List<Merchant> dbMerchants = new ArrayList<>();

        for (Company company : companies) {
            dbCompanies.add(company.serialize());
            for (UUID merchantUuid : company.getMerchantsUUID()) {
                double moneyWon = company.getMerchant(merchantUuid).getMoneyWon();
                dbCompanyMerchants.add(new CompanyMerchant(merchantUuid, company.getCompany_uuid(), moneyWon));

                ItemStack[] items = company.getMerchants().get(merchantUuid).getDepositedItems()
                        .toArray(new ItemStack[0]);
                byte[] content = BukkitSerializer.serializeItemStacks(items);
                dbMerchants.add(new Merchant(merchantUuid, content));
            }
        }

        try {
            companiesDao.create(dbCompanies);
            companyMerchantsDao.create(dbCompanyMerchants);
            merchantsDao.create(dbMerchants);
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error saving company data: {}", e.getMessage(), e);
        }

        OMCPlugin.getInstance().getSLF4JLogger().info("Company data saved successfully.");
    }

    public static void saveAllShop() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Saving shop data...");

        try {
            ConnectionSource connectionSource = DatabaseManager.getConnectionSource();
            TableUtils.clearTable(connectionSource, DBShop.class);
            TableUtils.clearTable(connectionSource, DBShopItem.class);
            TableUtils.clearTable(connectionSource, DBShopSale.class);
            TableUtils.clearTable(connectionSource, ShopSupplier.class);
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error clearing shop tables: {}", e.getMessage(), e);
        }

        List<DBShop> dbShops = new ArrayList<>();
        List<DBShopItem> dbShopItems = new ArrayList<>();
        List<ShopSupplier> dbShopSuppliers = new ArrayList<>();
        List<DBShopSale> dbShopSales = new ArrayList<>();

        for (Company company : companies) {
            for (Shop shop : company.getShops()) {
                UUID companyId = company.getCompany_uuid();
                UUID cityUuid = null;
                if (company.getOwner().isCity()) {
                    cityUuid = company.getOwner().getCity().getUniqueId();
                }

                double x = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockX();
                double y = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockY();
                double z = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockZ();

                dbShops.add(new DBShop(shop.getUuid(), shop.getSupremeOwner(), cityUuid, companyId, x, y, z));

                for (ShopItem shopItem : shop.getItems()) {
                    byte[] item = shopItem.getItem().serializeAsBytes();
                    double price = shopItem.getPricePerItem();
                    int amount = shopItem.getAmount();

                    dbShopItems.add(new DBShopItem(item, shop.getUuid(), price, amount, shopItem.getItemID()));
                }

                for (Map.Entry<Long, Supply> entry : shop.getSuppliers().entrySet()) {
                    Supply supply = entry.getValue();
                    Long time = entry.getKey();
                    UUID uuid = supply.getSupplier();
                    UUID item_uuid = supply.getItemId();
                    UUID supplier_uuid = supply.getSupplierUUID();
                    int amount = supply.getAmount();

                    dbShopSuppliers.add(new ShopSupplier(uuid, shop.getUuid(), item_uuid, supplier_uuid, amount, time));
                }

                for (ShopItem shopItem : shop.getSales()) {
                    byte[] item = shopItem.getItem().serializeAsBytes();
                    double price = shopItem.getPricePerItem();
                    int amount = shopItem.getAmount();

                    dbShopSales.add(new DBShopSale(item, shop.getUuid(), price, amount, shopItem.getItemID()));
                }
            }
        }

        Map<UUID, Shop> playerShops = PlayerShopManager.getPlayerShops();
        if (playerShops == null)
            return;

        for (Map.Entry<UUID, Shop> entry : playerShops.entrySet()) {
            Shop shop = entry.getValue();
            UUID owner = entry.getKey();
            double x = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockX();
            double y = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockY();
            double z = ShopBlocksManager.getMultiblock(shop.getUuid()).stockBlock().getBlockZ();

            for (ShopItem shopItem : shop.getItems()) {
                byte[] item = shopItem.getItem().serializeAsBytes();
                double price = shopItem.getPricePerItem();
                int amount = shopItem.getAmount();

                dbShopItems.add(new DBShopItem(item, shop.getUuid(), price, amount, shopItem.getItemID()));
            }

            dbShops.add(new DBShop(shop.getUuid(), owner, null, null, x, y, z));
        }

        try {
            shopsDao.create(dbShops);
            itemsDao.create(dbShopItems);
            salesDao.create(dbShopSales);

            dbShopSuppliers.forEach(supplier -> {
                try {
                    suppliersDao.createOrUpdate(supplier);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error saving shop data: {}", e.getMessage(), e);
        }

        OMCPlugin.getInstance().getSLF4JLogger().info("Shop data saved successfully.");
    }

    public static Set<CorpPermission> getPermissions(Company company, UUID player) {
        Set<CorpPermission> permissions = new HashSet<>();
        try {
            UUID companyUuid = company.getOwner().getCity() == null ? company.getOwner().getPlayer() : company.getOwner().getCity().getUniqueId();
            QueryBuilder<CompanyPermission, UUID> query = permissionsDao.queryBuilder();
            query.where().eq("company", companyUuid).and().eq("player", player);

            List<CompanyPermission> perms = permissionsDao.query(query.prepare());
            for (CompanyPermission perm : perms) {
                try {
                    permissions.add(CorpPermission.valueOf(perm.getPermission()));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid permission found in databse: " + perm.getPermission());
                }
            }

            return permissions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void removePermissions(Company company, UUID player, CorpPermission permission) {
        try {
            UUID companyUuid = company.getOwner().getCity() == null ? company.getOwner().getPlayer()
                    : company.getOwner().getCity().getUniqueId();

            permissionsDao.delete(new CompanyPermission(companyUuid, player, permission.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPermissions(Company company, UUID player, CorpPermission permission) {
        try {
            UUID companyUuid = company.getOwner().getCity() == null ? company.getOwner().getPlayer()
                    : company.getOwner().getCity().getUniqueId();

            permissionsDao.create(new CompanyPermission(companyUuid, player, permission.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the items of a marchant from the database
     *
     * @param player the uuid of the player we check
     * @return An ItemStack[] from byte stock in the database
     */
    public static ItemStack[] getMerchantItem(UUID player) {
        try {
            byte[] content = merchantsDao.queryForId(player).getContent();
            return content != null ? BukkitSerializer.deserializeItemStacks(content) : new ItemStack[54];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ItemStack[54];
    }

    /**
     * create a new company
     *
     * @param name         the name of the company
     * @param owner        the owner of the company
     * @param newMember    use for the city company (not working for now)
     * @param company_uuid use to set the company uuid if it creates at the load of
     *                     the server
     */
    public static void createCompany(String name, CompanyOwner owner, boolean newMember, UUID company_uuid) {
        companies.add(new Company(name, owner, company_uuid, newMember));
    }

    /**
     * appling for a company
     *
     * @param playerUUID the uuid of the applier
     * @param company    the company where he wants to apply
     */
    public static void applyToCompany(UUID playerUUID, Company company) {
        Company playerCompany = getCompany(playerUUID);

        if (playerCompany != null)
            return;

        if (!pendingApplications.getQueue().containsKey(playerUUID)) {
            pendingApplications.add(playerUUID, company);
        }
    }

    /**
     * accept the application of a player
     *
     * @param playerUUID the uuid of the applier
     * @param company    the company which accept the player
     */
    public static void acceptApplication(UUID playerUUID, Company company) {
        company.addMerchant(playerUUID, new MerchantData());
        pendingApplications.remove(playerUUID);
    }

    /**
     * know if a player has a pending application for a company
     *
     * @param playerUUID the uuid of the player
     * @param company    the company
     * @return true if it has one
     */
    public static boolean hasPendingApplicationFor(UUID playerUUID, Company company) {
        return pendingApplications.get(playerUUID) == company;
    }

    /**
     * deny the application of a player
     *
     * @param playerUUID the uuid of the applier
     */
    public static void denyApplication(UUID playerUUID) {
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
    public static List<UUID> getPendingApplications(Company company) {
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
    public static boolean liquidateCompany(Company company) {
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
    public static void fireAllMerchants(Company company) {
        for (UUID uuid : company.getMerchants().keySet()) {
            company.fireMerchant(uuid);
        }
    }

    /**
     * get the application list of a company
     *
     * @param playerUUID the uuid of the player who wants to leave the company
     * @return A different MethodeState
     */
    public static MethodState leaveCompany(UUID playerUUID) {
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
    public static Company getCompany(String name) {
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
    public static Shop getAnyShop(UUID shopUUID) {
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
     * get a company by a city (not use now)
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
    public static boolean isInCompany(UUID playerUUID) {
        return getCompany(playerUUID) != null;
    }

    /**
     * know if a player is a merchant in a company
     *
     * @param playerUUID the uuid of the player we check
     * @param company    the company we check
     * @return true or false
     */
    public static boolean isMerchantOfCompany(UUID playerUUID, Company company) {
        return company.getMerchants().containsKey(playerUUID);
    }

    /**
     * know if a company exists by its name
     *
     * @param name the name uses for the check
     * @return true or false
     */
    public static boolean companyExists(String name) {
        return getCompany(name) != null;
    }
}
