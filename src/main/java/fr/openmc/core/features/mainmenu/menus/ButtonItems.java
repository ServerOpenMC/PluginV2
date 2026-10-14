package fr.openmc.core.features.mainmenu.menus;

import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class ButtonItems {

    public static ItemStack item(Component name, Component... lore) {
        ItemStack item = new ItemStack(Material.PAPER);
        item.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.fromString("air"));
            meta.itemName(name);
            if (lore.length > 0)
                meta.lore(List.of(lore));
        });
        return item;
    }

    public static Component name(String key) {
        return TranslationManager.translation(key).color(NamedTextColor.YELLOW);
    }

    public static Component white(String key) {
        return TranslationManager.translation(key).color(NamedTextColor.WHITE);
    }

    public static Component gray(String key) {
        return TranslationManager.translation(key).color(NamedTextColor.DARK_GRAY);
    }

    public static Component red(String key) {
        return TranslationManager.translation(key).color(NamedTextColor.RED);
    }

}
