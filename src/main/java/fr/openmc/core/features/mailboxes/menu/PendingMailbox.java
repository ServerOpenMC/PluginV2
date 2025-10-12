package fr.openmc.core.features.mailboxes.menu;

import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.letter.SenderLetter;
import fr.openmc.core.features.mailboxes.utils.PaginatedMailbox;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;

public class PendingMailbox extends PaginatedMailbox<SenderLetter> {
    static {
        invErrorMessage = "Erreur lors de la récupération de vos lettres.";
    }

    public PendingMailbox(Player player) {
        super(player);
        if (fetchMailbox())
            initInventory();
    }

    public static void cancelLetter(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null) {
            Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                    .append(Component.text(id, NamedTextColor.RED))
                    .append(Component.text(" n'a pas été trouvée.", NamedTextColor.DARK_RED));
            sendFailureMessage(player, message);
        }

        int itemsCount = letter.getNumItems();
        ItemStack[] items = BukkitSerializer.deserializeItemStacks(letter.getItems());
        Player receiver = CacheOfflinePlayer.getOfflinePlayer(letter.getReceiver()).getPlayer();

        if (MailboxManager.deleteLetter(id)) {
            if (receiver != null)
                MailboxManager.cancelLetter(receiver, id);
            MailboxManager.givePlayerItems(player, items);
            Component message = Component.text("Vous avez annulé la lettre et reçu ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(itemsCount, NamedTextColor.GREEN))
                    .append(Component.text(" " + getItemCount(itemsCount), NamedTextColor.DARK_GREEN));
            sendSuccessMessage(player, message);
        }
    }

    public boolean fetchMailbox() {
        List<Letter> letters = MailboxManager.getSentLetters(player);
        if (letters.isEmpty()) {
            sendFailureMessage(player, "Vous n'avez aucune lettre.");
            return false;
        }

        letters.forEach((letter) -> pageItems.add(letter.toSenderLetter()));
        return true;
    }

    public void clickLetter(int slot) {
        SenderLetter senderLetter = getByIndex(slot);
        if (senderLetter == null)
            return;
        int id = senderLetter.getId();
        Component message = Component.text("Cliquez ici", NamedTextColor.YELLOW)
                .clickEvent(getRunCommand("cancel " + id))
                .hoverEvent(getHoverEvent("Annuler la lettre #" + id))
                .append(Component.text(" si vous êtes sur de vouloir annuler la lettre.", NamedTextColor.GOLD));
        sendWarningMessage(player, message);
        player.closeInventory();
    }
}
