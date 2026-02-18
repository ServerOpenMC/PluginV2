package fr.openmc.core.features.dream.registries.items.orb;

import fr.openmc.core.features.dream.mecanism.singularity.SingularityMenu;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.features.dream.models.registry.items.DreamUsableItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class Singularity extends DreamUsableItem {
    public Singularity(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.ONIRISIME;
    }

    @Override
    public boolean isTransferable() {
        return false;
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);

        item.getItemMeta().itemName(Component.text("Singularité"));
        return item;
    }

    @Override
    public void onRightClick(Player player, PlayerInteractEvent event) {
        new SingularityMenu(player).open();
    }
}
