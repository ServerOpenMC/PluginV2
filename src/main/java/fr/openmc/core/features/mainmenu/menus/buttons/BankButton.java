package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BankButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(4, 5, 6, 7, 8, 13, 14, 15, 16, 17, 22, 23, 24, 25, 26);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                ButtonItems.name("feature.mainmenu.item.bank.name"),
                ButtonItems.gray("feature.mainmenu.item.bank.command"));
    }

    @Override
    public void open(OMCPlayer player) {
        BankCommands.openBankMenu(player);
    }
}