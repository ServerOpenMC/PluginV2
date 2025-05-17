package fr.openmc.core.features.mailboxes;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.letter.SenderLetter;
import fr.openmc.core.features.mailboxes.utils.MailboxUtils;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;

@Getter
@DatabaseTable(tableName = "mail")
public class Letter {
    @DatabaseField(id = true, generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String sender;
    @DatabaseField(canBeNull = false)
    private String receiver;
    @DatabaseField(canBeNull = false)
    private byte[] items;
    @DatabaseField(columnName = "num_items", canBeNull = false)
    private int numItems;
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date sent;
    @DatabaseField
    private boolean refused;

    Letter() {
        // required by ORMLite
    }

    Letter(String sender, String receiver, byte[] items, int numItems, boolean refused) {
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
        this.numItems = numItems;
        this.refused = refused;
    }

    Letter(int id, String sender, String receiver, byte[] items, int numItems, Date sent, boolean refused) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
        this.numItems = numItems;
        this.sent = sent;
        this.refused = refused;
    }

    public boolean refuse() {
        refused = true;
        return MailboxManager.saveLetter(this);
    }

    public boolean isRefused() {
        return refused;
    }

    public LetterHead toLetterHead() {
        OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(UUID.fromString(sender));
        try {
            ItemStack[] items = BukkitSerializer.deserializeItemStacks(this.items);
            return new LetterHead(player, id, numItems,
                    LocalDateTime.ofInstant(sent.toInstant(), ZoneId.systemDefault()), items);
        } catch (Exception e) {
            e.printStackTrace();
            MailboxUtils.sendFailureMessage(player.getPlayer(), "Une erreur est survenue.");
            return null;
        }
    }

    public SenderLetter toSenderLetter() {
        OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(UUID.fromString(sender));

        return new SenderLetter(player, id, numItems, LocalDateTime.ofInstant(sent.toInstant(), ZoneId.systemDefault()),
                refused);
    }
}
