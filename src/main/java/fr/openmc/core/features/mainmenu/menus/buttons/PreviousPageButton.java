package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.features.mainmenu.menus.Page1;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class PreviousPageButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(81);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(ButtonItems.name("feature.mainmenu.item.previous_page"));
    }

    @Override
    public boolean runNextTick() {
        return false;
    }

    @Override
    public void open(OMCPlayer player) {
        PacketMenuLib.openMenu(new Page1(player), player);
    }
}