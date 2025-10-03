package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.customfonts.CustomFonts;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.openmc.core.features.homes.utils.HomeUtil.MAX_LENGTH_HOME_NAME;

public class HomeConfigMenu extends Menu {

    private final Home home;

    public HomeConfigMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Homes - Configuration";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::omc_homes_menus_home_settings:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
        Player player = getOwner();

        content.put(4, new ItemBuilder(this, home.getIconItem()));

        content.put(20, new ItemBuilder(this, home.getIcon().getItemStack(), itemMeta -> {
            itemMeta.displayName(Component.text("§aChanger l'icône"));
            itemMeta.lore(List.of(Component.text("§7■ §aClique §2gauche §apour changer l'icône de votre home")));
        }).setOnClick(inventoryClickEvent -> new HomeChangeIconMenu(player, home).open()));

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
        }).setOnClick(e -> DialogInput.send(getOwner(), Component.text("Entrez votre nouveau nom de home"), MAX_LENGTH_HOME_NAME, input -> {
            if (input == null) return;

            if (!HomeUtil.isValidHomeName(input)) return;

            if (HomesManager.getHomesNames(getOwner().getUniqueId()).contains(input)) {
                TextComponent message = Component.text("Tu as déjà un home avec ce nom.", NamedTextColor.RED);
                MessagesManager.sendMessage(player, message, Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            TextComponent message = Component.text()
                    .append(Component.text("Ton home ", NamedTextColor.GREEN))
                    .append(Component.text(home.getName(), NamedTextColor.YELLOW))
                    .append(Component.text(" a été renommé en ", NamedTextColor.GREEN))
                    .append(Component.text(input, NamedTextColor.YELLOW))
                    .append(Component.text(".", NamedTextColor.GREEN))
                    .build();

            MessagesManager.sendMessage(player, message, Prefix.HOME, MessageType.SUCCESS, true);
            HomesManager.renameHome(home, input);
        })));

        content.put(24, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bin_red")).getBest(), itemMeta -> {
            itemMeta.displayName(Component.text(CustomFonts.getBest("omc_homes:bin", "§c🗑") + " §cSupprimer le home"));
            itemMeta.lore(List.of(Component.text("§7■ §cClique §4gauche §cpour supprimer votre home")));
        }).setOnClick(inventoryClickEvent -> new HomeDeleteConfirmMenu(getOwner(), home).open()));

        content.put(36, new ItemBuilder(this, MailboxMenuManager.previousPageBtn(), true));
        content.put(44, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());

        return content;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
