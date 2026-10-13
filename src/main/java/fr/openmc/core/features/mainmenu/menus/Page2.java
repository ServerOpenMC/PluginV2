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
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.mainmenu.menus.buttons.*;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page2 implements Menu {

    private final List<ButtonMenu> buttons = List.of(
            new ShopsDisabledButton(), new CompanyDisabledButton(), new LeaderboardButton(),
            new BankButton(), new ComingSoonButton(), new PreviousPageButton(),
            new SettingsButton(), new MailboxButton(), new AdvancementsButton()
    );

    private final Map<Integer, ItemStack> content = new HashMap<>();

    public Page2(OMCPlayer player) {
        for (ButtonMenu button : buttons) {
            for (int slot : button.slots()) {
                content.put(slot, button.itemAt(player, slot));
            }
        }
    }

    @Override
    public Component getTitle(OMCPlayer player) {
        return Component.text(FontImageWrapper.replaceFontImages(":offset_-26::omc_main_menu_page_2:"));
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
        Player player = event.player();
        OMCPlayer omcPlayer = OMCPlayer.of(player);

        if (event.clickType() == ClickType.CLICK_OUTSIDE) {
            PacketMenuLib.closeMenu(omcPlayer);
            return;
        }
        if (event.clickType() != ClickType.LEFT_CLICK) return;

        if (DreamUtils.isInDreamWorld(player)) {
            PacketMenuLib.closeMenu(omcPlayer);
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.mainmenu.message.dream_world")
                            .color(NamedTextColor.RED),
                    Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        buttons.stream()
                .filter(btn -> btn.slots().contains(event.slot()))
                .findFirst()
                .ifPresent(btn -> {
                    if (btn.closesMenu()) PacketMenuLib.closeMenu(omcPlayer);

                    if (btn.runNextTick()) {
                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> btn.open(omcPlayer));
                    } else {
                        btn.open(omcPlayer);
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