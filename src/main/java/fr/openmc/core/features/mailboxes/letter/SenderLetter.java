package fr.openmc.core.features.mailboxes.letter;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;
import static fr.openmc.core.utils.DateUtils.formatRelativeDate;

@Getter
public class SenderLetter extends ItemStack {
    private final UUID uniqueId;

    public SenderLetter(OfflinePlayer player, UUID uniqueId, int itemsCount, LocalDateTime sentAt, boolean refused) {
        super(Material.PLAYER_HEAD, 1);
        this.uniqueId = uniqueId;
        SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(getStatus(refused));
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(colorText("➡ Cliquez pour annuler", NamedTextColor.YELLOW, true));
        lore.add(getPlayerName(player));
        lore.add(colorText(formatRelativeDate(sentAt) + ", " + itemsCount + " " + getItemCount(itemsCount), NamedTextColor.DARK_GRAY, true));
        skullMeta.lore(lore);
        this.setItemMeta(skullMeta);
    }

    public static Component getStatus(boolean refused) {
        NamedTextColor color = refused ? NamedTextColor.DARK_RED : NamedTextColor.DARK_AQUA;
        Component status = Component.text("[", NamedTextColor.DARK_GRAY)
                                    .append(Component.text(refused ? "❌" : "⌚", color))
                                    .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                    .append(Component.text(refused ? "Refusée" : "En attente", color));
        return nonItalic(status);
    }

}
