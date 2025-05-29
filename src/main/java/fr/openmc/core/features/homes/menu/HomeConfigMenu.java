package fr.openmc.core.features.homes.menu;

import fr.openmc.api.input.signgui.SignGUI;
import fr.openmc.api.input.signgui.exception.SignGUIVersionException;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customfonts.CustomFonts;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeConfigMenu extends Menu {

    private final Home home;

    public HomeConfigMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home_settings%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();
        Player player = getOwner();

        try {
            content.put(4, home.getIconItem());

            content.put(20, new ItemBuilder(this, HomeUtil.getRandomsIcons(), itemMeta -> {
                itemMeta.setDisplayName("§aChanger l'icône");
                itemMeta.setLore(List.of(ChatColor.GRAY + "■ §aClique §2gauche §apour changer l'icône de votre home"));
            }).setNextMenu(new HomeChangeIconMenu(player, home)));

            content.put(22, new ItemBuilder(this, Material.NAME_TAG, itemMeta -> {
                itemMeta.displayName(Component.text("Changer le nom", NamedTextColor.GREEN).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

                TextComponent lore = Component.text()
                        .append(Component.text("■ ", NamedTextColor.GRAY))
                        .append(Component.text("Clique ", NamedTextColor.GREEN))
                        .append(Component.text("gauche ", NamedTextColor.DARK_GREEN))
                        .append(Component.text("pour changer le nom de votre home", NamedTextColor.GREEN))
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build();
                itemMeta.lore(Collections.singletonList(lore));
            }).setOnClick(e -> {
                String[] lines = {
                        "",
                        " ᐱᐱᐱᐱᐱᐱᐱ ",
                        "Entrez votre",
                        "nom ci dessus"
                };

                SignGUI gui;
                try {
                    gui = SignGUI.builder()
                            .setLines(lines)
                            .setType(ItemUtils.getSignType(player))
                            .setHandler((p, result) -> {
                                String input = result.getLine(0);

                                if (HomeUtil.checkName(player, input))
                                    return Collections.emptyList();

                                if (HomesManager.getHomesNames(p.getUniqueId()).contains(input)) {
                                    TextComponent message = Component.text("Tu as déjà un home avec ce nom.", NamedTextColor.RED);
                                    MessagesManager.sendMessage(player, message, Prefix.HOME, MessageType.ERROR, true);
                                    return Collections.emptyList();
                                }

                                TextComponent message = Component.text()
                                        .append(Component.text("Ton home ", NamedTextColor.GREEN))
                                        .append(Component.text(home.getName(), NamedTextColor.YELLOW))
                                        .append(Component.text(" a été renommé en ", NamedTextColor.GREEN))
                                        .append(Component.text(input, NamedTextColor.YELLOW))
                                        .append(Component.text(".", NamedTextColor.GREEN))
                                        .build();

                                MessagesManager.sendMessage(player, message, Prefix.HOME, MessageType.SUCCESS, true);
                                HomesManager.getInstance().renameHome(home, input);

                                return Collections.emptyList();
                            })
                            .build();
                } catch (SignGUIVersionException ex) {
                    throw new RuntimeException(ex);
                }

                gui.open(player);
            }));

            content.put(24, new ItemBuilder(this, CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bin_red").getBest(), itemMeta -> {
                itemMeta.setDisplayName(CustomFonts.getBest("omc_homes:bin", "§c🗑") + " §cSupprimer le home");
                itemMeta.setLore(List.of(ChatColor.GRAY + "■ §cClique §4gauche §cpour supprimer votre home"));
            }).setNextMenu(new HomeDeleteConfirmMenu(getOwner(), home)));

            content.put(36, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setNextMenu(new HomeMenu(player)));
            content.put(44, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());

            return content;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
