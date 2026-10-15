package fr.openmc.core.features.mainmenu.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.api.packetmenulib.events.InventoryCloseEvent;
import fr.openmc.api.packetmenulib.menu.ClickType;
import fr.openmc.api.packetmenulib.menu.InventoryType;
import fr.openmc.api.packetmenulib.menu.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.mainmenu.menus.buttons.*;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page1 implements Menu {
    private final List<ButtonMenu> buttons = List.of(
            new CityButton(), new QuestsButton(), new MilestonesButton(), new ContestButton(),
            new ShopButton(), new HomeButton(), new ProfileButton(), new SettingsButton(),
            new MailboxButton(), new AdvancementsButton(), new NextPageButton()
    );

    private final Map<Integer, ItemStack> content = new HashMap<>();

    public Page1(OMCPlayer player) {
        for (ButtonMenu button : buttons) {
            for (int slot : button.slots()) {
                content.put(slot, button.itemAt(player, slot));
            }
        }
    }

    @Override
    public Component getTitle(OMCPlayer player) {
        boolean hasCity = CityManager.getPlayerCity(player.getUniqueId()) != null;
        return Component.text(FontImageWrapper.replaceFontImages(
                hasCity
                        ? ":offset_-26::omc_main_menu_page_1:"
                        : ":offset_-26::omc_main_menu_page_1_sans_ville:"
        ));
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.GENERIC_9X6;
    }

    @Override
    public Map<Integer, ItemStack> getContent() {
        return content;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        OMCPlayer player = OMCPlayer.of(event.player());
        if (event.clickType() == ClickType.CLICK_OUTSIDE) {
            PacketMenuLib.closeMenu(player);
            return;
        }

        if (event.clickType() != ClickType.LEFT_CLICK) return;

        if (DreamUtils.isInDreamWorld(player.getPlayer())) {
            PacketMenuLib.closeMenu(player);
            player.message().sendError(
                    TranslationManager.translation("feature.mainmenu.message.dream_world")
                            .color(NamedTextColor.RED)
            );
            return;
        }

        buttons.stream()
                .filter(btn -> btn.slots().contains(event.slot()))
                .findFirst()
                .ifPresent(btn -> {
                    if (btn.closesMenu()) PacketMenuLib.closeMenu(player);

                    if (btn.runNextTick()) {
                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> btn.open(player));
                    } else {
                        btn.open(player);
                    }
                });
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }

    @Override
    public boolean isCursorItemEnabled() {
        return false;
    }
}
