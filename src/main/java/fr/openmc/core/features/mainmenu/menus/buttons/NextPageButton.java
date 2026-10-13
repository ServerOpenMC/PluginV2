package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.features.mainmenu.menus.Page2;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class NextPageButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(89);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.hitbox(ButtonItems.name("feature.mainmenu.item.next_page"));
    }

    @Override
    public boolean runNextTick() {
        return false;
    }

    @Override
    public void open(OMCPlayer player) {
        PacketMenuLib.openMenu(new Page2(player), player);
    }
}
