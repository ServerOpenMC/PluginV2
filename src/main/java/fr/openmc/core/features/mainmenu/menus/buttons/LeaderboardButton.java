package fr.openmc.core.features.mainmenu.menus.buttons;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class LeaderboardButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(18, 19, 20, 21, 22, 23, 24, 25, 26);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                ButtonItems.name("feature.mainmenu.item.leaderboard.name"),
                ButtonItems.gray("feature.mainmenu.item.leaderboard.command"));
    }

    @Override
    public boolean closesMenu() {
        return true;
    }

    @Override
    public boolean runNextTick() {
        return false;
    }

    @Override
    public void open(OMCPlayer player) {
        MessagesManager.sendMessage(player.getPlayer(),
                TranslationManager.translation(
                        "feature.mainmenu.message.leaderboard_dev",
                        Component.text(FontImageWrapper.replaceFontImages(":sad:"))
                ).color(NamedTextColor.RED),
                Prefix.OPENMC, MessageType.INFO, true);
    }
}