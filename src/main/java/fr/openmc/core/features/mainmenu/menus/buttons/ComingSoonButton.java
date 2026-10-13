package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ComingSoonButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return IntStream.rangeClosed(36, 80).boxed().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(
                ButtonItems.name("feature.mainmenu.item.coming_soon.name"),
                ButtonItems.warn("feature.mainmenu.item.coming_soon.lore"));
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
                TranslationManager.translation("feature.mainmenu.message.coming_soon")
                        .color(NamedTextColor.GOLD),
                Prefix.OPENMC, MessageType.INFO, true);
    }
}