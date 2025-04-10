package fr.openmc.core.features.economy;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.Stats;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    public String recipient;
    public double amount;
    public String reason;
    public String sender;

    public Transaction(String recipient, String sender, double amount, String reason) {
        /*
        Recipient : Qui a reçu le paiement
            - CONSOLE pour le serveur (ex : adminshop)
        Sender: Qui as envoyé le paiement
            - CONSOLE pour le serveur (ex: quêtes)

        Amount: Montant envoyé/reçu
        Reason: Raison du paiement (transaction, achat, claim...)
         */

        this.recipient = recipient;
        this.sender = sender;
        this.amount = amount;
        this.reason = reason;
    }

    public ItemStack toItemStack(UUID player) {
        ItemStack itemstack;
        ItemMeta itemmeta;
        if (!Objects.equals(this.recipient, player.toString())) {
            itemstack = new ItemStack(Material.RED_CONCRETE, 1);
            itemmeta = itemstack.getItemMeta();
            itemmeta.setDisplayName("Transaction sortante");

            String recipient = "CONSOLE";
            if (!this.recipient.equals("CONSOLE")){
                recipient = Bukkit.getServer().getOfflinePlayer(UUID.fromString(this.recipient)).getName();
            }

            itemmeta.setLore(List.of(
                    "§r§6Destination:§f "+recipient,
                    "§r§6Montant:§f "+this.amount,
                    "§r§6Raison:§f "+reason
            ));
        } else {
            itemstack = new ItemStack(Material.LIME_CONCRETE, 1);
            itemmeta = itemstack.getItemMeta();
            itemmeta.setDisplayName("Transaction entrante");

            String senderName = "CONSOLE";
            if (!this.sender.equals("CONSOLE")){
                senderName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(this.sender)).getName();
            }

            itemmeta.setLore(List.of(
                    "§r§6Envoyeur:§f "+senderName,
                    "§r§6Montant:§f "+this.amount,
                    "§r§6Raison:§f "+reason
            ));
        }

        itemstack.setItemMeta(itemmeta);
        return itemstack;
    }

    public boolean register() {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) {
            return true;
        }

        if (!Objects.equals(sender, "CONSOLE")) {
            Stats.TOTAL_TRANSACTIONS.increment(UUID.fromString(sender));
        }

        if (!Objects.equals(recipient, "CONSOLE")) {
            Stats.TOTAL_TRANSACTIONS.increment(UUID.fromString(recipient));
        }

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO transactions VALUES (?, ?, ?, ?, DEFAULT)");
            statement.setString(1, this.recipient);
            statement.setString(2, this.sender);
            statement.setDouble(3, this.amount);
            statement.setString(4, this.reason);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}