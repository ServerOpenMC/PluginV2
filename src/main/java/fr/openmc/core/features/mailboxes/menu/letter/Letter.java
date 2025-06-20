package fr.openmc.core.features.mailboxes.menu.letter;

import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.*;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;

public class Letter extends MailboxInv {
    private final static String INV_NAME = "\uF990\uE003";

    static {
        invErrorMessage = "Erreur lors de la récupération de votre boite aux lettres.";
    }

    private final int id;
    private final ItemStack[] items;

    public Letter(Player player, LetterHead letterHead) {
        super(player);
        this.id = letterHead.getId();
        this.items = letterHead.getItems();

        inventory = Bukkit.createInventory(this, 54, MailboxMenuManager.getInvTitle(INV_NAME));
        inventory.setItem(45, homeBtn());
        inventory.setItem(48, acceptBtn());
        inventory.setItem(49, letterHead);
        inventory.setItem(50, refuseBtn());
        inventory.setItem(53, cancelBtn());

        for (int i = 0; i < items.length; i++)
            inventory.setItem(i + 9, items[i]);
    }

    public void refuse() {
        Component message = Component.text("Cliquez-ici", NamedTextColor.YELLOW)
                .clickEvent(getRunCommand("refuse " + id))
                .hoverEvent(getHoverEvent("Refuser la lettre #" + id))
                .append(Component.text(" si vous êtes sur de vouloir refuser la lettre.", NamedTextColor.GOLD));
        sendWarningMessage(player, message);
        player.closeInventory();
    }

    public void cancel() {
        player.closeInventory();
        sendFailureMessage(player, "La lettre a été annulée.");
    }
}
