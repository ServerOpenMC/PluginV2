package fr.openmc.core.features.trashbin.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TrashMenu extends Menu {
    /**
     * Constructs a new Menu for the specified player.
     *
     * @param owner The {@link Player} who owns the menu
     */
    public TrashMenu(Player owner) {
        super(owner);
    }

    int DROP_START = 0;
    int DROP_END = 24;
    int CANCEL = 25;
    int VALIDATE = 26;

    public void destroyItems(Inventory inv) {
        for (int i = DROP_START; i <= DROP_END; i++) {
            inv.setItem(i, null);
        }
    }
    public void returnItems(Player p, Inventory inv) {
        for (int i = DROP_START; i <= DROP_END; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                p.getInventory().addItem(item);
                inv.setItem(i, null);
            }
            inv.close();
        }
    }

    @Override
    public @NotNull String getName() {
        return "Poubelle";
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        int clickedSlot = e.getRawSlot();

        Inventory trashBin = e.getView().getTopInventory();
        Inventory clickedInvetory = e.getClickedInventory();

        if (!clickedInvetory.equals(trashBin)) {
            e.setCancelled(false);
        }

        if (clickedSlot < 0 || clickedSlot > CANCEL) {
            return;
        }

        if (clickedSlot == VALIDATE) {
            destroyItems(e.getInventory());
            MessagesManager.sendMessage(p, Component.text("Objets détruits"), Prefix.OPENMC, MessageType.INFO,  true);
        }

        if (clickedSlot == CANCEL) {
            returnItems(p, e.getInventory());
            MessagesManager.sendMessage(p, Component.text("Objets retourné dans votre inventaire"), Prefix.OPENMC, MessageType.INFO,  true);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();
        returnItems(player, inv);

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {

        Map<Integer, ItemBuilder> content = new HashMap<>();

        ItemBuilder validate = new ItemBuilder(this, Material.GREEN_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.itemName(Component.text("Détruire les objets"));
        });
        ItemBuilder cancel = new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.itemName(Component.text("Annuler"));
        });

        content.put(VALIDATE, validate);
        content.put(CANCEL, cancel);

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        Stream<Integer> takable = IntStream.rangeClosed(DROP_START, DROP_END).boxed();
        return Stream.concat(takable, MenuUtils.getInventoryItemSlots().stream()).toList();
    }

}
