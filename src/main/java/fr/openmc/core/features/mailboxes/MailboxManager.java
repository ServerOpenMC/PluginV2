package fr.openmc.core.features.mailboxes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.core.OMCRegistry;
import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.mailboxes.commands.MailboxCommand;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import fr.openmc.core.utils.bukkit.serializer.BukkitSerializer;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.DateUtils;
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
public class MailboxManager extends Feature implements HasDatabase, HasCommands {
    private static final int MAX_STACKS_PER_LETTER = 27;
    private final List<Letter> letters = new ArrayList<>();

    private int nextLetterId = 1;

    private final PlayerSettingsManager playerSettingsManager = OMCRegistry.FEATURES.PLAYER_SETTINGS.get();

    @Override
    public void init() {
        this.loadLetters();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new MailboxCommand()
        );
    }

    @Override
    public void save() {
        this.saveLetters();
    }

    public boolean sendItems(OMCPlayer sender, OMCOfflinePlayer receiver, ItemStack[] items) {
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

    private boolean sendLetter(OMCPlayer sender, OMCOfflinePlayer receiver, ItemStack[] items) {
        String receiverName = receiver.getName();
        int numItems = Arrays.stream(items).mapToInt(ItemStack::getAmount).sum();
        LocalDateTime sent = DateUtils.getLocalDateTime();

        try {
            byte[] itemsBytes = BukkitSerializer.serializeItemStacks(items);
            Letter letter = new Letter(nextLetterId++, sender.getUniqueId(), receiver.getUniqueId(), itemsBytes, numItems, Timestamp.valueOf(sent), false);
            letters.add(letter);

            int id = letter.getLetterId();
            OMCPlayer receiverPlayer = OMCPlayer.of(receiver.getPlayer())  ;
            if (receiverPlayer != null) {
                Inventory inv = receiverPlayer.getInventory();
                if (inv instanceof PlayerMailbox receiverMailbox) receiverMailbox.open();
                sendLetterReceivedNotification(sender, receiverPlayer, numItems, id);
            }

            sendSuccessSendingMessage(sender, receiver, numItems);
            return true;
        } catch (Exception ex) {
            OMCLogger.warn("Error while sending items to offline player: {}", ex.getMessage(), ex);
            MessagesManager.sendMessage(
                    sender,
                    TranslationManager.translation(
                            "feature.mailboxes.message.send_error",
                            receiver.getNameWithHead().color(NamedTextColor.RED)
                    ).color(NamedTextColor.DARK_RED),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return false;
        }
    }

    public void sendItemsToAOfflinePlayerBatch(Map<OfflinePlayer, ItemStack[]> playerItemsMap) {
        for (Map.Entry<OfflinePlayer, ItemStack[]> entry : playerItemsMap.entrySet()) {
            sendItemsToOfflinePlayer(entry.getKey(), entry.getValue());
        }
    }

    public void sendItemsToOfflinePlayer(OfflinePlayer player, ItemStack[] items) {
        try {
            int numItems = Arrays.stream(items).mapToInt(ItemStack::getAmount).sum();

            byte[] itemsBytes = BukkitSerializer.serializeItemStacks(changeStackItem(items));

            Letter letter = new Letter(nextLetterId++, player.getUniqueId(), player.getUniqueId(), itemsBytes, numItems,
                    Timestamp.valueOf(DateUtils.getLocalDateTime()), false);
            letters.add(letter);
        } catch (IOException e) {
            OMCLogger.warn("Error while sending items to offline players: {}", e.getMessage(), e);
        }
    }

    private ItemStack[] changeStackItem(ItemStack[] items) {
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

    public void sendMailNotification(Player player) {
        long count = letters.stream()
                .filter(letter -> letter.getReceiverUUID().equals(player.getUniqueId()) && !letter.isRefused())
                .count();

        if (count == 0) return;

        Component countLabel = count > 1
                ? Component.text(count)
                : TranslationManager.translation("feature.mailboxes.message.one_letter");
        Component line1 = TranslationManager.translation(
                "feature.mailboxes.message.new_letters.line1",
                countLabel.color(NamedTextColor.GREEN),
                pluralize(TranslationManager.translation("feature.mailboxes.letter"), count).color(NamedTextColor.DARK_GREEN)
        ).color(NamedTextColor.DARK_GREEN);
        Component clickComponent = TranslationManager.translation("feature.mailboxes.message.new_letters.click")
                .color(NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/mailbox"))
                .hoverEvent(getHoverEvent(TranslationManager.translation("feature.mailboxes.message.new_letters.hover")));
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

    public boolean deleteLetter(int id) {
        return letters.removeIf(letter -> letter.getLetterId() == id);
    }

    public Letter getById(Player player, int id) {
        Letter letter = letters.stream()
                .filter(l -> l.getLetterId() == id)
                .filter(l -> l.getReceiverUUID().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);

        if (letter == null || letter.isRefused()) return null;
        return letter;
    }

    public List<Letter> getSentLetters(Player player) {
        return letters.stream()
                .filter(l -> l.getSenderUUID().equals(player.getUniqueId()))
                .sorted(Comparator.comparing(Letter::getSent).reversed())
                .toList();
    }

    public List<Letter> getReceivedLetters(Player player) {
        return letters.stream()
                .filter(l -> l.getReceiverUUID().equals(player.getUniqueId()) && !l.isRefused())
                .sorted(Comparator.comparing(Letter::getSent).reversed())
                .toList();
    }

    public boolean canSend(Player sender, OfflinePlayer receiver) {
        if (sender.getUniqueId().equals(receiver.getUniqueId()))
            return true;
        PlayerSettings settings = playerSettingsManager.getPlayerSettings(receiver.getUniqueId());
        return settings.canPerformAction(SettingType.MAILBOX_RECEIVE_POLICY, sender.getUniqueId());
    }

    private void sendLetterReceivedNotification(OMCPlayer sender, OMCPlayer receiver, int numItems, int id) {
        Component line1 = TranslationManager.translation(
                "feature.mailboxes.message.letter_received.line1",
                Component.text(numItems).color(NamedTextColor.GREEN),
                pluralize(Component.space()
                                .append(TranslationManager.translation("global.item")), numItems).color(NamedTextColor.DARK_GREEN),
                sender.getNameWithHead().color(NamedTextColor.GREEN)
        ).color(NamedTextColor.DARK_GREEN);
        Component clickComponent = TranslationManager.translation("feature.mailboxes.message.letter_received.click")
                .color(NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/mailbox open " + id))
                .hoverEvent(getHoverEvent(TranslationManager.translation(
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
        Title titleComponent = getTitle(numItems, sender.getName());
        receiver.playSound(receiver.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f,
                1.0f);
        receiver.showTitle(titleComponent);
    }

    public void sendConfirmMenuToCancelLetter(Player player, Letter letter) {
        new ConfirmMenu(player,
                () -> {
                    int letterId = letter.getLetterId();
                    if (letter == null) {
                        Component message = TranslationManager.translation(
                                "feature.mailboxes.message.letter_not_found",
                                Component.text(letterId).color(NamedTextColor.RED)
                        ).color(NamedTextColor.DARK_RED);
                        MessagesManager.sendMessage(
                                player,
                                message,
                                Prefix.MAILBOX,
                                MessageType.ERROR,
                                true);
                        return;
                    }

                    int itemsCount = letter.getNumItems();
                    ItemStack[] items = BukkitSerializer.deserializeItemStacks(letter.getItems());
                    Player sender = CacheOfflinePlayer.getOfflinePlayer(letter.getSender()).getPlayer();

                    if (this.deleteLetter(letterId)) {
                        if (sender != null)
                            this.cancelLetter(sender);
                        this.givePlayerItems(sender, items);
                        Component message = TranslationManager.translation(
                                "feature.mailboxes.message.cancel_success_sender",
                                Component.text(player.getName()).color(NamedTextColor.DARK_GREEN),
                                Component.text(itemsCount).color(NamedTextColor.GREEN),
                                pluralize(Component.space().append(TranslationManager.translation("global.item")), itemsCount).color(NamedTextColor.DARK_GREEN)
                        ).color(NamedTextColor.DARK_GREEN);

                        MessagesManager.sendMessage(
                                sender,
                                message,
                                Prefix.MAILBOX,
                                MessageType.SUCCESS,
                                true);
                    }

                    new PendingMailbox(player).open();
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.mailboxes.menu.cancel.success",
                                    Component.text(letter.getLetterId()).color(NamedTextColor.GREEN)
                            ).color(NamedTextColor.GREEN),
                            Prefix.MAILBOX,
                            MessageType.SUCCESS,
                            false
                    );
                },
                player::closeInventory,
                List.of(TranslationManager.translation(
                        "feature.mailboxes.menu.cancel.confirm",
                        Component.text(letter.getLetterId()).color(NamedTextColor.RED)
                ).color(NamedTextColor.RED)),
                List.of(TranslationManager.translation(
                        "feature.mailboxes.menu.cancel.cancel",
                        Component.text(letter.getLetterId()).color(NamedTextColor.GREEN)
                ).color(NamedTextColor.GREEN))
        ).open();
    }

    private @NotNull Title getTitle(int numItems, String name) {
        Component subtitle = TranslationManager.translation(
                "feature.mailboxes.title.new_letter.subtitle",
                Component.text(name).color(NamedTextColor.GOLD),
                Component.text(numItems).color(NamedTextColor.GOLD),
                pluralize(Component.space()
                        .append(TranslationManager.translation("global.item")), numItems).color(NamedTextColor.YELLOW)
        ).color(NamedTextColor.YELLOW);
        Component title = TranslationManager.translation("feature.mailboxes.title.new_letter")
                .color(NamedTextColor.GREEN);
        return Title.title(title, subtitle);
    }

    private void sendSuccessSendingMessage(Player player, OMCOfflinePlayer receiver, int numItems) {
        Component message = TranslationManager.translation(
                "feature.mailboxes.message.send_success",
                Component.text(numItems).color(NamedTextColor.GREEN),
                pluralize(TranslationManager.translation("global.item"), numItems).color(NamedTextColor.DARK_GREEN),
                pluralize(TranslationManager.translation("feature.mailboxes.message.sent_word"), numItems).color(NamedTextColor.DARK_GREEN),
                receiver.getNameWithHead().color(NamedTextColor.GREEN)
        ).color(NamedTextColor.DARK_GREEN);

        MessagesManager.sendMessage(
                player,
                message,
                Prefix.MAILBOX,
                MessageType.SUCCESS,
                true
        );
    }

    public void givePlayerItems(Player player, ItemStack[] items) {
        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(items);
        for (ItemStack item : remainingItems.values())
            player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    public void cancelLetter(Player player) {
        Inventory inv = player.getInventory();
        if (inv instanceof PlayerMailbox playerMailbox) {
            playerMailbox.open();
        } else if (inv instanceof LetterMenu letter) {
            letter.cancel();
        }
    }

    // DB Methods

    private Dao<Letter, Integer> letterDao;

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Letter.class);
        letterDao = DaoManager.createDao(connectionSource, Letter.class);
    }

    public void loadLetters() {
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

    public void saveLetters() {
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
