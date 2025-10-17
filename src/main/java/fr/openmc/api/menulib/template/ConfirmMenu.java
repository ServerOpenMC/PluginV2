package fr.openmc.api.menulib.template;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    private String texture = null;
    private InventorySize inventorySize = InventorySize.SMALLEST;
    private int posAcceptBtn = 5;
    private int posDenyBtn = 3;

    /**
     * Add Confirmation Menu, it must use it for all
     *
     * @param owner        Player for a Menu owner
     * @param methodAccept Run your action when Accept
     * @param methodDeny   Run your action when Accept
     * @param loreAccept   Put your lore for Accept
     * @param loreDeny     Run your lore for Deny
     */
    public ConfirmMenu(Player owner, Runnable methodAccept, Runnable methodDeny, List<Component> loreAccept, List<Component> loreDeny) {
        super(owner);
        this.accept = methodAccept != null ? methodAccept : () -> {
        };
        this.deny = methodDeny != null ? methodDeny : () -> {
        };
        this.loreAcceptMsg = loreAccept;
        this.loreDenyMsg = loreDeny;
    }

    /**
     * Add Confirmation Menu, it must use it for all
     *
     * @param owner        Player for a Menu owner
     * @param methodAccept Run your action when Accept
     * @param methodDeny   Run your action when Accept
     * @param loreAccept   Put your lore for Accept
     * @param loreDeny     Run your lore for Deny
     * @param texture      set textures
     * @param size         Set inventory size
     */
    public ConfirmMenu(Player owner, Runnable methodAccept, Runnable methodDeny, List<Component> loreAccept, List<Component> loreDeny, String texture, InventorySize size, int posAcceptBtn, int posDenyBtn) {
        super(owner);
        this.accept = methodAccept != null ? methodAccept : () -> {};
        this.deny = methodDeny != null ? methodDeny : () -> {};
        this.loreAcceptMsg = loreAccept;
        this.loreDenyMsg = loreDeny;
        this.texture = texture;
        this.inventorySize = size;
        this.posAcceptBtn = posAcceptBtn;
        this.posDenyBtn = posDenyBtn;
    }

    @Override
    public @NotNull String getName() {
        return "Menu de Confirmation";
    }

    @Override
    public String getTexture() {
        return texture == null ? FontImageWrapper.replaceFontImages("§r§f:offset_-8::confirm_menu:") : texture;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return inventorySize;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        List<Component> loreAccept = new ArrayList<>(loreAcceptMsg);

        loreAccept.add(Component.text("§e§lCLIQUEZ ICI POUR VALIDER"));

        List<Component> loreDeny = new ArrayList<>(loreDenyMsg);

        loreDeny.add(Component.text("§e§lCLIQUEZ ICI POUR REFUSER"));

        ItemStack refuseBtn = CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest();
        ItemStack acceptBtn = CustomItemRegistry.getByName("omc_menus:accept_btn").getBest();

        inventory.put(posDenyBtn, new ItemBuilder(this, refuseBtn, itemMeta -> {
            itemMeta.displayName(Component.text("§cRefuser"));
            itemMeta.lore(loreDeny);
        }).setOnClick(event -> {
            try {
                deny.run();
            } catch (Exception e) {
                MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
                player.closeInventory();
                throw new RuntimeException(e);
            }
        }));

        inventory.put(posAcceptBtn, new ItemBuilder(this, acceptBtn, itemMeta -> {
            itemMeta.displayName(Component.text("§aAccepter"));
            itemMeta.lore(loreAccept);
        }).setOnClick(event -> {
            try {
                accept.run();
            } catch (Exception e) {
                MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
                player.closeInventory();
                throw new RuntimeException(e);
            }
        }));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
