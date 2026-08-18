package fr.openmc.core.features.bits.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.bits.models.BitsPlayer;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitsShopMenu extends Menu {

    private final BitsManager manager;

    public BitsShopMenu(Player owner) {
        super(owner);
        this.manager = OMCRegistry.FEATURES.BITS.get();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.bits.menu.shop.name");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        inventory.put(11, createBuyButton(OMCRegistry.CUSTOM_ITEMS.MODERN_BOX, 500));
        inventory.put(12, createBuyButton(OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_BOX, 500));
        inventory.put(14, createBuyButton(OMCRegistry.CUSTOM_ITEMS.OFFICE_BOX, 500));
        inventory.put(15, createBuyButton(OMCRegistry.CUSTOM_ITEMS.KITCHEN_BOX, 500));

        inventory.put(22, createBuyButton(OMCRegistry.CUSTOM_ITEMS.PELUCHE_ROOT, 2000));

        return inventory;
    }

    private ItemMenuBuilder createBuyButton(CustomItem item, double price) {
        Component itemName = item.getBest().getItemMeta().displayName();
        BitsPlayer bitsPlayer = manager.getBitsPlayer(getOwner().getUniqueId());
        double bits = bitsPlayer == null ? 0d : bitsPlayer.getBits();
        return new ItemMenuBuilder(this, item, itemMeta -> {
            itemMeta.displayName(itemName);
            itemMeta.lore(TranslationManager.translationLore("feature.bits.menu.shop.buy.lore",
                    itemName, Component.text(price).color(NamedTextColor.AQUA)));
        }).setOnClick(_ -> {
            if (bits < price) {
                MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.bits.menu.shop.not_enough_bits"),
                        Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }

            manager.withdrawBits(getOwner().getUniqueId(), price);
            ItemUtils.giveItem(getOwner(), item.getBest(), 1);
        });
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}