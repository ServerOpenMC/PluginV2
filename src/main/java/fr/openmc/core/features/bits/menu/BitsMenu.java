package fr.openmc.core.features.bits.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.bits.models.BitsPlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitsMenu extends Menu {
    private final BitsManager manager;

    public BitsMenu(Player owner) {
        super(owner);
        this.manager = OMCRegistry.FEATURES.BITS.get();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.bits.menu.main.name");
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

        BitsPlayer bitsPlayer = manager.getBitsPlayer(getOwner().getUniqueId());
        double bits = bitsPlayer == null ? 0d : bitsPlayer.getBits();

        inventory.put(12, new ItemMenuBuilder(this, Material.DIAMOND, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.bits.menu.main.bits"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.bits.menu.main.bits.lore",
                    Component.text(bits).color(NamedTextColor.AQUA),
                    Component.text(BitsManager.LINE_REQ / BitsManager.BITS_PER_LINE_REQ).color(NamedTextColor.AQUA)
            ));
        }));

        inventory.put(14, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_SHOP, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.bits.menu.main.shop"));
            itemMeta.lore(TranslationManager.translationLore("feature.bits.menu.main.shop.lore"));
        }).setOnClick(_ ->
                new BitsShopMenu(getOwner()).open()));

        inventory.put(18, new ItemMenuBuilder(this, Material.ARROW, true));

        return inventory;
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
