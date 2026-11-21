package fr.openmc.core.features.mainmenu.menus.buttons;

import com.destroystokyo.paper.profile.PlayerProfile;
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
        return Set.of(54, 55, 56, 57,
                63, 64, 65, 66,
                72, 73, 74, 75);
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
        if (slot != 54) return item(player);

        ItemStack head = CustomStack.getInstance("omc_main_menu:player_head").getItemStack();
        head.editMeta(meta -> {
            meta.customName(ButtonItems.name("feature.mainmenu.item.profile.name")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(ButtonItems.red("feature.mainmenu.item.profile.command")));
            if (meta instanceof SkullMeta skull && player != null && player.getPlayer() != null) {
                PlayerProfile profile = player.getPlayer().getPlayerProfile();
                if (profile.hasTextures()) {
                    skull.setPlayerProfile(profile);
                }
            }
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
