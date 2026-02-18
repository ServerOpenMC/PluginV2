package fr.openmc.core.features.dream.mecanism.singularity;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.TakeFromSingularityEvent;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SingularityMenu extends PaginatedMenu {
    public SingularityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return List.of();
    }

    @Override
    public List<ItemStack> getItems() {
        SingularityContents singuContents = SingularityManager.getSingularityContents(getOwner());

        if (singuContents == null) {
            return Collections.emptyList();
        }

        ItemStack[] contents = singuContents.getContent();

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                contents[i] = new ItemStack(Material.AIR);
            }
        }

        return Arrays.asList(contents);
    }

    private static final List<Integer> SINGULARITY_ITEM_SLOTS =
            IntStream.rangeClosed(0, 26)
                    .boxed()
                    .toList();

    @Override
    public List<Integer> getTakableSlot() {
        return Stream.concat(
                SINGULARITY_ITEM_SLOTS.stream(),
                IntStream.rangeClosed(27, 62)
                        .boxed()
        ).toList();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        return Collections.emptyMap();
    }

    @Override
    public @NotNull String getName() {
        return "Votre Singularité";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        boolean inDream = DreamUtils.isInDreamWorld(player);

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        int raw = event.getRawSlot();
        int menuSize = event.getInventory().getSize();

        boolean clickInMenu = raw < menuSize;
        boolean clickInPlayerInv = raw >= menuSize;

        DreamItem cursorDream = DreamItemRegistry.getByItemStack(cursor);
        DreamItem currentDream = DreamItemRegistry.getByItemStack(current);

        if ((cursorDream != null && cursorDream.getName().equals("omc_dream:singularity"))
                || (currentDream != null && currentDream.getName().equals("omc_dream:singularity"))) {
            event.setCancelled(true);
            return;
        }

        // prendre
        if (current != null && current.getType() != Material.AIR) {
            if (currentDream == null) return;

            if (inDream) return;

            if (clickInPlayerInv) return;

            ItemStack replacement = currentDream.getTransferableItem();

            if (replacement == null) {
                event.setCancelled(true);
                return;
            }

            replacement.setAmount(current.getAmount());
            event.setCurrentItem(replacement);
	        Bukkit.getServer().getPluginManager().callEvent(new TakeFromSingularityEvent(player, replacement));
            return;
        }

        // déposer
        if (cursor != null && cursor.getType() != Material.AIR) {
            if (cursorDream == null) {
                event.setCancelled(true);
                return;
            }

            if (!inDream) {
                if (clickInMenu) {
                    event.setCancelled(true);
                    return;
                }
                if (clickInPlayerInv) return; // Laisser
            } else if (inDream) {
                if (!cursorDream.isTransferable()) event.setCancelled(true);
            }
        }
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (Restart.isRestarting) return;
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) return;

        Inventory inv = event.getInventory();
        exit(inv);
    }

    private void exit(Inventory inv) {
        Player player = getOwner();
        ItemStack[] inventoryContents = inv.getContents();

        ItemStack[] validContents = new ItemStack[inventoryContents.length];
        List<ItemStack> toReturnToPlayer = new ArrayList<>();

        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (item == null || item.getType() == Material.AIR) continue;

            DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);

            if (dreamItem == null || !dreamItem.isTransferable()) {
                toReturnToPlayer.add(item);
            } else {
                validContents[i] = item;
            }
        }

        for (ItemStack item : toReturnToPlayer) {
            player.getInventory().addItem(item);
        }

        SingularityContents contents = SingularityManager.getSingularityContents(player);

        if (contents == null) {
            SingularityManager.addSingularityContents(player, validContents);
        } else {
            contents.setContent(validContents);
        }
    }
}
