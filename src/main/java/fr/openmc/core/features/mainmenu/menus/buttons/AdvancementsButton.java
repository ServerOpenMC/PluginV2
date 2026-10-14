package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.listeners.MainMenuListener;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class AdvancementsButton implements ButtonMenu {
    @Override
    public Set<Integer> slots() {
        return Set.of(88);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        return ButtonItems.item(ButtonItems.name("feature.mainmenu.item.advancements.name"));
    }

    @Override
    public boolean closesMenu() {
        return true;
    }

    @Override
    public void open(OMCPlayer player) {
        PacketMenuLib.closeMenu(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            MainMenuListener.setModeAdvancement(player);
            Component message = TranslationManager.translation(
                    "feature.mainmenu.message.advancements_hint",
                    Component.keybind("key.advancements").color(NamedTextColor.YELLOW)
            ).color(NamedTextColor.GREEN);
            player.sendActionBar(message);
        });
    }
}
