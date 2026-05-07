package fr.openmc.core.features.dream.registries.items.orb;

import fr.openmc.core.features.dream.mecanism.singularity.SingularityMenu;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.registry.items.options.UsableItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class Singularity extends DreamItem implements UsableItem {
    public Singularity() {
        super(new DreamItemMeta(
                "omc_dream:singularity",
                "Singularité",
                DreamRarity.ONIRISIME,
                Material.HEART_OF_THE_SEA,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public void onRightClick(Player player, PlayerInteractEvent event) {
        new SingularityMenu(player).open();
    }
}
