package fr.openmc.core.features.corporation;


import dev.xernas.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.data.TransactionData;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.Queue;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class Company {

    private final String name;
    private final EconomyManager economyManager;
    private final ShopBlocksManager shopBlocksManager;
    private final Map<UUID, MerchantData> merchants = new HashMap<>();
    private final List<Shop> shops = new ArrayList<>();
    private final Queue<Long, TransactionData> transactions = new Queue<>(150);
    private final double turnover = 0;
    private CompanyOwner owner;
    private double balance = 0;
    @Setter
    private double cut = 0.25;

    private int shopCounter = 0;

    public Company(String name, CompanyOwner owner, EconomyManager economyManager, ShopBlocksManager shopBlocksManager) {
        this.name = name;
        this.owner = owner;
        this.economyManager = economyManager;
        this.shopBlocksManager = shopBlocksManager;
    }

    public double getTurnover() {
        double turnover = 0;
        for (Shop shop : shops) turnover += shop.getTurnover();
        return turnover;
    }

    public Shop getShop(UUID uuid) {
        for (Shop shop : shops) {
            if (shop.getUuid().equals(uuid)) {
                return shop;
            }
        }
        return null;
    }

    public Shop getShop(int shop) {
        for (Shop shopToGet : shops) {
            if (shopToGet.getIndex() == shop) {
                return shopToGet;
            }
        }
        return null;
    }

    public boolean createShop(Player whoCreated, Block barrel, Block cash) {
        if (withdraw(100, whoCreated, "Création de shop", economyManager)) {
            Shop newShop = new Shop(new ShopOwner(this), shopCounter, economyManager, shopBlocksManager);
            shops.add(newShop);
            economyManager.withdrawBalance(whoCreated.getUniqueId(), 100);
            shopBlocksManager.registerMultiblock(newShop, new Shop.Multiblock(barrel.getLocation(), cash.getLocation()));
            shopBlocksManager.placeShop(newShop, whoCreated, true);
            shopCounter++;
            return true;
        }
        return false;
    }

    public MethodState deleteShop(Player player, UUID uuid) {
        for (Shop shop : shops) {
            if (shop.getUuid().equals(uuid)) {
                if (!shop.getItems().isEmpty()) {
                    return MethodState.WARNING;
                }
                if (!deposit(75, player, "Suppression de shop", economyManager)) {
                    return MethodState.SPECIAL;
                }
                if (!shopBlocksManager.removeShop(shop)) {
                    return MethodState.ESCAPE;
                }
                shops.remove(shop);
                economyManager.addBalance(player.getUniqueId(), 75);
                return MethodState.SUCCESS;
            }
        }
        return MethodState.ERROR;
    }

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

    public List<UUID> getMerchantsUUID() {
        return new ArrayList<>(merchants.keySet());
    }

    public MerchantData getMerchant(UUID uuid) {
        return merchants.get(uuid);
    }

    public void addMerchant(UUID uuid, MerchantData data) {
        merchants.put(uuid, data);
    }

    public void fireMerchant(UUID uuid) {
        removeMerchant(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(ChatColor.RED + "Vous avez été renvoyé de l'entreprise' " + name);
        }
    }

    public void removeMerchant(UUID uuid) {
        merchants.remove(uuid);
    }

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

    public boolean isOwner(UUID uuid) {
        if (owner.isPlayer()) {
            return owner.getPlayer().equals(uuid);
        }
        else {
            return owner.getCity().getMembers().contains(uuid);
        }
    }

    public boolean isUniqueOwner(UUID uuid) {
        if (owner.isPlayer()) {
            return owner.getPlayer().equals(uuid);
        }
        else {
            return owner.getCity().getPlayerWith(CPermission.OWNER).equals(uuid);
        }
    }

    public boolean isIn(UUID uuid) {
        if (merchants.containsKey(uuid)) {
            return true;
        }
        return isOwner(uuid);
    }

    public void setOwner(UUID uuid) {
        owner = new CompanyOwner(uuid);
    }

    public ItemStack getHead() {
        if (owner.isPlayer()) {
            return ItemUtils.getPlayerSkull(owner.getPlayer());
        }
        else {
            return ItemUtils.getPlayerSkull(owner.getCity().getPlayerWith(CPermission.OWNER));
        }
    }

    public boolean withdraw(double amount, Player player, String nature, EconomyManager economyManager) {
        return withdraw(amount, player, nature, "", economyManager);
    }

    public boolean withdraw(double amount, Player player, String nature, String additionalInfo, EconomyManager economyManager) {
        if (balance >= amount) {
            balance -= amount;
            if (amount > 0) {
                TransactionData transaction = new TransactionData(-amount, nature, additionalInfo, player.getUniqueId());
                transactions.add(System.currentTimeMillis(), transaction);
                economyManager.addBalance(player.getUniqueId(), amount);
            }
            return true;
        }
        return false;
    }

    public boolean deposit(double amount, Player player, String nature, EconomyManager economyManager) {
        return deposit(amount, player, nature, "", economyManager);
    }

    public boolean deposit(double amount, Player player, String nature, String additionalInfo, EconomyManager economyManager) {
        if (economyManager.withdrawBalance(player.getUniqueId(), amount)) {
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
