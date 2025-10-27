package fr.openmc.core.features.mailboxes;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.letter.SenderLetter;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

@Getter
@DatabaseTable(tableName = "mail")
public class Letter {
    @DatabaseField(columnName = "letter_id")
    private int letterId;
    @DatabaseField(canBeNull = false)
    private UUID sender;
    @DatabaseField(canBeNull = false)
    private UUID receiver;
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] items;
    @DatabaseField(columnName = "num_items", canBeNull = false)
    private int numItems;
    @DatabaseField(canBeNull = false)
    private Timestamp sent;
    @DatabaseField
    private boolean refused;

    private ItemStack[] cachedItems;

    Letter() {
        // required by ORMLite
    }

    Letter(int id, UUID sender, UUID receiver, byte[] items, int numItems, Timestamp sent, boolean refused) {
        this.letterId = id;
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
        this.numItems = numItems;
        this.refused = refused;
        this.sent = sent;
        this.cachedItems = BukkitSerializer.deserializeItemStacks(this.items);
    }

    public boolean refuse() {
        return refused = true;
    }

    public LetterHead toLetterHead() {
        OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(sender);
        return new LetterHead(player, letterId, numItems, LocalDateTime.ofInstant(sent.toInstant(), ZoneId.systemDefault()), this.cachedItems);
    }

    public ItemBuilder toSenderLetterItemBuilder(Menu menu) {
        OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(sender);

        SenderLetter senderLetter = new SenderLetter(player, numItems, LocalDateTime.ofInstant(sent.toInstant(), ZoneId.systemDefault()),
                refused);
        return new ItemBuilder(menu, senderLetter);
    }

    @Override
    public String toString() {
        return "Letter{" +
                "letterId=" + letterId +
                ", sender=" + sender +
                ", items" + Arrays.toString(items) +
                ", cachedItems=" + Arrays.toString(cachedItems) +
                ", receiver=" + receiver +
                ", itemsLength=" + items.length +
                ", numItems=" + numItems +
                ", sent=" + sent +
                ", refused=" + refused +
                '}';
    }
}
