package fr.openmc.api.menulib.template;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.List;
import java.util.function.Function;

public class ItemMenuTemplate {
    public static final Function<Menu, ItemMenuBuilder> BTN_PREVIOUS_PAGE_WHITE = (menu) ->
            new ItemMenuBuilder(menu, OMCRegistry.CUSTOM_ITEMS.MAILBOX_ARROW_LEFT, meta ->
                    meta.displayName(Component.text("⬅ Page précédente",
                    NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))
            ).setPreviousPageButton();

    public static final Function<Menu, ItemMenuBuilder> BTN_NEXT_PAGE_WHITE = (menu) ->
            new ItemMenuBuilder(menu, OMCRegistry.CUSTOM_ITEMS.MAILBOX_ARROW_RIGHT, meta ->
                    meta.displayName(Component.text("Page suivante ➡",
                    NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))
            ).setNextPageButton();

    public static final Function<Menu, ItemMenuBuilder> BTN_PREVIOUS_PAGE_ORANGE = (menu) ->
            new ItemMenuBuilder(menu, OMCRegistry.CUSTOM_ITEMS.ICON_BACK_ORANGE, meta ->
                    meta.displayName(TranslationManager.translation("messages.menus.previous_page"))
            ).setPreviousPageButton();

    public static final Function<Menu, ItemMenuBuilder> BTN_NEXT_PAGE_ORANGE = (menu) ->
            new ItemMenuBuilder(menu, OMCRegistry.CUSTOM_ITEMS.ICON_NEXT_ORANGE, meta ->
                    meta.displayName(TranslationManager.translation("messages.menus.next_page"))
            ).setNextPageButton();

    public static final Function<Menu, ItemMenuBuilder> BTN_CANCEL = (menu) ->
            new ItemMenuBuilder(menu, OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL, meta ->
                    meta.displayName(TranslationManager.translation("messages.menus.close"))
            ).setCloseButton();

    public static final Function<Menu, ItemMenuBuilder> BTN_CLOSE = (menu) ->
            btn(menu, "✘", "Annuler", OMCRegistry.CUSTOM_ITEMS.MAILBOX_CANCEL_BTN, NamedTextColor.DARK_RED, true)
                    .setCloseButton();

    public static final Function<Menu, ItemMenuBuilder> BTN_MAILBOX_ACCEPT = (menu) ->
            btn(menu, "✔", "Accepter", OMCRegistry.CUSTOM_ITEMS.MAILBOX_ACCEPT_BTN, NamedTextColor.DARK_GREEN, true);

    public static final Function<Menu, ItemMenuBuilder> BTN_MAILBOX_SEND = (menu) ->
            btn(menu, "✉", "Envoyer", OMCRegistry.CUSTOM_ITEMS.MAILBOX_SEND, NamedTextColor.DARK_AQUA, true);

    public static final Function<Menu, ItemMenuBuilder> BTN_MAILBOX_HOME = (menu) ->
            new ItemMenuBuilder(menu, Material.CHEST, meta -> {
        meta.displayName(Component.text("⬅ Home", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.setMaxStackSize(1);
    }).setOnClick(e -> new HomeMailbox(menu.getOwner()).open());


    public static ItemMenuBuilder btn(Menu menu, String symbol, String name, List<Component> lore, CustomItem customItem, NamedTextColor color, boolean bold) {
        ItemMenuBuilder item = btn(menu, symbol, name, customItem, color, bold);

        item.editMeta(
                meta -> meta.lore(lore)
        );
        return item;
    }

    public static ItemMenuBuilder btn(Menu menu, String symbol, String name, CustomItem customItem, NamedTextColor color, boolean bold) {
        Component itemName = Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text(symbol, color))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" " + name, color));

        return new ItemMenuBuilder(menu, customItem, meta -> {
            meta.displayName(itemName
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, bold));
            meta.setMaxStackSize(1);
        });
    }
}
