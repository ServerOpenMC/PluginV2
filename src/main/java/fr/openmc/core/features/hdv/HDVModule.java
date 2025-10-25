package fr.openmc.core.features.hdv;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@SuppressWarnings({"unused", "WeakerAccess"})
public class HDVModule {
    @Getter
    private static HDVModule instance;
    private final Map<UUID, List<HDVListing>> listings;
    private static final String PREFIX = Prefix.HDV.asText() + " ";
    public static final String MILESTONE_FIRST_SALE = "Premier article vendu";
    public static final String MILESTONE_FIRST_PURCHASE = "Premier achat";
    private final File storageFile;

    public HDVModule() {
        instance = this;
        this.listings = new HashMap<>();
        this.storageFile = new File(OMCPlugin.getInstance().getDataFolder(), "hdv_listings.yml");
        File parent = this.storageFile.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean created = parent.mkdirs();
            if (!created) {
                OMCPlugin.getInstance().getSLF4JLogger().warn("Unable to create HDV storage directory: {}", parent.getAbsolutePath());
            }
        }
        registerMilestones();
        loadListings();
    }

    private void registerMilestones() {
        MilestonesManager.registerMilestones(
            new HDVMilestone(MILESTONE_FIRST_SALE, "Vendez votre premier article dans l'HDV"),
            new HDVMilestone(MILESTONE_FIRST_PURCHASE, "Achetez votre premier article dans l'HDV")
        );
    }

    public void addListing(Player seller, ItemStack item, double price) {
        if (price <= 0) {
            seller.sendMessage(PREFIX + "§cLe prix doit être supérieur à 0!");
            return;
        }

        HDVListing listing = new HDVListing(seller.getUniqueId(), item.clone(), price);
        listings.computeIfAbsent(seller.getUniqueId(), k -> new ArrayList<>()).add(listing);
        seller.getInventory().setItemInMainHand(null);
        seller.sendMessage(PREFIX + "§aVotre objet a été mis en vente pour §e" + price + " §acoins.");
        saveListings();
    }

    public void removeListing(UUID seller, HDVListing listing) {
        if (listings.containsKey(seller)) {
            listings.get(seller).remove(listing);
            saveListings();
        }
    }

    /**
     * Handle a purchase from the HDV. Sends messages and transfers money/items.
     * This method intentionally returns void because callers do not use a return value.
     */
    public void buyItem(Player buyer, HDVListing listing) {
        double price = listing.getPrice();
        UUID buyerId = buyer.getUniqueId();
        UUID sellerId = listing.getSeller();
        if (EconomyManager.getBalance(buyerId) < price) {
            buyer.sendMessage(PREFIX + "§cVous n'avez pas assez d'argent pour acheter cet objet!");
            return;
        }
        if (buyerId.equals(sellerId)) {
            buyer.sendMessage(PREFIX + "§cVous ne pouvez pas acheter vos propres objets!");
            return;
        }
        if (!EconomyManager.withdrawBalance(buyerId, price)) {
            buyer.sendMessage(PREFIX + "§cErreur lors du paiement!");
            return;
        }
        EconomyManager.addBalance(sellerId, price);
        Player seller = Bukkit.getPlayer(sellerId);
        if (seller != null) {
            seller.sendMessage(PREFIX + "§aVotre objet a été vendu pour §e" + price + " §acoins!");
            // milestone registration happens on module init; actual completion API not changed here
        }
        removeListing(sellerId, listing);
        buyer.getInventory().addItem(listing.getItem());
        buyer.sendMessage(PREFIX + "§aAchat effectué avec succès!");
    }

    public List<HDVListing> getAllListings() {
        List<HDVListing> allListings = new ArrayList<>();
        listings.values().forEach(allListings::addAll);
        return allListings;
    }

    public List<HDVListing> getPlayerListings(UUID player) {
        return listings.getOrDefault(player, new ArrayList<>());
    }

    /** Persist listings to YAML file (seller,price,item base64) */
    public synchronized void saveListings() {
        try {
            // parent already ensured in constructor
            YamlConfiguration cfg = new YamlConfiguration();
            List<Map<String, Object>> out = new ArrayList<>();
            for (Map.Entry<UUID, List<HDVListing>> e : listings.entrySet()) {
                for (HDVListing l : e.getValue()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("seller", l.getSeller().toString());
                    m.put("price", l.getPrice());
                    try {
                        byte[] bytes = BukkitSerializer.serializeItemStacks(new ItemStack[]{l.getItem()});
                        String base64 = Base64.getEncoder().encodeToString(bytes);
                        m.put("item", base64);
                    } catch (IOException ex) {
                        // skip this listing if serialization fails
                        OMCPlugin.getInstance().getSLF4JLogger().error("Failed to serialize HDV item", ex);
                        continue;
                    }
                    out.add(m);
                }
            }
            cfg.set("listings", out);
            cfg.save(storageFile);
        } catch (IOException ex) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to save HDV listings", ex);
        }
    }

    /** Load listings from YAML storage into memory */
    @SuppressWarnings("unchecked")
    public synchronized void loadListings() {
        try {
            if (!storageFile.exists()) return;
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(storageFile);
            List<Map<String, Object>> in = (List<Map<String, Object>>) cfg.getList("listings");
            if (in == null) return;
            listings.clear();
            for (Map<String, Object> m : in) {
                try {
                    UUID seller = UUID.fromString((String) m.get("seller"));
                    double price = ((Number) m.get("price")).doubleValue();
                    String base64 = (String) m.get("item");
                    byte[] bytes = Base64.getDecoder().decode(base64);
                    ItemStack[] items = BukkitSerializer.deserializeItemStacks(bytes);
                    ItemStack item = items.length > 0 ? items[0] : null;
                    if (item == null) continue;
                    HDVListing listing = new HDVListing(seller, item, price);
                    listings.computeIfAbsent(seller, k -> new ArrayList<>()).add(listing);
                } catch (Exception ex) {
                    OMCPlugin.getInstance().getSLF4JLogger().error("Failed to load a HDV listing", ex);
                }
            }
        } catch (Exception ex) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to load HDV listings", ex);
        }
    }
}
