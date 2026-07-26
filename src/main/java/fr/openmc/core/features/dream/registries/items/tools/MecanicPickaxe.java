package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MecanicPickaxe extends DreamItem {
    public MecanicPickaxe() {
        super(new DreamItemMeta(
                "omc_dream:mecanic_pickaxe",
                TranslationManager.translation("feature.dream.item.mecanic_pickaxe.name"),
                DreamRarity.LEGENDARY,
                Material.NETHERITE_PICKAXE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
