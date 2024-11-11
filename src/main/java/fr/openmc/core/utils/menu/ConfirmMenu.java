package fr.openmc.core.utils.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmMenu extends Menu {

    private final Menu menu;
    private final String loreAccept;
    private final String loreDeny;
    private final Runnable accept;
    private final Runnable deny;

    /**
     * Add Confirmation Menu, it must use for all
     * @param owner Player for Menu owner
     * @param oldMenu Open your Old Menu
     * @param methodAccept Run your action when Accept
     * @param methodDeny Run your action when Accept
     * @param loreAccept Put your lore for Accept
     * @param loreDeny Run your lore for Deny
     */
    public ConfirmMenu(Player owner, Menu oldMenu, Runnable methodAccept, Runnable methodDeny, String loreAccept, String loreDeny) {
        super(owner);
        this.accept = methodAccept;
        this.deny = methodDeny;
        this.menu = oldMenu;
        this.loreAccept = loreAccept;
        this.loreDeny = loreDeny;
    }

    @Override
    public @NotNull String getName() {
        return "Confirmation";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();

        List<Component> lore_accept = new ArrayList<>();
        lore_accept.add(Component.text(loreAccept));
        lore_accept.add(Component.text("§e§lCLIQUEZ ICI POUR VALIDER"));

        List<Component> lore_deny = new ArrayList<>();
        lore_deny.add(Component.text(loreDeny));
        lore_deny.add(Component.text("§e§lCLIQUEZ ICI POUR REFUSER"));

        inventory.put(2, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> {
            itemMeta.itemName(Component.text("§cRefuser"));
            itemMeta.lore(lore_deny);
        }).setOnClick(event -> {
            deny.run();
            menu.open();
        }));

        inventory.put(6, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> {
            itemMeta.itemName(Component.text("§aAccepter"));
            itemMeta.lore(lore_accept);
        }).setOnClick(event -> {
            accept.run();
            menu.open();
        }));


        return inventory;
    }
}
