package fr.openmc.core.features.mailboxes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.mailboxes.commands.MailboxCommand;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.bukkit.serializer.BukkitSerializer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHoverEvent;
import static fr.openmc.core.utils.text.InputUtils.pluralize;

@Credit(developers = {"Gexary", "Axeno"}, graphist = {"Gexary"})
public class MailboxManager extends Feature implements DatabaseFeature, HasCommands {
    private static final int MAX_STACKS_PER_LETTER = 27;
    private static final List<Letter> letters = new ArrayList<>();

    private static int nextLetterId = 1;

    @Override
    public void init() {
        MailboxManager.loadLetters();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new MailboxCommand()
        );
    }

    @Override
    public void save() {
        MailboxManager.saveLetters();
    }

    public static boolean sendItems(Player sender, OfflinePlayer receiver, ItemStack[] items) {
        if (!canSend(sender, receiver)) return false;

        List<ItemStack> allItems = Arrays.asList(items);
        for (int i = 0; i < allItems.size(); i += MAX_STACKS_PER_LETTER) {
            List<ItemStack> subList = allItems.subList(i, Math.min(i + MAX_STACKS_PER_LETTER, allItems.size()));
            if (!sendLetter(sender, receiver, subList.toArray(new ItemStack[0]))) {
                return false;
            }
        }
        return true;
    }

    private static boolean sendLetter(Player sender, OfflinePlayer receiver, ItemStack[] items) {
        String receiverName = receiver.getName();
        int numItems = Arrays.stream(items).mapToInt(ItemStack::getAmount).sum();
        LocalDateTime sent = LocalDateTime.now();

        try {
            byte[] itemsBytes = BukkitSerializer.serializeItemStacks(items);
            Letter letter = new Letter(nextLetterId++, sender.getUniqueId(), receiver.getUniqueId(), itemsBytes, numItems, Timestamp.valueOf(sent), false);
            letters.add(letter);

            int id = letter.getLetterId();
            Player receiverPlayer = receiver.getPlayer();
            if (receiverPlayer != null) {
                Inventory inv = receiverPlayer.getInventory();
                if (inv instanceof PlayerMailbox receiverMailbox) receiverMailbox.open();
                sendLetterReceivedNotification(receiverPlayer, numItems, id, sender.getName());
            }

            sendSuccessSendingMessage(sender, receiverName, numItems);
            return true;
        } catch (Exception ex) {
            OMCLogger.warn("Error while sending items to offline player: {}", ex.getMessage(), ex);
            MessagesManager.sendMessage(
                    sender,
                    TranslationManager.translation(
                            "feature.mailboxes.message.send_error",
                            Component.text(receiverName).color(NamedTextColor.RED)
                    ).color(NamedTextColor.DARK_RED),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return false;
        }
    }

    public static void sendItemsToAOfflinePlayerBatch(Map<OfflinePlayer, ItemStack[]> playerItemsMap) {
        try {
            for (Map.Entry<OfflinePlayer, ItemStack[]> entry : playerItemsMap.entrySet()) {
                OfflinePlayer player = entry.getKey();
                ItemStack[] items = entry.getValue();

                int numItems = Arrays.stream(items).mapToInt(ItemStack::getAmount).sum();

                byte[] itemsBytes = BukkitSerializer.serializeItemStacks(changeStackItem(items));

                Letter letter = new Letter(nextLetterId++, player.getUniqueId(), player.getUniqueId(), itemsBytes, numItems,
                        Timestamp.valueOf(LocalDateTime.now()), false);
                letters.add(letter);
            }
        } catch (IOException e) {
            OMCLogger.warn("Error while sending items to offline players: {}", e.getMessage(), e);
        }
    }

    private static ItemStack[] changeStackItem(ItemStack[] items) {
        return Arrays.stream(items)
                .filter(Objects::nonNull)
                .map(item -> {
                    ItemStack clone = item.clone();
                    int amount = Math.max(1, Math.min(clone.getAmount(), 99));
                    clone.setAmount(amount);
                    return clone;
                })
                .toArray(ItemStack[]::new);
    }

    public static void sendMailNotification(Player player) {
        long count = letters.stream()
                .filter(letter -> letter.getReceiver().equals(player.getUniqueId()) && !letter.isRefused())
                .count();

        if (count == 0) return;

        String countLabel = count > 1
                ? Long.toString(count)
                : TranslationManager.translationString("feature.mailboxes.message.one_letter");
        Component line1 = TranslationManager.translation(
                "feature.mailboxes.message.new_letters.line1",
                Component.text(countLabel).color(NamedTextColor.GREEN),
                Component.text(pluralize("lettre", count)).color(NamedTextColor.DARK_GREEN)
        ).color(NamedTextColor.DARK_GREEN);
        Component clickComponent = TranslationManager.translation("feature.mailboxes.message.new_letters.click")
                .color(NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/mailbox"))
                .hoverEvent(getHoverEvent(TranslationManager.translationString("feature.mailboxes.message.new_letters.hover")));
        Component line2 = clickComponent
                .append(Component.space())
                .append(TranslationManager.translation("feature.mailboxes.message.new_letters.suffix")
                        .color(NamedTextColor.GOLD));
        Component message = line1.appendNewline().append(line2);

        MessagesManager.sendMessage(
                player,
                message,
                Prefix.MAILBOX,
                MessageType.SUCCESS,
                true
        );
    }

    public static boolean deleteLetter(int id) {
        return letters.removeIf(letter -> letter.getLetterId() == id);
    }

    public static Letter getById(Player player, int id) {
        Letter letter = letters.stream()
                .filter(l -> l.getLetterId() == id)
                .findFirst()
                .orElse(null);

        if (letter == null || letter.isRefused()) return null;
        return letter;
    }

    public static List<Letter> getSentLetters(Player player) {
        return letters.stream()
                .filter(l -> l.getSender().equals(player.getUniqueId()))
                .sorted(Comparator.comparing(Letter::getSent).reversed())
                .toList();
    }

    public static List<Letter> getReceivedLetters(Player player) {
        return letters.stream()
                .filter(l -> l.getReceiver().equals(player.getUniqueId()) && !l.isRefused())
                .sorted(Comparator.comparing(Letter::getSent).reversed())
                .toList();
    }

    public static boolean canSend(Player sender, OfflinePlayer receiver) {
        if (sender.getUniqueId().equals(receiver.getUniqueId()))
            return true;
        PlayerSettings settings = PlayerSettingsManager.getPlayerSettings(receiver.getUniqueId());
        return settings.canPerformAction(SettingType.MAILBOX_RECEIVE_POLICY, sender.getUniqueId());
    }

    private static void sendLetterReceivedNotification(Player receiver, int numItems, int id, String name) {
        Component line1 = TranslationManager.translation(
                "feature.mailboxes.message.letter_received.line1",
                Component.text(numItems).color(NamedTextColor.GREEN),
                Component.text(pluralize(" item", numItems)).color(NamedTextColor.DARK_GREEN),
                Component.text(name).color(NamedTextColor.GREEN)
        ).color(NamedTextColor.DARK_GREEN);
        Component clickComponent = TranslationManager.translation("feature.mailboxes.message.letter_received.click")
                .color(NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/mailbox open " + id))
                .hoverEvent(getHoverEvent(TranslationManager.translationString(
                        "feature.mailboxes.message.letter_received.hover",
                        Component.text(id)
                )));
        Component line2 = clickComponent
                .append(Component.space())
                .append(TranslationManager.translation("feature.mailboxes.message.letter_received.suffix")
                        .color(NamedTextColor.GOLD));
        Component message = line1.appendNewline().append(line2);

        MessagesManager.sendMessage(
                receiver,
                message,
                Prefix.MAILBOX,
                MessageType.SUCCESS,
                true
        );
        Title titleComponent = getTitle(numItems, name);
        receiver.playSound(receiver.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f,
                1.0f);
        receiver.showTitle(titleComponent);
    }

    private static @NotNull Title getTitle(int numItems, String name) {
        Component subtitle = TranslationManager.translation(
                "feature.mailboxes.title.new_letter.subtitle",
                Component.text(name).color(NamedTextColor.GOLD),
                Component.text(numItems).color(NamedTextColor.GOLD),
                Component.text(pluralize(" item", numItems)).color(NamedTextColor.YELLOW)
        ).color(NamedTextColor.YELLOW);
        Component title = TranslationManager.translation("feature.mailboxes.title.new_letter")
                .color(NamedTextColor.GREEN);
        return Title.title(title, subtitle);
    }

    private static void sendSuccessSendingMessage(Player player, String receiverName, int numItems) {
        Component message = TranslationManager.translation(
                "feature.mailboxes.message.send_success",
                Component.text(numItems).color(NamedTextColor.GREEN),
                Component.text(pluralize("item", numItems)).color(NamedTextColor.DARK_GREEN),
                Component.text(pluralize("envoyé", numItems)).color(NamedTextColor.DARK_GREEN),
                Component.text(receiverName).color(NamedTextColor.GREEN)
        ).color(NamedTextColor.DARK_GREEN);

        MessagesManager.sendMessage(
                player,
                message,
                Prefix.MAILBOX,
                MessageType.SUCCESS,
                true
        );
    }

    public static void givePlayerItems(Player player, ItemStack[] items) {
        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(items);
        for (ItemStack item : remainingItems.values())
            player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    public static void cancelLetter(Player player) {
        Inventory inv = player.getInventory();
        if (inv instanceof PlayerMailbox playerMailbox) {
            playerMailbox.open();
        } else if (inv instanceof LetterMenu letter) {
            letter.cancel();
        }
    }

    // DB Methods

    private static Dao<Letter, Integer> letterDao;

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Letter.class);
        letterDao = DaoManager.createDao(connectionSource, Letter.class);
    }

    public static void loadLetters() {
        try {
            letters.addAll(letterDao.queryForAll());

            nextLetterId = letters.stream()
                    .mapToInt(Letter::getLetterId)
                    .max()
                    .orElse(0) + 1;
        } catch (SQLException e) {
            OMCLogger.error("Error loading letters from database: {}", e.getMessage(), e);
        }
    }

    public static void saveLetters() {
        try {
            TableUtils.clearTable(letterDao.getConnectionSource(), Letter.class);
            for (Letter letter : letters) {
                letterDao.create(letter);
            }
        } catch (SQLException e) {
            OMCLogger.error("Error saving letters to database: {}", e.getMessage(), e);
        }
    }
}
