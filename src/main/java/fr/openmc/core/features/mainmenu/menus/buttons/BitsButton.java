package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.bits.commands.BitsCommands;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BitsButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(84);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                TranslationManager.translation("feature.mainmenu.item.bits").color(NamedTextColor.BLUE),
                ButtonItems.gray("feature.mainmenu.item.bits.command"));
    }

    @Override
    public void open(OMCPlayer player) {
        BitsCommands.mainCommand(player);
    }
}