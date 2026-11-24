package fr.openmc.core.features.events.contents.christmas.contents.items;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.UsableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiftItem extends CustomItem implements UsableItem {
    public GiftItem(String id) {
        super(id);
    }

    @Override
    public @NotNull ItemStack getVanilla() {
        return ItemStack.of(Material.PAPER);
    }

    @Override
    public void onRightClick(Player player, PlayerInteractEvent event) {
        // todo: faire logique de clic
    }
}
