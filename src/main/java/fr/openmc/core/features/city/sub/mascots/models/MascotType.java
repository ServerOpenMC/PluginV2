package fr.openmc.core.features.city.sub.mascots.models;

import fr.openmc.core.utils.EnumUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
public enum MascotType {
    PIG(3, 15),
    PANDA(4, 10),
    SHEEP(5, 10),
    AXOLOTL(10, 20),
    CHICKEN(11, 20),
    COW(12, 10),
    GOAT(13, 15),
    MOOSHROOM(14, 10),
    WOLF(15, 15),
    VILLAGER(16, 10),
    SKELETON(21, 10),
    SPIDER(22, 10),
    ZOMBIE(23, 10);

    private final int slot;
    private final Material spawnEgg;
    private final EntityType entityType;
    private final Component displayName;
    private final int price;

    private final Function<Boolean, ItemStack> itemFunction;
    private final Map<Boolean, ItemStack> itemCache = new HashMap<>(2);

    MascotType(int slot, int price) {
        this.slot = slot;
        this.spawnEgg = EnumUtils.match(name() + "_SPAWN_EGG", Material.class, Material.PIG_SPAWN_EGG);
        this.entityType = EnumUtils.match(name(), EntityType.class, EntityType.PIG);
        this.displayName = Component.translatable(entityType.translationKey());
        this.price = price;

        this.itemFunction = this::createMascotItem;
    }

    public ItemStack getMascotItem(boolean selected) {
        return itemCache.computeIfAbsent(selected, itemFunction);
    }

    private ItemStack createMascotItem(boolean selected) {
        ItemStack item = new ItemStack(spawnEgg);
        item.editMeta(meta -> {
            meta.displayName(
                    displayName
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );

            Component lore = Component.text()
                    .append(Component.text("Nécéssite ").color(NamedTextColor.GRAY))
                    .append(Component.text(price, NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" d'Aywenites"))
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build();

            meta.lore(Collections.singletonList(lore));

            if (selected)
                meta.setEnchantmentGlintOverride(true);
        });

        return item;
    }
}
