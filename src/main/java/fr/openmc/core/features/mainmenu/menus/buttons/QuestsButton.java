package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.features.quests.command.QuestCommand;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class QuestsButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(4, 5, 6, 7, 8, 13, 14, 15, 16, 17, 22, 23, 24, 25, 26);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(
                ButtonItems.name("feature.mainmenu.item.quests.name"),
                ButtonItems.hint("feature.mainmenu.item.quests.lore"));
    }

    @Override
    public void open(OMCPlayer player) {
        QuestCommand.onQuest(player);
    }
}
