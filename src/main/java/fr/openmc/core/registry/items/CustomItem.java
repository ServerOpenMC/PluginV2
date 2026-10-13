package fr.openmc.core.registry.items;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.hooks.itemsadder.sprite.SpriteUtils;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.riftengine.api.scanner.items.ItemEntry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class CustomItem {
    @Getter
    private final String id;
    @Getter
    private ItemEntry itemEntry;
    @Getter
    private ObjectComponent sprite;

    // @Override afin d'ajouter des metas personnalisées
    @Getter
    private CustomItemMeta meta;

    public CustomItem(String id) {
        this.id = id;
        this.meta = null;
    }

    public CustomItem(CustomItemMeta meta) {
        this(meta.getId());
        this.meta = meta;
    }

    public abstract @NotNull ItemStack getVanilla();

    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance(getId());
        return stack != null ? stack.getItemStack() : null;
    }

    public CustomStack getCustomStack() {
        return CustomStack.getInstance(getId());
    }

    public CustomBlock getCustomBlock() {
        return CustomBlock.getInstance(getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ItemStack anotherItem) {
            Optional<CustomItem> citem = OMCRegistry.CUSTOM_ITEMS.get(anotherItem);

            if (citem.isEmpty()) return false;
            return citem.get().getId().equals(this.getId());
        }

        if (object instanceof String otherObjectName)
            return this.getId().equals(otherObjectName);

        if (object instanceof CustomItem citem)
            return citem.getId().equals(this.getId());

        return false;
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     *
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBest() {
        ItemStack item;
        if (!ItemsAdderHook.isEnable() || getItemsAdder() == null) {
            item = getVanilla();
        } else {
            item = getItemsAdder();
        }

        ItemUtils.setTag(item, CustomItemRegistry.CUSTOM_ITEM_KEY, this.getId());

        return item;
    }

    public void updateSprite() {
        // Seulement init si on est dans le runtime
        if (OMCPlugin.getInstance() != null) {
            this.itemEntry = OMCRegistry.SCANNERS.ITEMS.getFromNamespacedId(
                    ItemsAdderHook.getItemsAdderPath(OMCPlugin.getInstance().getDataPath()), id);
            ObjectComponent sprite = SpriteUtils.getSprite(this);
            if (sprite != null) {
                HoverEvent<?> hoverEvent = this.getBest().displayName().hoverEvent();
                if (hoverEvent != null && hoverEvent.asHoverEvent() != null)
                    this.sprite = sprite.hoverEvent(hoverEvent.asHoverEvent());
            }
        } else {
            this.itemEntry = null;
            this.sprite = null;
        }
    }
}