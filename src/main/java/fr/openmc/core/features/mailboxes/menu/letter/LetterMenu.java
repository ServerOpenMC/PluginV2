package fr.openmc.core.features.mailboxes.menu.letter;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.events.ClaimLetterEvent;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.*;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;

public class LetterMenu extends Menu {

    private ItemStack[] items;

    private final LetterHead letterHead;

    @Override
    public @NotNull String getName() {
        return "Lettre de " + letterHead.displayName();
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::letter_mailbox:");
    }

    public LetterMenu(Player player, LetterHead letterHead) {
        super(player);
        this.letterHead = letterHead;
        Letter letter = MailboxManager.getById(player, letterHead.getLetterId());
        this.items = BukkitSerializer.deserializeItemStacks(letter.getItems());
    }

    public static LetterHead getById(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null || letter.isRefused()) {
            sendFailureMessage(player, "La lettre n'a pas été trouvée.");
            return null;
        }
        return letter.toLetterHead();
    }

    public static void refuseLetter(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter != null && !letter.isRefused()) {
            if (letter.refuse()) {
                sendSuccessMessage(player, "La lettre a été refusée.");
                return;
            }
        }

        Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                .append(Component.text(id, NamedTextColor.RED))
                .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED));
        sendFailureMessage(player, message);
    }

    public void accept() {
        if (MailboxManager.deleteLetter(letterHead.getLetterId())) {
            Component message = Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(letterHead.getItemsCount(), NamedTextColor.GREEN))
                    .append(Component.text(" " + getItemCount(letterHead.getItemsCount()), NamedTextColor.DARK_GREEN));
            sendSuccessMessage(getOwner(), message);

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new ClaimLetterEvent(getOwner()));
            });

            HashMap<Integer, ItemStack> remainingItems = getOwner().getInventory().addItem(items);
            for (ItemStack item : remainingItems.values()) {
                getOwner().getWorld().dropItemNaturally(getOwner().getLocation(), item);
            }
        } else {
            Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                    .append(Component.text(letterHead.getLetterId(), NamedTextColor.RED))
                    .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED));
            sendFailureMessage(getOwner(), message);
        }
        getOwner().closeInventory();
    }

    public void refuse() {
        Component message = Component.text("Cliquez-ici", NamedTextColor.YELLOW)
                .clickEvent(getRunCommand("refuse " + letterHead.getLetterId()))
                .hoverEvent(getHoverEvent("Refuser la lettre #" + letterHead.getLetterId()))
                .append(Component.text(" si vous êtes sur de vouloir refuser la lettre.", NamedTextColor.GOLD));
        sendWarningMessage(getOwner(), message);
        getOwner().closeInventory();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();

        for (int i = 0; i < items.length; i++)
            content.put(i + 9, new ItemBuilder(this, items[i]));

        content.put(45, homeBtn(this));

        content.put(48, acceptBtn(this).setOnClick(e -> accept()));

        content.put(49, new ItemBuilder(this, letterHead));

        content.put(50, refuseBtn(this).setOnClick(e -> refuse()));

        content.put(53, cancelBtn(this).setOnClick(e -> {
            getOwner().closeInventory();
            sendFailureMessage(getOwner(), "La lettre a été annulée.");
        }));

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
