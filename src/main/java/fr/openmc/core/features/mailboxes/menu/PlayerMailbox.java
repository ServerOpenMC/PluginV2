package fr.openmc.core.features.mailboxes.menu;

import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.utils.PaginatedMailbox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PlayerMailbox extends PaginatedMailbox<LetterHead> {

    static {
        invErrorMessage = "Erreur lors de la récupération de votre boite aux lettres.";
    }

    public PlayerMailbox(Player player) {
        super(player);
        if (fetchMailbox())
            initInventory();
    }

    public void addLetter(LetterHead letterHead) {
        pageItems.add(letterHead);
        int size = pageItems.size();
        if (size - 1 / maxIndex == page)
            updateInventory(false, size - 1 % maxIndex);
    }

    public void removeLetter(int id) {
        for (int i = 0; i < pageItems.size(); i++) {
            if (pageItems.get(i).getId() == id) {
                pageItems.remove(i);
                int currentPage = i / maxIndex;

                if (currentPage == page) {
                    updateInventory(false, i % maxIndex);
                } else if (currentPage < page) {
                    updateInventory(true);
                }
                break;
            }
        }
    }

    public boolean fetchMailbox() {
        List<Letter> letters = MailboxManager.getLetters(player);
        if (letters.size() < 1)
            return false;

        letters.forEach((letter) -> pageItems.add(letter.toLetterHead()));
        return true;
    }
}
