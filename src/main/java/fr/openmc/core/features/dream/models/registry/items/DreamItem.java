package fr.openmc.core.features.dream.models.registry.items;

import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DreamItem extends CustomItem {
    @Getter
    private DreamItemMeta meta;
    /**
     * Crée un DreamItem à partir d'un meta qui contiens comme clé id équivalent au namespace:item
     * @param dreamItemMeta La méta de la classe (obligatoire afin de lire certaines infos a propos de l'item depuis le bootstrap)
     */
    protected DreamItem(DreamItemMeta dreamItemMeta) {
        super(dreamItemMeta);
        meta = dreamItemMeta;
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(getMeta().getDefaultMaterial());
        item.getItemMeta().itemName(Component.text(getMeta().getName()));
        return item;
    }

    public DreamRarity getRarity() {
        return getMeta().getRarity();
    }

    public boolean isTransferable() {
        return getMeta().getTransferable();
    };

    public abstract ItemStack getTransferableItem();

    private List<Component> getGeneratedLore() {
        ItemStack baseItem;

        if (!ItemsAdderHook.isEnable() || getItemsAdder() == null) {
            baseItem = getVanilla();
        } else {
            baseItem = getItemsAdder();
        }

        List<Component> lore = baseItem.lore();
        if (lore == null) lore = new ArrayList<>();

        if (this instanceof DreamEquipableItem equipableItem) {
            lore.add(Component.empty());

            lore.add(Component.text("§7§oTemps additionnel: §r§a+" + equipableItem.getAdditionalMaxTime() + "s"));

            Integer coldResistance = equipableItem.getColdResistance();
            if (coldResistance != null) {
                lore.add(Component.text("§7§oResistance au froid: §r§b+" + coldResistance));
            }
        }

        lore.add(Component.empty());

        if (isTransferable()) {
            lore.add(Component.text("§9§ko §r§9Dream Transferable §9§ko"));
        }

        lore.add(this.getRarity().getTemplateLore());

        return lore;
    }

    private List<Component> getGeneratedLoreTransferable() {
        ItemStack baseItem;

        if (!ItemsAdderHook.isEnable() || getItemsAdder() == null) {
            baseItem = getVanilla();
        } else {
            baseItem = getItemsAdder();
        }

        List<Component> lore = baseItem.lore();
        if (lore == null) lore = new ArrayList<>();

        lore.add(Component.empty());

        if (isTransferable()) {
            lore.add(Component.text("§9§ko §r§9Dream Transferable §9§ko"));
        }

        lore.add(this.getRarity().getTemplateLore());

        return lore;
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     *
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBestTransferable() {
        ItemStack item = super.getBest();
        item.lore(this.getGeneratedLoreTransferable());

        return item;
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     *
     * @return Best ItemStack to use for the server
     */
    @Override
    public ItemStack getBest() {
        ItemStack item = super.getBest();
        item.lore(this.getGeneratedLore());

        return item;
    }
}
