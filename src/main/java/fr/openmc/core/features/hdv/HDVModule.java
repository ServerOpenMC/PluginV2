package fr.openmc.core.features.hdv;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.hdv.commands.HDVCommand;
import fr.openmc.core.features.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HDVModule implements Listener {

    private final JavaPlugin plugin;
    private static final String PREFIX = "§8[§6OpenMC §8> §eHDV§8]§r ";

    private final Map<UUID, HDVListing> listings = new HashMap<>();
    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final Map<UUID, String> playerCategories = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    private double taxRate = 0.05;
    private int maxListingsPerPlayer = 10;

    public HDVModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getCommand("hdv").setExecutor(new HDVCommand(this));

        loadConfig();

        dataFile = new File(plugin.getDataFolder(), "hdv-listings.yml");
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadListings();

        CommandsManager.getHandler().register(new HDVCommand(this));
        plugin.getLogger().info("[OpenMC > HDV] Module HDV activé !");
    }

    public void disable() {
        saveListings();
        plugin.getLogger().info("[OpenMC > HDV] Module HDV désactivé !");
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        taxRate = plugin.getConfig().getDouble("tax-rate", 0.05);
        maxListingsPerPlayer = plugin.getConfig().getInt("max-listings-per-player", 10);
    }

    public void sellItem(Player seller, ItemStack item, double price) {
        UUID listingId = UUID.randomUUID();
        HDVListing listing = new HDVListing(listingId, seller.getUniqueId(), seller.getName(),
                item, price, System.currentTimeMillis());
        listings.put(listingId, listing);
        saveListings();
    }

    public int getPlayerListingsCount(UUID playerUUID) {
        return (int) listings.values().stream()
                .filter(l -> l.getSellerUUID().equals(playerUUID))
                .count();
    }

    public int getMaxListingsPerPlayer() {
        return maxListingsPerPlayer;
    }

    public void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8[§6OpenMC§8] §eHôtel de Ville");

        playerPages.put(p.getUniqueId(), 0);
        playerCategories.put(p.getUniqueId(), "TOUS");

        inv.setItem(0, createItem(Material.GRASS_BLOCK, "§a§lTous les objets", "§7Voir tous les objets"));
        inv.setItem(1, createItem(Material.DIAMOND_SWORD, "§c§lArmes", "§7Épées, arcs, tridents..."));
        inv.setItem(2, createItem(Material.DIAMOND_CHESTPLATE, "§9§lArmures", "§7Casques, plastrons..."));
        inv.setItem(3, createItem(Material.DIAMOND_PICKAXE, "§e§lOutils", "§7Pioches, pelles, haches..."));
        inv.setItem(4, createItem(Material.ENCHANTED_BOOK, "§d§lEnchantements", "§7Livres enchantés"));
        inv.setItem(5, createItem(Material.GOLDEN_APPLE, "§6§lNourriture", "§7Aliments et potions"));
        inv.setItem(6, createItem(Material.COBBLESTONE, "§7§lBlocs", "§7Blocs de construction"));
        inv.setItem(7, createItem(Material.REDSTONE, "§c§lRedstone", "§7Circuits et mécanismes"));
        inv.setItem(8, createItem(Material.CHEST, "§b§lAutres", "§7Objets divers"));

        for (int i = 9; i < 18; i++) {
            inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", ""));
        }

        inv.setItem(45, createItem(Material.GOLD_INGOT, "§6§lMes ventes",
                "§7Gérer mes objets en vente",
                "§7En vente: §e" + getPlayerListingsCount(p.getUniqueId()) + "§7/§e" + maxListingsPerPlayer));
        inv.setItem(49, createItem(Material.EMERALD, "§a§lVendre un objet",
                "§7Tenez un objet et faites:",
                "§e/hdv vendre <prix>",
                "",
                "§7Votre solde: " + EconomyManager.getFormattedBalance(p.getUniqueId())));
        inv.setItem(48, createItem(Material.ARROW, "§e◄ Page précédente", ""));
        inv.setItem(50, createItem(Material.ARROW, "§ePage suivante ►", ""));
        inv.setItem(53, createItem(Material.BARRIER, "§c§lFermer", ""));

        loadListingsToInventory(inv, p, "TOUS", 0);

        p.openInventory(inv);
    }

    private void openCategoryMenu(Player p, String category) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8[§6OpenMC§8] §e" + getCategoryName(category));

        int page = playerPages.getOrDefault(p.getUniqueId(), 0);
        playerCategories.put(p.getUniqueId(), category);

        inv.setItem(45, createItem(Material.ARROW, "§e§l← Retour", "§7Retour au menu principal"));
        inv.setItem(49, createItem(Material.EMERALD, "§a§lVendre un objet",
                "§7Tenez un objet et faites:",
                "§e/hdv vendre <prix>",
                "",
                "§7Votre solde: " + EconomyManager.getFormattedBalance(p.getUniqueId())));
        inv.setItem(48, createItem(Material.ARROW, "§e◄ Page précédente", ""));
        inv.setItem(50, createItem(Material.ARROW, "§ePage suivante ►", ""));
        inv.setItem(53, createItem(Material.BARRIER, "§c§lFermer", ""));

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", ""));
        }

        loadListingsToInventory(inv, p, category, page);

        p.openInventory(inv);
    }

    public void openMyListingsMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8[§6OpenMC§8] §eMes ventes");

        List<HDVListing> myListings = listings.values().stream()
                .filter(l -> l.getSellerUUID().equals(p.getUniqueId()))
                .collect(Collectors.toList());

        int slot = 9;
        for (HDVListing listing : myListings) {
            if (slot >= 45) break;

            ItemStack display = listing.getItem().clone();
            ItemMeta meta = display.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§6Prix: " + EconomyManager.getFormattedNumber(listing.getPrice()));
            lore.add("§7Clic droit pour retirer");
            meta.setLore(lore);
            display.setItemMeta(meta);

            inv.setItem(slot++, display);
        }

        inv.setItem(45, createItem(Material.ARROW, "§e§l← Retour", ""));
        inv.setItem(49, createItem(Material.EMERALD, "§a§lVotre solde",
                EconomyManager.getFormattedBalance(p.getUniqueId()),
                "",
                "§7Objets en vente: §e" + myListings.size() + "§7/§e" + maxListingsPerPlayer));
        inv.setItem(53, createItem(Material.BARRIER, "§c§lFermer", ""));

        p.openInventory(inv);
    }

    private void loadListingsToInventory(Inventory inv, Player p, String category, int page) {
        List<HDVListing> filtered = listings.values().stream()
                .filter(l -> !l.getSellerUUID().equals(p.getUniqueId()))
                .filter(l -> category.equals("TOUS") || getItemCategory(l.getItem().getType()).equals(category))
                .sorted(Comparator.comparingLong(HDVListing::getTimestamp).reversed())
                .collect(Collectors.toList());

        int startIndex = page * 36;
        int endIndex = Math.min(startIndex + 36, filtered.size());

        int slot = 9;
        for (int i = startIndex; i < endIndex; i++) {
            HDVListing listing = filtered.get(i);
            ItemStack display = listing.getItem().clone();
            ItemMeta meta = display.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§6Prix: " + EconomyManager.getFormattedNumber(listing.getPrice()));
            lore.add("§7Vendeur: §f" + listing.getSellerName());
            lore.add("");
            lore.add("§a§lClic gauche pour acheter");
            meta.setLore(lore);
            display.setItemMeta(meta);

            inv.setItem(slot++, display);
        }

        if (page > 0) {
            inv.setItem(48, createItem(Material.ARROW, "§e◄ Page " + page, ""));
        }
        if (endIndex < filtered.size()) {
            inv.setItem(50, createItem(Material.ARROW, "§ePage " + (page + 2) + " ►", ""));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().contains("HDV") || e.getView().getTitle().contains("Mes ventes")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            ItemStack clicked = e.getCurrentItem();

            if (e.getView().getTitle().equals("§8[§6OpenMC§8] §eHôtel de Ville")) {
                handleMainMenuClick(p, e.getSlot());
            } else if (e.getView().getTitle().startsWith("§8[§6OpenMC§8] §e")) {
                handleCategoryMenuClick(p, e.getSlot(), clicked);
            } else if (e.getView().getTitle().equals("§8[§6OpenMC§8] §eMes ventes")) {
                handleMyListingsClick(p, e.getSlot(), clicked, e.isRightClick());
            }
        }
    }

    private void handleMainMenuClick(Player p, int slot) {
        if (slot == 0) openCategoryMenu(p, "TOUS");
        else if (slot == 1) openCategoryMenu(p, "ARMES");
        else if (slot == 2) openCategoryMenu(p, "ARMURES");
        else if (slot == 3) openCategoryMenu(p, "OUTILS");
        else if (slot == 4) openCategoryMenu(p, "ENCHANTEMENTS");
        else if (slot == 5) openCategoryMenu(p, "NOURRITURE");
        else if (slot == 6) openCategoryMenu(p, "BLOCS");
        else if (slot == 7) openCategoryMenu(p, "REDSTONE");
        else if (slot == 8) openCategoryMenu(p, "AUTRES");
        else if (slot == 45) openMyListingsMenu(p);
        else if (slot == 53) p.closeInventory();
    }

    private void handleCategoryMenuClick(Player p, int slot, ItemStack clicked) {
        if (slot == 45) {
            openMainMenu(p);
        } else if (slot == 48) {
            int page = playerPages.getOrDefault(p.getUniqueId(), 0);
            if (page > 0) {
                playerPages.put(p.getUniqueId(), page - 1);
                openCategoryMenu(p, playerCategories.get(p.getUniqueId()));
            }
        } else if (slot == 50) {
            int page = playerPages.getOrDefault(p.getUniqueId(), 0);
            playerPages.put(p.getUniqueId(), page + 1);
            openCategoryMenu(p, playerCategories.get(p.getUniqueId()));
        } else if (slot == 53) {
            p.closeInventory();
        } else if (slot >= 9 && slot < 45) {
            buyItem(p, clicked);
        }
    }

    private void handleMyListingsClick(Player p, int slot, ItemStack clicked, boolean isRightClick) {
        if (slot == 45) {
            openMainMenu(p);
        } else if (slot == 53) {
            p.closeInventory();
        } else if (slot >= 9 && slot < 45 && isRightClick) {
            removeMyListing(p, clicked);
        }
    }

    private void buyItem(Player buyer, ItemStack display) {
        if (!buyer.hasPermission("openmc.hdv.buy")) {
            buyer.sendMessage(PREFIX + "§cVous n'avez pas la permission d'acheter des objets !");
            return;
        }

        HDVListing listing = findListingByItem(display);
        if (listing == null) {
            buyer.sendMessage(PREFIX + "§cCet objet n'est plus disponible !");
            return;
        }

        double price = listing.getPrice();
        double buyerBalance = EconomyManager.getBalance(buyer.getUniqueId());

        if (buyerBalance < price) {
            buyer.sendMessage(PREFIX + "§cVous n'avez pas assez d'argent ! Requis: " +
                    EconomyManager.getFormattedNumber(price));
            buyer.sendMessage(PREFIX + "§cVotre solde: " +
                    EconomyManager.getFormattedBalance(buyer.getUniqueId()));
            return;
        }

        double tax = price * taxRate;
        double sellerReceives = price - tax;

        if (!EconomyManager.withdrawBalance(buyer.getUniqueId(), price)) {
            buyer.sendMessage(PREFIX + "§cErreur lors de la transaction !");
            return;
        }

        EconomyManager.addBalance(listing.getSellerUUID(), sellerReceives);

        buyer.getInventory().addItem(listing.getItem());
        listings.remove(listing.getId());
        saveListings();

        buyer.sendMessage(PREFIX + "§aVous avez acheté §e" + listing.getItem().getType() +
                " §apour " + EconomyManager.getFormattedNumber(price) + " §a!");

        Player seller = Bukkit.getPlayer(listing.getSellerUUID());
        if (seller != null && seller.isOnline()) {
            seller.sendMessage(PREFIX + "§aVotre §e" + listing.getItem().getType() +
                    " §aa été vendu pour " + EconomyManager.getFormattedNumber(price) + " §a!");
            seller.sendMessage(PREFIX + "§aVous avez reçu " +
                    EconomyManager.getFormattedNumber(sellerReceives) +
                    " §a(taxe: " + EconomyManager.getFormattedNumber(tax) + "§a)");
        }

        buyer.closeInventory();
        openCategoryMenu(buyer, playerCategories.getOrDefault(buyer.getUniqueId(), "TOUS"));
    }

    private void removeMyListing(Player p, ItemStack display) {
        HDVListing listing = findListingByItem(display);
        if (listing == null || !listing.getSellerUUID().equals(p.getUniqueId())) {
            p.sendMessage(PREFIX + "§cImpossible de retirer cet objet !");
            return;
        }

        p.getInventory().addItem(listing.getItem());
        listings.remove(listing.getId());
        saveListings();

        p.sendMessage(PREFIX + "§aVotre objet a été retiré de la vente !");
        p.closeInventory();
        openMyListingsMenu(p);
    }

    private HDVListing findListingByItem(ItemStack display) {
        ItemMeta meta = display.getItemMeta();
        if (meta == null || !meta.hasLore()) return null;

        for (HDVListing listing : listings.values()) {
            if (listing.getItem().getType() == display.getType() &&
                    listing.getItem().getAmount() == display.getAmount()) {
                return listing;
            }
        }
        return null;
    }

    private String getItemCategory(Material mat) {
        String name = mat.name();
        if (name.contains("SWORD") || name.contains("BOW") || name.contains("TRIDENT") ||
                name.contains("CROSSBOW") || name.contains("AXE") && name.contains("DIAMOND")) {
            return "ARMES";
        } else if (name.contains("HELMET") || name.contains("CHESTPLATE") ||
                name.contains("LEGGINGS") || name.contains("BOOTS")) {
            return "ARMURES";
        } else if (name.contains("PICKAXE") || name.contains("AXE") ||
                name.contains("SHOVEL") || name.contains("HOE")) {
            return "OUTILS";
        } else if (name.contains("ENCHANTED_BOOK")) {
            return "ENCHANTEMENTS";
        } else if (mat.isEdible() || name.contains("POTION")) {
            return "NOURRITURE";
        } else if (mat.isBlock() && !name.contains("REDSTONE")) {
            return "BLOCS";
        } else if (name.contains("REDSTONE") || name.contains("REPEATER") ||
                name.contains("COMPARATOR") || name.contains("PISTON")) {
            return "REDSTONE";
        }
        return "AUTRES";
    }

    private String getCategoryName(String category) {
        switch (category) {
            case "TOUS": return "Tous les objets";
            case "ARMES": return "Armes";
            case "ARMURES": return "Armures";
            case "OUTILS": return "Outils";
            case "ENCHANTEMENTS": return "Enchantements";
            case "NOURRITURE": return "Nourriture";
            case "BLOCS": return "Blocs";
            case "REDSTONE": return "Redstone";
            case "AUTRES": return "Autres";
            default: return category;
        }
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }

    private void saveListings() {
        dataConfig.set("listings", null);
        int i = 0;
        for (HDVListing listing : listings.values()) {
            String path = "listings." + i;
            dataConfig.set(path + ".id", listing.getId().toString());
            dataConfig.set(path + ".seller-uuid", listing.getSellerUUID().toString());
            dataConfig.set(path + ".seller-name", listing.getSellerName());
            dataConfig.set(path + ".item", listing.getItem());
            dataConfig.set(path + ".price", listing.getPrice());
            dataConfig.set(path + ".timestamp", listing.getTimestamp());
            i++;
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListings() {
        if (!dataConfig.contains("listings")) return;

        for (String key : dataConfig.getConfigurationSection("listings").getKeys(false)) {
            String path = "listings." + key;
            UUID id = UUID.fromString(dataConfig.getString(path + ".id"));
            UUID sellerUUID = UUID.fromString(dataConfig.getString(path + ".seller-uuid"));
            String sellerName = dataConfig.getString(path + ".seller-name");
            ItemStack item = dataConfig.getItemStack(path + ".item");
            double price = dataConfig.getDouble(path + ".price");
            long timestamp = dataConfig.getLong(path + ".timestamp");

            listings.put(id, new HDVListing(id, sellerUUID, sellerName, item, price, timestamp));
        }
    }

    public static class HDVListing {
        private final UUID id;
        private final UUID sellerUUID;
        private final String sellerName;
        private final ItemStack item;
        private final double price;
        private final long timestamp;

        public HDVListing(UUID id, UUID sellerUUID, String sellerName, ItemStack item, double price, long timestamp) {
            this.id = id;
            this.sellerUUID = sellerUUID;
            this.sellerName = sellerName;
            this.item = item;
            this.price = price;
            this.timestamp = timestamp;
        }

        public UUID getId() { return id; }
        public UUID getSellerUUID() { return sellerUUID; }
        public String getSellerName() { return sellerName; }
        public ItemStack getItem() { return item; }
        public double getPrice() { return price; }
        public long getTimestamp() { return timestamp; }
    }
}