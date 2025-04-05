package fr.openmc.core.features.corporation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corporation.commands.CompanyCommand;
import fr.openmc.core.features.corporation.commands.ShopCommand;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.listener.ShopListener;
import fr.openmc.core.utils.Queue;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;

@Getter
public class CompanyManager {

    @Getter static CompanyManager instance;

    // Liste de toutes les entreprises créées
    @Getter public static List<Company> companies = new ArrayList<>();

    // File d'attente des candidatures en attente, avec une limite de 100
    private final Queue<UUID, Company> pendingApplications = new Queue<>(100);

    public CompanyManager () {
        instance = this;

        CommandsManager.getHandler().register(
                new CompanyCommand(),
                new ShopCommand()
        );

        OMCPlugin.registerEvents(
                new ShopListener()
        );

        companies = getAllCompany();
        loadAllShops();
    }

    public static void init_db(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS company_perms (company_uuid VARCHAR(36) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();

            stmt.addBatch("CREATE TABLE IF NOT EXISTS shops (" +
                    "shop_uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "owner VARCHAR(36), " +
                    "city_uuid VARCHAR(36), " +
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
                    "city_uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "owner VARCHAR(36), " +
                    "cut DOUBLE NOT NULL, " +
                    "balance DOUBLE NOT NULL)");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS company_merchants (" +
                    "city_uuid VARCHAR(36) NOT NULL, " +
                    "player VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "moneyWon DOUBLE NOT NULL)");

            stmt.addBatch("CREATE TABLE IF NOT EXISTS merchants_data (" +
                    "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "content LONGBLOB)");

            stmt.executeBatch();
        }
    }

    // Optimisation : Utilisation de try-with-resources et évite les connexions multiples
    public static List<Company> getAllCompany() {
        List<Company> companies = new ArrayList<>();

        String query = "SELECT city_uuid, name, owner, cut, balance FROM company";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String cityUuid = rs.getString("city_uuid");
                UUID owner = UUID.fromString(rs.getString("owner"));
                String name = rs.getString("name");
                double cut = rs.getDouble("cut");
                double balance = rs.getDouble("balance");

                Company company = (cityUuid == null)
                        ? new Company(name, new CompanyOwner(owner))
                        : new Company(name, new CompanyOwner(CityManager.getCity(cityUuid)));

                company.setCut(cut);
                company.setBalance(balance);

                String merchantQuery = "SELECT player, moneyWon FROM company_merchants WHERE city_uuid = ?";
                try (PreparedStatement merchantStmt = conn.prepareStatement(merchantQuery)) {
                    merchantStmt.setString(1, cityUuid);
                    try (ResultSet merchantRs = merchantStmt.executeQuery()) {
                        while (merchantRs.next()) {
                            UUID playerUuid = UUID.fromString(merchantRs.getString("player"));
                            double moneyWon = merchantRs.getDouble("moneyWon");

                            MerchantData merchantData = new MerchantData();
                            merchantData.addMoneyWon(moneyWon);

                            for (ItemStack item : getMerchantItem(playerUuid)) {
                                merchantData.depositItem(item);
                            }

                            company.addMerchant(playerUuid, merchantData);
                        }
                    }
                }
                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }

