package fr.openmc.core.utils.bukkit;

import fr.openmc.api.menulib.MenuLib;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

/**
 * The {@code ItemBuilder} class is a utility for creating and customizing {@link ItemStack}
 * It provides methods to set item properties, and manage
 * metadata, making it easier to create interactive items
 */
public class ItemBuilder extends ItemStack {
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(ItemStack.of(material));
    }

    public ItemBuilder(ItemStack item) {
        this(item, null);
    }

    public ItemBuilder(Material material, Consumer<ItemMeta> itemMeta) {
        this(new ItemStack(material), itemMeta);
    }

    public ItemBuilder(ItemStack item, Consumer<ItemMeta> itemMeta) {
        super(item);
        meta = item.getItemMeta();
        if (itemMeta != null) itemMeta.accept(meta);
        setItemMeta(meta);
    }

    /**
     * Sets the unique identifier for the item using the specified {@code itemId}.
     * The identifier is stored in the item's {@link PersistentDataContainer} as a
     * {@link String} in a lower-case format, allowing it to be associated with
     * specific functionality in the menu system.
     *
     * @param itemId The unique identifier to associate with the item. This value is stored
     *               in a lower-case format within the item's {@link PersistentDataContainer}.
     * @return The current instance of {@link fr.openmc.api.menulib.utils.ItemMenuBuilder}, allowing for method chaining
     * when creating and customizing items.
     */
    public ItemBuilder setItemId(String itemId) {
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(MenuLib.getItemIdKey(), PersistentDataType.STRING, itemId.toLowerCase());
        setItemMeta(meta);
        return this;
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder hide(DataComponentType... typesToHide) {
        if (typesToHide == null) return this;

        if (this.hasData(DataComponentTypes.TOOLTIP_DISPLAY) && this.getData(DataComponentTypes.TOOLTIP_DISPLAY).hideTooltip())
            return this;

        TooltipDisplay tooltipDisplay = TooltipDisplay.tooltipDisplay().addHiddenComponents(typesToHide).build();
        this.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);

        return this;
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder hideTooltip(boolean hideTooltip) {
        if (this.getType().equals(Material.AIR)) return this;

        TooltipDisplay tooltipDisplay = TooltipDisplay.tooltipDisplay().hideTooltip(hideTooltip).build();
        this.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);

        return this;
    }
}
