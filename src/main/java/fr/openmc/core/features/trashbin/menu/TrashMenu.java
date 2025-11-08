package fr.openmc.core.features.trashbin.menu;

import com.j256.ormlite.stmt.query.In;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    int DROP_END = 17;
    int FILLER_START = 18;
    int FILLER_END = 24;
    int VALIDATE = 25;
    int CANCEL = 26;

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
            p.sendMessage("Objets détruits");
        }

        if (clickedSlot == CANCEL) {
            returnItems(p, e.getInventory());
            p.sendMessage("Objets retourner dans votre inventaire");
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();
        returnItems((Player) event.getPlayer(), inv);

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {

        Map<Integer, ItemBuilder> content = new HashMap<>();

        ItemBuilder filler = new ItemBuilder(this, Material.WHITE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.itemName(Component.text(""));
        });
        ItemBuilder validate = new ItemBuilder(this, Material.GREEN_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.itemName(Component.text("Détruire les objets"));
        });
        ItemBuilder cancel = new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.itemName(Component.text("Annuler"));
        });

        for (int i = FILLER_START; i <= FILLER_END; i++) {
            content.put(i, filler);
        }

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
