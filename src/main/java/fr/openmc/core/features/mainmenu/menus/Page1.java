package fr.openmc.core.features.mainmenu.menus;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.api.packetmenulib.events.InventoryCloseEvent;
import fr.openmc.api.packetmenulib.menu.ClickType;
import fr.openmc.api.packetmenulib.menu.InventoryType;
import fr.openmc.api.packetmenulib.menu.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.contest.commands.ContestCommand;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.models.Contest;
import fr.openmc.core.features.homes.command.TpHome;
import fr.openmc.core.features.mailboxes.MailboxCommand;
import fr.openmc.core.features.mainmenu.listeners.PacketListener;
import fr.openmc.core.features.milestones.menus.MainMilestonesMenu;
import fr.openmc.core.features.quests.command.QuestCommand;
import fr.openmc.core.features.settings.command.SettingsCommand;
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Page1 implements Menu {

    private final Component title;
    private final Map<Integer, ItemStack> content;
    private static final Set<Integer> CITY_SLOTS = Set.of(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
    private static final Set<Integer> QUEST_SLOTS = Set.of(4, 5, 6, 7, 8, 13, 14, 15, 16, 17, 22, 23, 24, 25, 26);
    private static final Set<Integer> MILESTONES_SLOTS = Set.of(31, 32, 33, 34, 35, 40, 41, 42, 43, 44, 49, 50, 51, 52, 53);
    private static final Set<Integer> CONTEST_SLOTS = Set.of(54, 55, 56, 57, 58);
    private static final Set<Integer> SHOP_SLOTS = Set.of(63, 64, 65, 66, 67);
    private static final Set<Integer> HOME_SLOTS = Set.of(72, 73, 74, 75, 76);
    private static final Set<Integer> PROFILE_SLOTS = Set.of(59, 60, 61, 62, 68, 69, 70, 71, 77, 78, 79, 80);
    private static final int LEFT_ARROW_SLOT = 81;  // Not used in this menu, but in Page2
    private static final int RIGHT_ARROW_SLOT = 89;
    private static final Set<Integer> SETTINGS_SLOTS = Set.of(82, 83, 84);
    private static final Set<Integer> MAILBOX_SLOTS = Set.of(85, 86, 87);
    private static final int ADVANCEMENTS_SLOT = 88;

    public Page1(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        ItemStack cityItem = new ItemStack(Material.PAPER);
        cityItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.lore(List.of(Component.text("/city", NamedTextColor.DARK_GRAY)));
        });

        if (playerCity != null) {
            title = Component.text(FontImageWrapper.replaceFontImages(":offset_-26::omc_main_menu_page_1:"));
            cityItem.editMeta(meta -> meta.itemName(Component.text("Ville : " + playerCity.getName(), NamedTextColor.YELLOW)));
        } else {
            title = Component.text(FontImageWrapper.replaceFontImages(":offset_-26::omc_main_menu_page_1_sans_ville:"));
            cityItem.editMeta(meta -> meta.itemName(Component.text("Vous ne faites pas partie d'une ville.", NamedTextColor.GRAY)));
        }

        content = new HashMap<>();

        ItemStack advancementsItem = new ItemStack(Material.PAPER);
        advancementsItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Afficher les progrès", NamedTextColor.YELLOW));
        });
        content.put(ADVANCEMENTS_SLOT, advancementsItem);

        CITY_SLOTS.forEach(slot -> content.put(slot, cityItem));

        ItemStack questsItem = new ItemStack(Material.PAPER);
        questsItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Quêtes", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/quest", NamedTextColor.DARK_GRAY)));
        });
        QUEST_SLOTS.forEach(slot -> content.put(slot, questsItem));

        ItemStack milestonesItem = new ItemStack(Material.PAPER);
        milestonesItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Milestones", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("Découvrez les features dans des routes de progressions!", NamedTextColor.WHITE),
                    Component.text("/milestones", NamedTextColor.DARK_GRAY)));
        });
        MILESTONES_SLOTS.forEach(slot -> content.put(slot, milestonesItem));

        ItemStack contestItem = new ItemStack(Material.PAPER);
        Contest data = ContestManager.data;
        int phase = data.getPhase();
        if (phase != 1) {
            contestItem.editMeta(meta -> {
                meta.setItemModel(NamespacedKey.minecraft("air"));
                meta.itemName(Component.text(ChatColor.valueOf(data.getColor1()) + data.getCamp1() + " §8VS " + ChatColor.valueOf(data.getColor2()) + data.getCamp2()));
                meta.lore(List.of(
                                Component.text("§cFin dans " + DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY)),
                                Component.text("/contest", NamedTextColor.DARK_GRAY)
                        )
                );
            });
        } else {
            contestItem.editMeta(meta -> {
                meta.setItemModel(NamespacedKey.minecraft("air"));
                meta.itemName(Component.text("Concours", NamedTextColor.YELLOW));
                meta.lore(List.of(Component.text("/contest", NamedTextColor.DARK_GRAY)));
            });
        }
        CONTEST_SLOTS.forEach(slot -> content.put(slot, contestItem));

        ItemStack shopItem = new ItemStack(Material.PAPER);
        shopItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Boutique", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/adminshop", NamedTextColor.DARK_GRAY)));
        });
        SHOP_SLOTS.forEach(slot -> content.put(slot, shopItem));

        ItemStack homeItem = new ItemStack(Material.PAPER);
        homeItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Homes", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("/home", NamedTextColor.DARK_GRAY)));
        });
        HOME_SLOTS.forEach(slot -> content.put(slot, homeItem));

        ItemStack profilItem = new ItemStack(Material.PAPER);
        profilItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Profil", NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("En développement", NamedTextColor.RED)));
        });
        PROFILE_SLOTS.forEach(slot -> {
            if (slot != 60)
                content.put(slot, profilItem);
        });

        ItemStack playerHeadProfilItem = CustomStack.getInstance("omc_main_menu:player_head").getItemStack();
        playerHeadProfilItem.editMeta(meta -> {
            meta.customName(Component.text("Profil", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("En développement", NamedTextColor.RED)));
            if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(player);
            }
        });
        content.put(60, playerHeadProfilItem);

        ItemStack rightArrowItem = new ItemStack(Material.PAPER);
        rightArrowItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.itemName(Component.text("Page suivante", NamedTextColor.YELLOW));
        });
        content.put(RIGHT_ARROW_SLOT, rightArrowItem);

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
        if (CITY_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> CityCommands.mainCommand(player));
        } else if (QUEST_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> QuestCommand.onQuest(player));
        } else if (MILESTONES_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> new MainMilestonesMenu(player).open());
        } else if (CONTEST_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> ContestCommand.mainCommand(player));
        } else if (SHOP_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> AdminShopManager.openMainMenu(player));
        } else if (HOME_SLOTS.contains(slot)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> TpHome.home(player, null));
        } else if (PROFILE_SLOTS.contains(slot)) {
            PacketMenuLib.closeMenu(player);
            player.sendMessage(Component.text(FontImageWrapper.replaceFontImages("Les profils des joueurs sont toujours en développement :sad:."), NamedTextColor.RED));
        } else if (RIGHT_ARROW_SLOT == slot) {
            PacketMenuLib.openMenu(new Page2(), player);
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
