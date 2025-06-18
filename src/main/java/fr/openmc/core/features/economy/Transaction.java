package fr.openmc.core.features.economy;

import fr.openmc.core.utils.CacheOfflinePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@DatabaseTable(tableName = "transactions")
public class Transaction {
    @DatabaseField(canBeNull = false)
    public String recipient;
    @DatabaseField(canBeNull = false)
    public double amount;
    @DatabaseField(canBeNull = false)
    public String reason;
    @DatabaseField(canBeNull = false)
    public String sender;

    Transaction() {
        // required for ORMLite
    }

    public Transaction(String recipient, String sender, double amount, String reason) {
        /*
         * Recipient : Qui a reçu le paiement
         * - CONSOLE pour le serveur (ex : adminshop)
         * Sender: Qui as envoyé le paiement
         * - CONSOLE pour le serveur (ex: quêtes)
         * 
         * Amount: Montant envoyé/reçu
         * Reason: Raison du paiement (transaction, achat, claim...)
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
            if (!this.recipient.equals("CONSOLE")) {
                recipient = CacheOfflinePlayer.getOfflinePlayer(UUID.fromString(this.recipient)).getName();
            }

            itemmeta.setLore(List.of(
                    "§r§6Destination:§f " + recipient,
                    "§r§6Montant:§f " + this.amount,
                    "§r§6Raison:§f " + reason));
        } else {
            itemstack = new ItemStack(Material.LIME_CONCRETE, 1);
            itemmeta = itemstack.getItemMeta();
            itemmeta.setDisplayName("Transaction entrante");

            String senderName = "CONSOLE";
            if (!this.sender.equals("CONSOLE")) {
                senderName = CacheOfflinePlayer.getOfflinePlayer(UUID.fromString(this.sender)).getName();
            }

            itemmeta.setLore(List.of(
                    "§r§6Envoyeur:§f " + senderName,
                    "§r§6Montant:§f " + this.amount,
                    "§r§6Raison:§f " + reason));
        }

        itemstack.setItemMeta(itemmeta);
        return itemstack;
    }
}
