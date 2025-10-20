package fr.openmc.core.features.mailboxes.letter;

import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.util.List;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getPlayerName;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.nonItalic;
import static fr.openmc.core.utils.DateUtils.formatRelativeDate;
import static fr.openmc.core.utils.InputUtils.pluralize;

@Getter
public class LetterHead extends ItemStack {
    private final int letterId;
    private final int itemsCount;
    private final ItemStack[] items;

    public LetterHead(OfflinePlayer player, int letterId, int itemsCount, LocalDateTime sentAt, ItemStack[] items) {
        super(Material.PLAYER_HEAD, 1);
        this.letterId = letterId;
        this.itemsCount = itemsCount;
        this.items = items;
        SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(getPlayerName(player));
        skullMeta.lore(List.of(
                nonItalic(Component.text(formatRelativeDate(sentAt), NamedTextColor.DARK_GRAY)),
                nonItalic(Component.text("➤ Contient ", NamedTextColor.DARK_GREEN)
                        .append(Component.text(itemsCount, NamedTextColor.GREEN, TextDecoration.BOLD))
                        .append(Component.text(pluralize("item", itemsCount), NamedTextColor.DARK_GREEN)))
        ));
        this.setItemMeta(skullMeta);
    }
}