    public static void loadAllShops() {
        OMCPlugin.getInstance().getLogger().info("chargements des shops...");
        Map<UUID, List<ShopItem>> shopItems = new HashMap<>();

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
             PreparedStatement statement = conn.prepareStatement("SELECT shop_uuid, owner, city_uuid, x, y, z FROM shops");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UUID shopUuid = UUID.fromString(rs.getString("shop_uuid"));
                UUID owner = UUID.fromString(rs.getString("owner"));
                String cityUuid = rs.getString("city_uuid");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");


                Block barrel = new Location(Bukkit.getWorld("world"), x, y, z).getBlock();
                Block cashRegister = new Location(Bukkit.getWorld("world"), x, y + 1, z).getBlock();

                if (barrel.getType() == Material.BARREL && cashRegister.getType().toString().contains("SIGN")) {
                    Shop shop = null;
                    if (cityUuid == null) {
                        PlayerShopManager.getInstance().createShop(owner, barrel, cashRegister, shopUuid);
                        shop = PlayerShopManager.getInstance().getShopByUUID(shopUuid);
                    } else {
                        City city = CityManager.getCity(cityUuid);
                        if (city != null) {
                            Company company = CompanyManager.getInstance().getCompany(city);
                            company.createShop(owner, barrel, cashRegister, shopUuid);
                            shop = company.getShop(shopUuid);
                        }
                    }
                    if (shop == null || shopItems.get(shopUuid)==null) {
                        continue;
                    }
                    for (ShopItem shopItem : shopItems.get(shopUuid)) {
                        shop.addItem(shopItem.getItem(), shopItem.getPricePerItem(), shopItem.getAmount());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void saveAllCompanies() {
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des commpanies...");

        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {

            stmt.executeUpdate("TRUNCATE TABLE company;");
            stmt.executeUpdate("TRUNCATE TABLE company_merchants;");
            stmt.executeUpdate("TRUNCATE TABLE merchants_data;");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String queryCompany = "INSERT INTO company (city_uuid, name, owner, cut, balance) VALUES (?, ?, ?, ?, ?)";
        String queryMerchant = "INSERT INTO company_merchants (city_uuid, player, moneyWon) VALUES (?, ?, ?)";
        String queryMerchantData = "INSERT INTO merchants_data (uuid, content) VALUES (?, ?)";

        try (
                PreparedStatement stmtCompany = DatabaseManager.getConnection().prepareStatement(queryCompany);
                PreparedStatement stmtMerchant = DatabaseManager.getConnection().prepareStatement(queryMerchant);
                PreparedStatement stmtMerchantData = DatabaseManager.getConnection().prepareStatement(queryMerchantData)
        ) {
            for (Company company : companies) {
                String cityUuid = company.getOwner().getCity().getUUID();
                String name = company.getName();
                UUID owner = company.getOwner().getPlayer();
                double cut = company.getCut();
                double balance = company.getBalance();

                stmtCompany.setString(1, cityUuid);
                stmtCompany.setString(2, name);
                stmtCompany.setString(3, owner.toString());
                stmtCompany.setDouble(4, cut);
                stmtCompany.setDouble(5, balance);
                stmtCompany.addBatch(); // Adding the company to batch

                for (UUID merchantUUID : company.getMerchantsUUID()) {
                    double moneyWon = company.getMerchant(merchantUUID).getMoneyWon();
                    stmtMerchant.setString(1, cityUuid);
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
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des companies fini.");
    }


    public static void saveAllShop() {
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des shops...");

        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {

            stmt.executeUpdate("TRUNCATE TABLE shops;");
            stmt.executeUpdate("TRUNCATE TABLE shops_item;");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String queryShop = "INSERT INTO shops (shop_uuid, owner, city_uuid, x, y, z) VALUES (?, ?, ?, ?, ?, ?)";
        String queryShopItem = "INSERT INTO shops_item (item, shop_uuid, price, amount) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmtShop = DatabaseManager.getConnection().prepareStatement(queryShop)) {
            for (Company company : companies) {
                for (Shop shop : company.getShops()) {
                    UUID shopUuid = shop.getUuid();
                    UUID owner = shop.getOwner().getPlayer();
                    String cityUuid = company.getOwner().getCity().getUUID();
                    if (cityUuid == null) {
                        continue;
                    }
                    double x = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockX();
                    double y = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockY();
                    double z = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockZ();

                    for (ShopItem shopItem : shop.getItems()){
                        byte[] item = shopItem.getItem().serializeAsBytes();
                        double price = shopItem.getPricePerItem();
                        int amount = shopItem.getAmount();

                        try (PreparedStatement stmtShopItem = DatabaseManager.getConnection().prepareStatement(queryShopItem)) {
                            stmtShopItem.setBytes(1, item);
                            stmtShopItem.setString(2, shopUuid.toString());
                            stmtShopItem.setDouble(3, price);
                            stmtShopItem.setInt(4, amount);
                            stmtShopItem.addBatch();

                            stmtShopItem.executeBatch();
                        }
                    }

                    stmtShop.setString(1, shopUuid.toString());
                    stmtShop.setString(2, owner.toString());
                    stmtShop.setString(3, cityUuid);
                    stmtShop.setDouble(4, x);
                    stmtShop.setDouble(5, y);
                    stmtShop.setDouble(6, z);
                    stmtShop.addBatch(); // Adding shop to batch
                }
            }
            stmtShop.executeBatch();  // Execute batch for shops
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Map.Entry<UUID, Shop> entry : PlayerShopManager.getInstance().getPlayerShops().entrySet()) {
            Shop shop = entry.getValue();
            UUID shopUuid = shop.getUuid();
            UUID owner = entry.getKey();
            double x = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockX();
            double y = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockY();
            double z = shop.getBlocksManager().getMultiblock(shopUuid).getStockBlock().getBlockZ();

            for (ShopItem shopItem : shop.getItems()){
                byte[] item = shopItem.getItem().serializeAsBytes();
                double price = shopItem.getPricePerItem();
                int amount = shopItem.getAmount();

                try (PreparedStatement stmtShopItem = DatabaseManager.getConnection().prepareStatement(queryShopItem)) {
                    stmtShopItem.setBytes(1, item);
                    stmtShopItem.setString(2, shopUuid.toString());
                    stmtShopItem.setDouble(3, price);
                    stmtShopItem.setInt(4, amount);
                    stmtShopItem.addBatch();

                    stmtShopItem.executeBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try (PreparedStatement stmtShop = DatabaseManager.getConnection().prepareStatement(queryShop)) {
                stmtShop.setString(1, shopUuid.toString());
                stmtShop.setString(2, owner.toString());
                stmtShop.setString(3, null);
                stmtShop.setDouble(4, x);
                stmtShop.setDouble(5, y);
                stmtShop.setDouble(6, z);
                stmtShop.addBatch();  // Adding shop to batch
                stmtShop.executeBatch(); // Execute batch for shops
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        OMCPlugin.getInstance().getLogger().info("Sauvegarde des données des shops fini.");
    }

    public static ItemStack[] getMerchantItem(UUID playerUUID) {
        String query = "SELECT content FROM merchants_data WHERE uuid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, playerUUID.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    byte[] content = rs.getBytes("content");
                    return content != null ? ItemStack.deserializeItemsFromBytes(content) : new ItemStack[54];
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ItemStack[54];
    }

    // Crée une nouvelle entreprise et l'ajoute à la liste des entreprises existantes
    public void createCompany(String name, CompanyOwner owner, boolean newMember) {
        companies.add(new Company(name, owner, newMember));
    }

    // Un joueur postule pour rejoindre une entreprise
    public void applyToCompany(UUID player, Company company) {
        pendingApplications.add(player, company);
    }

    // Accepte une candidature et ajoute le joueur en tant que marchand
    public void acceptApplication(UUID player, Company company) {
        company.addMerchant(player, new MerchantData());
        pendingApplications.remove(player);
    }

    // Vérifie si un joueur a une candidature en attente pour une entreprise donnée
    public boolean hasPendingApplicationFor(UUID player, Company company) {
        return pendingApplications.get(player) == company;
    }

    // Refuse une candidature en attente
    public void denyApplication(UUID player) {
        pendingApplications.remove(player);
    }

    // Retourne la liste des joueurs ayant une candidature en attente pour une entreprise donnée
    public List<UUID> getPendingApplications(Company company) {
        List<UUID> players = new ArrayList<>();
        for (UUID player : pendingApplications.getQueue().keySet()) {
            if (hasPendingApplicationFor(player, company)) {
                players.add(player);
            }
        }
        return players;
    }

    // Liquidation d'une entreprise (suppression si conditions remplies)
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

    // Renvoyer tous les marchands d'une entreprise
    public void fireAllMerchants(Company company) {
        for (UUID uuid : company.getMerchants().keySet()) {
            company.fireMerchant(uuid);
        }
    }

    // Permet à un joueur de quitter une entreprise (différents cas gérés)
    public MethodState leaveCompany(UUID player) {
        Company company = getCompany(player);

        if (company.isOwner(player)) {
            // Si le joueur est propriétaire et qu'il n'y a pas d'autres marchands
            if (company.getMerchants().isEmpty()) {
                if (company.isUniqueOwner(player)) {
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
        MerchantData data = company.getMerchant(player);
        company.removeMerchant(player);

        // Si plus aucun membre n'est présent après le départ, l'entreprise est liquidée
        if (company.getAllMembers().isEmpty()) {
            if (!liquidateCompany(company)) {
                company.addMerchant(player, data); // Annulation si liquidation impossible
                return MethodState.WARNING;
            }
        }
        return MethodState.SUCCESS;
    }

    // Trouve une entreprise par son nom
    public Company getCompany(String name) {
        for (Company company : companies) {
            if (company.getName().equals(name)) {
                return company;
            }
        }
        return null;
    }

    // Trouve un magasin par son UUID, quel que soit son propriétaire
    public Shop getAnyShop(UUID shopUUID) {
        for (Company company : companies) {
            Shop shop = company.getShop(shopUUID);
            if (shop != null) {
                return shop;
            }
        }
        return null;
    }

    // Trouve l'entreprise d'un joueur (en tant que marchand ou propriétaire)
    public Company getCompany(UUID player) {
        for (Company company : companies) {
            if (company.getMerchants().containsKey(player)) {
                return company;
            }
            CompanyOwner owner = company.getOwner();
            if (owner.isPlayer() && owner.getPlayer().equals(player)) {
                return company;
            }
            if (owner.isCity() && owner.getCity().getMembers().contains(player)) {
                return company;
            }
        }
        return null;
    }

    // Trouve l'entreprise associée à une ville donnée
    public Company getCompany(City city) {
        for (Company company : companies) {
            if (company.getOwner().getCity() != null && company.getOwner().getCity().equals(city)) {
                return company;
            }
        }
        return null;
    }

    // Vérifie si un joueur est dans une entreprise
    public boolean isInCompany(UUID player) {
        return getCompany(player) != null;
    }

    // Vérifie si un joueur est un marchand dans une entreprise donnée
    public boolean isMerchantOfCompany(UUID player, Company company) {
        return company.getMerchants().containsKey(player);
    }

    // Vérifie si une entreprise existe par son nom
    public boolean companyExists(String name) {
        return getCompany(name) != null;
    }
}