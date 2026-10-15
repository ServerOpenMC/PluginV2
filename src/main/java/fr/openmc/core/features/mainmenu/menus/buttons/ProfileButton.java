package fr.openmc.core.features.mainmenu.menus.buttons;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.features.profile.menu.ProfileMenu;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Set;

public class ProfileButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(59, 60, 61, 62, 68, 69, 70, 71, 77, 78, 79, 80);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(
                ButtonItems.name("feature.mainmenu.item.profile.name"),
                ButtonItems.gray("feature.mainmenu.item.profile.command")
        );
    }

    @Override
    public ItemStack itemAt(OMCPlayer player, int slot) {
        if (slot != 60) return item(player);

        ItemStack head = CustomStack.getInstance("omc_main_menu:player_head").getItemStack();
        head.editMeta(meta -> {
            meta.customName(ButtonItems.name("feature.mainmenu.item.profile.name")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(ButtonItems.red("feature.mainmenu.item.profile.lore")));
            if (meta instanceof SkullMeta skull) skull.setOwningPlayer(player);
        });
        return head;
    }

    @Override
    public boolean closesMenu() {
        return true;
    }

    @Override
    public void open(OMCPlayer player) {
        new ProfileMenu(player).open();
    }
}
