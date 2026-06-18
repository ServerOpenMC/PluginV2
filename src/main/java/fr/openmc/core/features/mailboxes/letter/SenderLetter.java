package fr.openmc.core.features.mailboxes.letter;

import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;
import static fr.openmc.core.utils.text.DateUtils.formatRelativeDate;
import static fr.openmc.core.utils.text.InputUtils.pluralize;

@Getter
public class SenderLetter extends ItemStack {

    public SenderLetter(OfflinePlayer player, int itemsCount, LocalDateTime sentAt, boolean refused) {
        super(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(getStatus(refused));
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(colorText(TranslationManager.translationString("feature.mailboxes.letter.cancel_hint"), NamedTextColor.YELLOW, true));
        lore.add(getPlayerName(player));
        lore.add(colorText(TranslationManager.translationString(
                "feature.mailboxes.letter.sent_info",
                Component.text(formatRelativeDate(sentAt)),
                Component.text(itemsCount),
                Component.text(pluralize("item", itemsCount))
        ), NamedTextColor.DARK_GRAY, true));
        skullMeta.lore(lore);
        this.setItemMeta(skullMeta);
    }

    public static Component getStatus(boolean refused) {
        NamedTextColor color = refused ? NamedTextColor.DARK_RED : NamedTextColor.DARK_AQUA;
        Component statusLabel = TranslationManager.translation(refused
                ? "feature.mailboxes.letter.status.refused"
                : "feature.mailboxes.letter.status.pending").color(color);
        Component status = Component.text("[", NamedTextColor.DARK_GRAY)
                                    .append(Component.text(refused ? "❌" : "⌚", color))
                                    .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                    .append(statusLabel);
        return nonItalic(status);
    }
}
