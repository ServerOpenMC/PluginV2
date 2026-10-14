package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mailboxes.commands.MailboxCommand;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MailboxButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(85, 86, 87);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                ButtonItems.name("feature.mainmenu.item.mailbox.name"),
                ButtonItems.hint("feature.mainmenu.item.mailbox.command")
        );
    }

    @Override
    public void open(OMCPlayer player) {
        MailboxCommand.homeMailbox(player);
    }
}
