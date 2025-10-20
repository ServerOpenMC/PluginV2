package fr.openmc.core.features.mailboxes.utils;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.defaultmenu.ConfirmMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.nonItalic;

@SuppressWarnings("UnstableApiUsage")
public class MailboxMenuManager {
    public static final Key openmcKey = Key.key("openmc", "menu");
    private static final Material customMaterial = Material.BARRIER;

    public static Component getInvTitle(String title) {
        return Component.text(title, NamedTextColor.WHITE).font(openmcKey);
    }

    public static ItemStack getCustomItem(Component name, int data) {
        return getCustomItem(name, data, null);
    }

    public static ItemStack getCustomItem(Component name, int data, List<Component> lore) {
        ItemStack item = new ItemStack(customMaterial);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(data).build());

        ItemMeta meta = item.getItemMeta();
        meta.displayName(nonItalic(name));
        if (lore != null)
            meta.lore(lore);

        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemBuilder transparentItem(Menu menu) {
        return new ItemBuilder(menu, CustomItemRegistry.getByName("omc_homes:omc_homes_invisible").getBest()).hideTooltip(true);
    }

    public static ItemBuilder getBtn(Menu menu, String symbol, String name, String customModelName, NamedTextColor color, boolean bold) {
        Component itemName = Component.text("[", NamedTextColor.DARK_GRAY).append(Component.text(symbol, color)).append(Component.text("]", NamedTextColor.DARK_GRAY)).append(Component.text(" " + name, color));
        return new ItemBuilder(menu, CustomItemRegistry.getByName(customModelName).getBest(), meta -> {
            meta.displayName(bold ? itemName.decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false) : itemName);
            meta.setMaxStackSize(1);
        });
    }

    public static ItemBuilder cancelBtn(Menu menu) {
        return getBtn(menu, "✘", "Annuler", "omc_menus:mailbox_cancel_btn", NamedTextColor.DARK_RED, true);
    }

    public static ItemStack nextPageBtn() {
        Component name = Component.text("Next page ➡", NamedTextColor.GOLD, TextDecoration.BOLD);
        return getCustomItem(name, 2003);
    }

    public static ItemStack previousPageBtn() {
        Component name = Component.text("⬅ Previous page", NamedTextColor.GOLD, TextDecoration.BOLD);
        return getCustomItem(name, 2004);
    }

    public static ItemBuilder acceptBtn(Menu menu) {
        return getBtn(menu, "✔", "Accepter", "omc_menus:mailbox_accept_btn", NamedTextColor.DARK_GREEN, true);
    }

    public static ItemBuilder sendBtn(Menu menu) {
        return getBtn(menu, "✉", "Envoyer", "omc_menus:mailbox_send", NamedTextColor.DARK_AQUA, true);
    }

    public static ItemBuilder refuseBtn(Menu menu) {
        return getBtn(menu, "✘", "Refuser", "omc_menus:mailbox_refuse_btn", NamedTextColor.DARK_RED, true);
    }

    public static ItemBuilder homeBtn(Menu menu) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("⬅ Home", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return new ItemBuilder(menu, item).setOnClick(e -> new HomeMailbox(menu.getOwner()).open());
    }

    public static HashMap<Integer, ItemBuilder> getPaginatedButtons(Menu menu) {
        HashMap<Integer, ItemBuilder> buttons = new HashMap<>();

        buttons.put(48, new ItemBuilder(menu, CustomItemRegistry.getByName("omc_menus:mailbox_arrow_left").getBest(), meta -> {
            meta.displayName(Component.text("§6§l⬅ Page précédente"));
        }).setPreviousPageButton());

        buttons.put(49, new ItemBuilder(menu, CustomItemRegistry.getByName("omc_menus:mailbox_cancel_btn").getBest(), meta -> {
            meta.displayName(Component.text("§8§l[§c§l✖§8§l] §c§lFermer"));
        }).setCloseButton());

        buttons.put(50, new ItemBuilder(menu, CustomItemRegistry.getByName("omc_menus:mailbox_arrow_right").getBest(), meta -> {
            meta.displayName(Component.text("§6§lPage suivante ➡"));
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
                            Component.text("Vous avez annulé la mailbox #" + letter.getLetterId(), NamedTextColor.GREEN),
                            Prefix.MAILBOX,
                            MessageType.SUCCESS,
                            false
                    );
                },
                player::closeInventory,
                List.of(Component.text("Confirmer l'annulation de la mailbox #" + letter.getLetterId(), NamedTextColor.RED)),
                List.of(Component.text("Annuler l'annulation de la mailbox #" + letter.getLetterId(), NamedTextColor.GREEN))
        ).open();
    }
}