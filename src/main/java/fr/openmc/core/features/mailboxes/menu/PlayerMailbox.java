package fr.openmc.core.features.mailboxes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMailbox extends PaginatedMenu {

    public PlayerMailbox(Player player) {
        super(player);
    }

    @Override
    public @NotNull String getName() {
        return "Boite aux lettres";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::player_mailbox:");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getBottomSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        MailboxManager.getReceivedLetters(getOwner()).forEach(letter ->
                items.add(new ItemBuilder(this, letter.toLetterHead())
                        .setOnClick(e -> new LetterMenu(getOwner(), letter).open()))
        );

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();

        buttons.put(45, MailboxMenuManager.homeBtn(this));
        buttons.putAll(MailboxMenuManager.getPaginatedButtons(this));

        return buttons;
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
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public int getSizeOfItems() {
        return MailboxManager.getReceivedLetters(getOwner()).size();
    }
}
