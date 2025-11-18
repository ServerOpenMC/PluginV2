package fr.openmc.core.features.mailboxes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.serializer.BukkitSerializer;
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
import static fr.openmc.core.utils.InputUtils.pluralize;

public class MailboxManager {
    private static final int MAX_STACKS_PER_LETTER = 27;
    private static final List<Letter> letters = new ArrayList<>();

    private static int nextLetterId = 1;

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
            OMCPlugin.getInstance().getSLF4JLogger().warn("Error while sending items to offline player: {}", ex.getMessage(), ex);
            MessagesManager.sendMessage(
                    sender,
                    Component.text("Une erreur est apparue lors de l'envoie des items à ", NamedTextColor.DARK_RED)
                            .append(Component.text(receiverName, NamedTextColor.RED)),
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
            OMCPlugin.getInstance().getSLF4JLogger().warn("Error while sending items to offline players: {}", e.getMessage(), e);
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

        Component message = Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN)
                .append(Component.text((count > 1 ? count : "une") + " ", NamedTextColor.GREEN))
                .append(Component.text(pluralize("lettre", count) + ".", NamedTextColor.DARK_GREEN))
                .appendNewline()
                .append(Component.text("Cliquez-ici", NamedTextColor.YELLOW))
                .clickEvent(ClickEvent.runCommand("/mailbox"))
                .hoverEvent(getHoverEvent("Ouvrir ma boîte aux lettres"))
                .append(Component.text(" pour ouvrir les lettres", NamedTextColor.GOLD));

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
        Component message = Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN)
                .append(Component.text(numItems, NamedTextColor.GREEN))
                .append(Component.text(pluralize(" item", numItems) + " de la part de ", NamedTextColor.DARK_GREEN))
                .append(Component.text(name, NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Cliquez-ici", NamedTextColor.YELLOW))
                .clickEvent(ClickEvent.runCommand("/mailbox open " + id))
                .hoverEvent(getHoverEvent("Ouvrir la lettre #" + id))
                .append(Component.text(" pour ouvrir la lettre", NamedTextColor.GOLD));

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
        Component subtitle = Component.text(name, NamedTextColor.GOLD)
                .append(Component.text(" vous a envoyé ", NamedTextColor.YELLOW))
                .append(Component.text(numItems, NamedTextColor.GOLD))
                .append(Component.text(pluralize(" item", numItems), NamedTextColor.YELLOW));
        Component title = Component.text("Nouvelle lettre !", NamedTextColor.GREEN);
        return Title.title(title, subtitle);
    }

    private static void sendSuccessSendingMessage(Player player, String receiverName, int numItems) {
        Component message = Component.text(numItems, NamedTextColor.GREEN)
                .append(Component.text(" " + pluralize("item", numItems) + " " + pluralize("envoyé", numItems) + " à ", NamedTextColor.DARK_GREEN))
                .append(Component.text(receiverName, NamedTextColor.GREEN));

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

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
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
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading letters from database: {}", e.getMessage(), e);
        }
    }

    public static void saveLetters() {
        try {
            TableUtils.clearTable(letterDao.getConnectionSource(), Letter.class);
            for (Letter letter : letters) {
                letterDao.create(letter);
            }
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error saving letters to database: {}", e.getMessage(), e);
        }
    }
}
