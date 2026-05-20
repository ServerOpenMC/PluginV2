package fr.openmc.core.features.mailboxes.utils;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class MailboxMenuManager {
    public static ItemBuilder getBtn(Menu menu, String symbol, String nameKey, String customModelName, NamedTextColor color, boolean bold) {
        Component itemName = Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text(symbol, color))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.space())
                .append(TranslationManager.translation(nameKey).color(color));
        return new ItemBuilder(menu, CustomItemRegistry.getByName(customModelName).getBest(), meta -> {
            meta.displayName(itemName.decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, bold));
            meta.setMaxStackSize(1);
        });
    }

    public static ItemBuilder cancelBtn(Menu menu) {
        return getBtn(menu, "✘", "feature.mailboxes.menu.button.cancel", "omc_menus:mailbox_cancel_btn", NamedTextColor.DARK_RED, true);
    }

    public static ItemStack nextPageBtn() {
        Component name = TranslationManager.translation("feature.mailboxes.menu.button.next_page_arrow")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        ItemStack item = CustomItemRegistry.getByName("omc_menus:mailbox_arrow_right").getBest();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack previousPageBtn() {
        Component name = TranslationManager.translation("feature.mailboxes.menu.button.previous_page_arrow")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        ItemStack item = CustomItemRegistry.getByName("omc_menus:mailbox_arrow_left").getBest();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemBuilder acceptBtn(Menu menu) {
        return getBtn(menu, "✔", "feature.mailboxes.menu.button.accept", "omc_menus:mailbox_accept_btn", NamedTextColor.DARK_GREEN, true);
    }

    public static ItemBuilder sendBtn(Menu menu) {
        return getBtn(menu, "✉", "feature.mailboxes.menu.button.send", "omc_menus:mailbox_send", NamedTextColor.DARK_AQUA, true);
    }

    public static ItemBuilder refuseBtn(Menu menu) {
        ItemBuilder item = getBtn(menu, "✘", "feature.mailboxes.menu.button.refuse", "omc_menus:mailbox_refuse_btn", NamedTextColor.DARK_RED, true);
        item.editMeta(
                meta -> meta.lore(List.of(TranslationManager.translation("feature.mailboxes.menu.refuse_warning")
                        .decoration(TextDecoration.ITALIC, false)))
        );
        return item;
    }

    public static ItemBuilder homeBtn(Menu menu) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TranslationManager.translation("feature.mailboxes.menu.button.home_arrow")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return new ItemBuilder(menu, item).setOnClick(e -> new HomeMailbox(menu.getOwner()).open());
    }

    public static HashMap<Integer, ItemBuilder> getPaginatedButtons(Menu menu) {
        HashMap<Integer, ItemBuilder> buttons = new HashMap<>();

        buttons.put(48, new ItemBuilder(menu, CustomItemRegistry.getByName("omc_menus:mailbox_arrow_left").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("feature.mailboxes.menu.pagination.previous"));
        }).setPreviousPageButton());

        buttons.put(49, new ItemBuilder(menu, CustomItemRegistry.getByName("omc_menus:mailbox_cancel_btn").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("feature.mailboxes.menu.pagination.close"));
        }).setCloseButton());

        buttons.put(50, new ItemBuilder(menu, CustomItemRegistry.getByName("omc_menus:mailbox_arrow_right").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("feature.mailboxes.menu.pagination.next"));
        }).setNextPageButton());

        return buttons;
    }

    public static void sendConfirmMenuToCancelLetter(Player player, Letter letter) {
        new ConfirmMenu(player,
                () -> {
                    PendingMailbox.cancelLetter(player, letter.getLetterId());
                    new PendingMailbox(player).open();
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.mailboxes.menu.cancel.success",
                                    Component.text(letter.getLetterId()).color(NamedTextColor.GREEN)
                            ).color(NamedTextColor.GREEN),
                            Prefix.MAILBOX,
                            MessageType.SUCCESS,
                            false
                    );
                },
                player::closeInventory,
                List.of(TranslationManager.translation(
                        "feature.mailboxes.menu.cancel.confirm",
                        Component.text(letter.getLetterId()).color(NamedTextColor.RED)
                ).color(NamedTextColor.RED)),
                List.of(TranslationManager.translation(
                        "feature.mailboxes.menu.cancel.cancel",
                        Component.text(letter.getLetterId()).color(NamedTextColor.GREEN)
                ).color(NamedTextColor.GREEN))
        ).open();
    }
}