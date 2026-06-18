package fr.openmc.core.features.mailboxes.letter;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.util.List;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getPlayerName;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.nonItalic;
import static fr.openmc.core.utils.text.DateUtils.formatRelativeDate;
import static fr.openmc.core.utils.text.InputUtils.pluralize;
import static fr.openmc.core.utils.text.messages.TranslationManager.translation;

@Getter
@SuppressWarnings("UnstableApiUsage")
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
                nonItalic(translation(
                        "feature.mailboxes.letter.contains",
                        Component.text(itemsCount).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                        Component.text(pluralize("item", itemsCount)).color(NamedTextColor.DARK_GREEN)
                ).color(NamedTextColor.DARK_GREEN))
        ));
        TooltipDisplay tooltipDisplay = TooltipDisplay.tooltipDisplay().addHiddenComponents(
                DataComponentTypes.PROFILE
        ).build();
        this.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);
        this.setItemMeta(skullMeta);
    }
}
