package fr.openmc.core.features.economy;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            itemmeta.displayName(TranslationManager.translation("feature.economy.transaction.outgoing.name"));

            String recipient = "CONSOLE";
            if (!this.recipient.equals("CONSOLE")) {
                recipient = CacheOfflinePlayer.getOfflinePlayer(UUID.fromString(this.recipient)).getName();
            }

            itemmeta.lore(TranslationManager.translationLore(
                    "feature.economy.transaction.outgoing.lore",
                    Component.text(recipient).color(NamedTextColor.WHITE),
                    Component.text(this.amount).color(NamedTextColor.WHITE),
                    Component.text(reason).color(NamedTextColor.WHITE)
            ));
        } else {
            itemstack = new ItemStack(Material.LIME_CONCRETE, 1);
            itemmeta = itemstack.getItemMeta();
            itemmeta.displayName(TranslationManager.translation("feature.economy.transaction.incoming.name"));

            String senderName = "CONSOLE";
            if (!this.sender.equals("CONSOLE")) {
                senderName = CacheOfflinePlayer.getOfflinePlayer(UUID.fromString(this.sender)).getName();
            }

            itemmeta.lore(TranslationManager.translationLore(
                    "feature.economy.transaction.incoming.lore",
                    Component.text(senderName).color(NamedTextColor.WHITE),
                    Component.text(this.amount).color(NamedTextColor.WHITE),
                    Component.text(reason).color(NamedTextColor.WHITE)
            ));
        }

        itemstack.setItemMeta(itemmeta);
        return itemstack;
    }
}
