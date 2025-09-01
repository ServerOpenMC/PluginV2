package fr.openmc.core.features.mainmenu.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.api.packetmenulib.events.InventoryCloseEvent;
import fr.openmc.api.packetmenulib.menu.ClickType;
import fr.openmc.api.packetmenulib.menu.InventoryType;
import fr.openmc.api.packetmenulib.menu.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.features.mailboxes.MailboxCommand;
import fr.openmc.core.features.mainmenu.listeners.PacketListener;
import fr.openmc.core.features.settings.command.SettingsCommand;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Page2 implements Menu {
    private static final Set<Integer> SHOPS_SLOTS = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8);
    private static final Set<Integer> COMPANY_SLOTS = Set.of(9, 10, 11, 12, 13, 14, 15, 16, 17);
    private static final Set<Integer> LEADERBOARD_SLOTS = Set.of(18, 19, 20, 21, 22, 23, 24, 25, 26);
    private static final Set<Integer> BANK_SLOTS = Set.of(27, 28, 29, 30, 31, 32, 33, 34, 35);
    private static final Set<Integer> COMING_SOON_1_SLOTS = Set.of(36, 37, 38, 39, 40, 41, 42, 43, 44);
    private static final Set<Integer> COMING_SOON_2_SLOTS = Set.of(45, 46, 47, 48, 49, 50, 51, 52, 53);
    private static final Set<Integer> COMING_SOON_3_SLOTS = Set.of(54, 55, 56, 57, 58, 59, 60, 61, 62);
    private static final Set<Integer> COMING_SOON_4_SLOTS = Set.of(63, 64, 65, 66, 67, 68, 69, 70, 71);
    private static final Set<Integer> COMING_SOON_5_SLOTS = Set.of(72, 73, 74, 75, 76, 77, 78, 79, 80);
    private static final int LEFT_ARROW_SLOT = 81;
    private static final int RIGHT_ARROW_SLOT = 89; // Not used in this menu, but in Page1
    private static final Set<Integer> SETTINGS_SLOTS = Set.of(82, 83, 84);
    private static final Set<Integer> MAILBOX_SLOTS = Set.of(85, 86, 87);
    private static final int ADVANCEMENTS_SLOT = 88;
    private final Component title;
    private final Map<Integer, ItemStack> content;

    public Page2() {
        title = Component.text(FontImageWrapper.replaceFontImages(":offset_-26::omc_main_menu_page_2:"));
        content = new HashMap<>();

        ItemStack advancementsItem = new ItemStack(Material.PAPER);
        advancementsItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Afficher les progrès", NamedTextColor.YELLOW));
        });
        content.put(ADVANCEMENTS_SLOT, advancementsItem);

        ItemStack leftArrowItem = new ItemStack(Material.PAPER);
        leftArrowItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Page précédente", NamedTextColor.YELLOW));
        });
        content.put(LEFT_ARROW_SLOT, leftArrowItem);

        ItemStack settingsItem = new ItemStack(Material.PAPER);
        settingsItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Paramètres", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/settings", NamedTextColor.DARK_GRAY)));
        });
        SETTINGS_SLOTS.forEach(slot -> content.put(slot, settingsItem));

        ItemStack mailboxItem = new ItemStack(Material.PAPER);
        mailboxItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Boîte aux lettres", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/mailbox home", NamedTextColor.DARK_GRAY)));
        });
        MAILBOX_SLOTS.forEach(slot -> content.put(slot, mailboxItem));

        ItemStack shopsItem = new ItemStack(Material.PAPER);
        shopsItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Shops (Désactivé)", NamedTextColor.YELLOW));
        });
        SHOPS_SLOTS.forEach(slot -> content.put(slot, shopsItem));

        ItemStack companyItem = new ItemStack(Material.PAPER);
        companyItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Entreprises (Désactivé)", NamedTextColor.YELLOW));
        });
        COMPANY_SLOTS.forEach(slot -> content.put(slot, companyItem));

        ItemStack leaderboardItem = new ItemStack(Material.PAPER);
        leaderboardItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Leaderboard", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/leaderboard", NamedTextColor.DARK_GRAY), Component.text("En cours de développement", NamedTextColor.RED)));
        });
        LEADERBOARD_SLOTS.forEach(slot -> content.put(slot, leaderboardItem));

        ItemStack bankItem = new ItemStack(Material.PAPER);
        bankItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Banque", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/banque", NamedTextColor.DARK_GRAY)));
        });
        BANK_SLOTS.forEach(slot -> content.put(slot, bankItem));

        ItemStack comingSoonItem = new ItemStack(Material.PAPER);
        comingSoonItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("À venir", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("Cette fonctionnalité est en cours de développement.", NamedTextColor.RED)));
        });
        COMING_SOON_1_SLOTS.forEach(slot -> content.put(slot, comingSoonItem));
        COMING_SOON_2_SLOTS.forEach(slot -> content.put(slot, comingSoonItem));
        COMING_SOON_3_SLOTS.forEach(slot -> content.put(slot, comingSoonItem));
        COMING_SOON_4_SLOTS.forEach(slot -> content.put(slot, comingSoonItem));
        COMING_SOON_5_SLOTS.forEach(slot -> content.put(slot, comingSoonItem));
    }

    @Override
    public Component getTitle() {
        return title;
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
        if (event.clickType() == ClickType.CLICK_OUTSIDE) {
            PacketMenuLib.closeMenu(player);
            return;
        }

        if (event.clickType() != ClickType.LEFT_CLICK) {
            return;
        }

        int slot = event.slot();
        if (LEFT_ARROW_SLOT == slot) {
            PacketMenuLib.openMenu(new Page1(player), player);
        } else if (SETTINGS_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> SettingsCommand.settings(player));
        } else if (MAILBOX_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> MailboxCommand.homeMailbox(player));
        } else if (ADVANCEMENTS_SLOT == slot) {
            PacketMenuLib.closeMenu(player);
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
                ClientboundUpdateAdvancementsPacket packet = PacketListener.getAdvancementPackets().get(nmsPlayer.getUUID());
                if (packet == null)
                    return;

                nmsPlayer.connection.send(packet);
                PacketListener.getEnabledAdvancements().add(nmsPlayer.getUUID());
                Component message = Component.text("Appuyez sur la touche '").color(NamedTextColor.GREEN)
                        .append(Component.keybind("key.advancements").color(NamedTextColor.YELLOW))
                        .append(Component.text("' pour ouvrir le menu des Avancements.", NamedTextColor.GREEN));
                player.sendActionBar(message);
            });
        } else if (COMPANY_SLOTS.contains(slot) || SHOPS_SLOTS.contains(slot)) {
            MessagesManager.sendMessage(player, Component.text("Les entreprises et shops on été désactivé :sad:.", NamedTextColor.RED), Prefix.OPENMC, MessageType.ERROR, true);
        } else if (LEADERBOARD_SLOTS.contains(slot)) {
            // TODO: Ajouter un menu de classement
            PacketMenuLib.closeMenu(player);
            player.sendMessage(Component.text(FontImageWrapper.replaceFontImages("Le menu de leaderboard est toujours en développement :sad:.\nVous pouvez toujours utiliser le /lb ou regarder les holograms dans le spawn."), NamedTextColor.RED));
        } else if (BANK_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> BankCommands.openBankMenu(player));
        } else if (COMING_SOON_1_SLOTS.contains(slot) || COMING_SOON_2_SLOTS.contains(slot)
                || COMING_SOON_3_SLOTS.contains(slot) || COMING_SOON_4_SLOTS.contains(slot)
                || COMING_SOON_5_SLOTS.contains(slot)) {
            PacketMenuLib.closeMenu(player);
            player.sendMessage(Component.text("Cette fonctionnalité est en cours de développement et sera bientôt disponible !", NamedTextColor.GOLD));
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }

    @Override
    public boolean isCursorItemEnabled() {
        return false;
    }
}
