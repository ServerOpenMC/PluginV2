package fr.openmc.core.features.mailboxes;

import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;

// Author Gexary
public class MailboxManager {

    private static Dao<Letter, Integer> letterDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Letter.class);
        letterDao = DaoManager.createDao(connectionSource, Letter.class);
    }

    public static boolean sendItems(Player sender, OfflinePlayer receiver, ItemStack[] items) {
        if (!canSend(sender, receiver))
            return false;
        String receiverName = receiver.getName();
        int numItems = Arrays.stream(items).mapToInt(ItemStack::getAmount).sum();
        LocalDateTime sent = LocalDateTime.now();

        try {
            byte[] itemsBytes = BukkitSerializer.serializeItemStacks(items);
            Letter letter = new Letter(sender.getUniqueId(), receiver.getUniqueId(), itemsBytes, numItems,
                    Timestamp.valueOf(LocalDateTime.now()), false);
            if (letterDao.create(letter) == 0)
                return false;
            int id = letter.getId();

            Player receiverPlayer = receiver.getPlayer();
            if (receiverPlayer != null) {
                if (MailboxMenuManager.playerInventories.get(receiverPlayer) instanceof PlayerMailbox receiverMailbox) {
                    LetterHead letterHead = new LetterHead(sender, numItems, id, sent);
                    receiverMailbox.addLetter(letterHead);
                } else
                    sendNotification(receiverPlayer, numItems, id, sender.getName());
            }
            sendSuccessSendingMessage(sender, receiverName, numItems);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            sendFailureSendingMessage(sender, receiverName);
            return false;
        }
    }

    public static void sendItemsToAOfflinePlayerBatch(Map<OfflinePlayer, ItemStack[]> playerItemsMap) {
        try {
            List<Letter> letters = new ArrayList<>();
            for (Map.Entry<OfflinePlayer, ItemStack[]> entry : playerItemsMap.entrySet()) {
                OfflinePlayer player = entry.getKey();
                ItemStack[] items = entry.getValue();

                int numItems = Arrays.stream(items).mapToInt(ItemStack::getAmount).sum();

                byte[] itemsBytes = BukkitSerializer.serializeItemStacks(items);

                Letter letter = new Letter(player.getUniqueId(), player.getUniqueId(), itemsBytes, numItems,
                        Timestamp.valueOf(LocalDateTime.now()), false);
                letters.add(letter);
            }
            letterDao.create(letters);
        } catch (SQLException e) {
            Logger.getLogger(MailboxManager.class.getName()).log(Level.SEVERE,
                    "Erreur lors de l'envoi des items batch à des joueurs hors ligne", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendMailNotification(Player player) {
        try {
            QueryBuilder<Letter, Integer> query = letterDao.queryBuilder();
            query.where().eq("receiver", player.getUniqueId()).and().eq("refused", false);
            query.setCountOf(true);
            long count = letterDao.countOf(query.prepare());

            if (count == 0)
                return;

            Component message = null;
            message = Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN);
            if (count > 1) {
                message.append(Component.text(count, NamedTextColor.GREEN))
                        .append(Component.text(" lettres.", NamedTextColor.DARK_GREEN));
            } else if (count == 1) {
                message.append(Component.text("une", NamedTextColor.GREEN))
                        .append(Component.text(" lettre.", NamedTextColor.DARK_GREEN));
            }

            message.append(Component.text("\nCliquez-ici", NamedTextColor.YELLOW))
                    .clickEvent(ClickEvent.runCommand("/mailbox"))
                    .hoverEvent(getHoverEvent("Ouvrir ma boîte aux lettres"))
                    .append(Component.text(" pour ouvrir les lettres", NamedTextColor.GOLD));

            if (message != null)
                sendSuccessMessage(player, message);
        } catch (SQLException e) {
            e.printStackTrace();
            sendFailureMessage(player, "Une erreur est survenue.");
        }
    }

    public static boolean saveLetter(Letter letter) {
        try {
            return letterDao.createOrUpdate(letter) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteLetter(int id) {
        try {
            return letterDao.deleteById(id) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Letter getById(Player player, int id) {
        try {
            Letter letter = letterDao.queryForId(id);
            if (letter.isRefused())
                return null;

            return letter;
        } catch (Exception e) {
            e.printStackTrace();
            sendFailureMessage(player, "Une erreur est survenue.");
            return null;
        }
    }

    public static List<Letter> getSentLetters(Player player) {
        try {
            QueryBuilder<Letter, Integer> query = letterDao.queryBuilder();
            query.where().eq("sender", player.getUniqueId());
            query.orderBy("sent", false);
            return letterDao.query(query.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Letter> getReceivedLetters(Player player) {
        try {
            QueryBuilder<Letter, Integer> query = letterDao.queryBuilder();
            query.where().eq("receiver", player.getUniqueId());
            query.orderBy("sent", false);
            return letterDao.query(query.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // todo
    public static boolean canSend(Player sender, OfflinePlayer receiver) {
        if (!PlayerSettingsManager.canReceiveMailbox(receiver.getUniqueId(), sender.getUniqueId())) {
            return false;
        }
        return true;
    }

    private static void sendNotification(Player receiver, int numItems, int id, String name) {
        Component message = Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN)
                .append(Component.text(numItems, NamedTextColor.GREEN))
                .append(Component.text(" item" + (numItems > 1 ? "s" : "") + " de la part de ",
                        NamedTextColor.DARK_GREEN))
                .append(Component.text(name, NamedTextColor.GREEN))
                .append(Component.text("\nCliquez-ici", NamedTextColor.YELLOW))
                .clickEvent(getRunCommand("open " + id))
                .hoverEvent(getHoverEvent("Ouvrir la lettre #" + id))
                .append(Component.text(" pour ouvrir la lettre", NamedTextColor.GOLD));
        sendSuccessMessage(receiver, message);
        Title titleComponent = getTitle(numItems, name);
        receiver.playSound(receiver.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f,
                1.0f);
        receiver.showTitle(titleComponent);
    }

    private static @NotNull Title getTitle(int numItems, String name) {
        Component subtitle = Component.text(name, NamedTextColor.GOLD)
                .append(Component.text(" vous a envoyé ", NamedTextColor.YELLOW))
                .append(Component.text(numItems, NamedTextColor.GOLD))
                .append(Component.text(" item" + (numItems > 1 ? "s" : ""), NamedTextColor.YELLOW));
        Component title = Component.text("Nouvelle lettre !", NamedTextColor.GREEN);
        return Title.title(title, subtitle);
    }

    private static void sendFailureSendingMessage(Player player, String receiverName) {
        Component message = Component
                .text("Une erreur est apparue lors de l'envoie des items à ", NamedTextColor.DARK_RED)
                .append(Component.text(receiverName, NamedTextColor.RED));
        sendFailureMessage(player, message);
    }

    private static void sendSuccessSendingMessage(Player player, String receiverName, int numItems) {
        Component message = Component.text(numItems, NamedTextColor.GREEN)
                .append(Component.text(" " + getItemCount(numItems) + " envoyé" + (numItems > 1 ? "s" : "") + " à ",
                        NamedTextColor.DARK_GREEN))
                .append(Component.text(receiverName, NamedTextColor.GREEN));
        sendSuccessMessage(player, message);
    }

    public static void givePlayerItems(Player player, ItemStack[] items) {
        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(items);
        for (ItemStack item : remainingItems.values())
            player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    public static void cancelLetter(Player player, int id) {
        MailboxInv inv = MailboxMenuManager.playerInventories.get(player);
        if (inv instanceof PlayerMailbox playerMailbox) {
            playerMailbox.removeLetter(id);
        } else if (inv instanceof LetterMenu letter) {
            letter.cancel();
        }
    }
}
