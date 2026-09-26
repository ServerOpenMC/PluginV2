package fr.openmc.core.features.events.contents.halloween.halloween;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemMeta;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class CandyItem extends CustomItem {

    @Getter
    public int diabetesIndex;
    public Material baseItem;

    public static final Map<String, CandyItem> candies = new HashMap<>();

    public CandyItem(String candyID , int diabetesIndex, Material baseItem) {
        super(new CustomItemMeta(candyID));
        this.diabetesIndex = diabetesIndex;
        this.baseItem = baseItem;
        candies.put(candyID, this);
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(baseItem);
        item.editMeta(meta -> meta.itemName(
                TranslationManager.translation("feature." + super.getId().replace(":", ".") + ".name")
        ));
        return item;
    }
}
