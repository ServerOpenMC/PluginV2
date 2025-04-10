package fr.openmc.core.utils.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmMenu extends Menu {

    private final List<Component> loreAcceptMsg;
    private final List<Component> loreDenyMsg;
    private final Runnable accept;
    private final Runnable deny;

    /**
     * Add Confirmation Menu, it must use for all
     * @param owner Player for Menu owner
     * @param methodAccept Run your action when Accept
     * @param methodDeny Run your action when Accept
     * @param loreAccept Put your lore for Accept
     * @param loreDeny Run your lore for Deny
     */
    public ConfirmMenu(Player owner, Runnable methodAccept, Runnable methodDeny, List<Component> loreAccept, List<Component> loreDeny) {
        super(owner);
        this.accept = methodAccept != null ? methodAccept : () -> {};
        this.deny = methodDeny != null ? methodDeny : () -> {};
        this.loreAcceptMsg = loreAccept;
        this.loreDenyMsg = loreDeny;
    }

    @Override
    public @NotNull String getName() {
        if (PapiAPI.hasPAPI() && CustomItemRegistry.hasItemsAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-8%%img_confirm_menu%");
        } else {
            return "Confirmation";
        }
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        List<Component> loreAccept = new ArrayList<>(loreAcceptMsg);
        loreAccept.add(Component.text("§e§lCLIQUEZ ICI POUR VALIDER"));

        List<Component> loreDeny = new ArrayList<>(loreDenyMsg);
        loreDeny.add(Component.text("§e§lCLIQUEZ ICI POUR REFUSER"));

        ItemStack refuseBtn = CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest();
        ItemStack  acceptBtn = CustomItemRegistry.getByName("omc_menus:accept_btn").getBest();

        inventory.put(3, new ItemBuilder(this, refuseBtn, itemMeta -> {
            itemMeta.displayName(Component.text("§cRefuser"));
            itemMeta.lore(loreDeny);
        }).setOnClick(event -> {
            deny.run();
        }));

        inventory.put(5, new ItemBuilder(this, acceptBtn, itemMeta -> {
            itemMeta.displayName(Component.text("§aAccepter"));
            itemMeta.lore(loreAccept);
        }).setOnClick(event -> {
            accept.run();
        }));

        return inventory;
    }
}
