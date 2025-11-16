package fr.openmc.core.listeners;

import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.utils.ArmorType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ArmorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void onInventoryClick(final InventoryClickEvent event) {

        if (event.getAction() == InventoryAction.NOTHING) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        boolean shift = event.getClick() == ClickType.SHIFT_LEFT
                || event.getClick() == ClickType.SHIFT_RIGHT;
        boolean numberKey = event.getClick() == ClickType.NUMBER_KEY;

        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() != InventoryType.PLAYER)
            return;

        if (event.getSlotType() != InventoryType.SlotType.ARMOR
                && event.getSlotType() != InventoryType.SlotType.QUICKBAR
                && event.getSlotType() != InventoryType.SlotType.CONTAINER)
            return;

        if (!event.getInventory().getType().equals(InventoryType.CRAFTING)
                && !event.getInventory().getType().equals(InventoryType.PLAYER)) return;

        ArmorType newArmorType = ArmorType.match(shift ? event.getCurrentItem() : event.getCursor());

        if (!shift && newArmorType != null && event.getRawSlot() != newArmorType.getSlot()) return;

        if (shift) {
            if (newArmorType == null) return;

            boolean equipping = event.getRawSlot() != newArmorType.getSlot();
            ItemStack current = switch (newArmorType) {
                case HELMET -> player.getInventory().getHelmet();
                case CHESTPLATE -> player.getInventory().getChestplate();
                case LEGGINGS -> player.getInventory().getLeggings();
                case BOOTS -> player.getInventory().getBoots();
            };

            if (equipping == isAirOrNull(current)) {
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                        player,
                        ArmorEquipEvent.EquipMethod.SHIFT_CLICK,
                        newArmorType,
                        equipping ? null : event.getCurrentItem(),
                        equipping ? event.getCurrentItem() : null
                );

                Bukkit.getPluginManager().callEvent(armorEquipEvent);

                if (armorEquipEvent.isCancelled()) event.setCancelled(true);
            }
            return;
        }

        ItemStack newArmorPiece = event.getCursor();
        ItemStack oldArmorPiece = event.getCurrentItem();

        if (numberKey) {
            ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());

            if (!isAirOrNull(hotbarItem)) {
                newArmorType = ArmorType.match(hotbarItem);
                newArmorPiece = oldArmorPiece;
                oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
            } else {
                newArmorType = ArmorType.match(!isAirOrNull(oldArmorPiece) ? oldArmorPiece : newArmorPiece);
            }
        } else if (isAirOrNull(newArmorPiece) && !isAirOrNull(oldArmorPiece)) {
            newArmorType = ArmorType.match(oldArmorPiece);
        }

        if (newArmorType == null || event.getRawSlot() != newArmorType.getSlot()) return;

        ArmorEquipEvent.EquipMethod method =
                (event.getAction() == InventoryAction.HOTBAR_SWAP || numberKey)
                        ? ArmorEquipEvent.EquipMethod.HOTBAR_SWAP
                        : ArmorEquipEvent.EquipMethod.PICK_DROP;

        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                player, method, newArmorType, oldArmorPiece, newArmorPiece
        );

        Bukkit.getPluginManager().callEvent(armorEquipEvent);

        if (armorEquipEvent.isCancelled()) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.useItemInHand().equals(Event.Result.DENY)) return;
        if (event.getAction().equals(Action.PHYSICAL)) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            ArmorType newArmorType = ArmorType.match(item);
            if (newArmorType == null) return;

            ItemStack oldArmor = switch (newArmorType) {
                case HELMET -> player.getInventory().getHelmet();
                case CHESTPLATE -> player.getInventory().getChestplate();
                case LEGGINGS -> player.getInventory().getLeggings();
                case BOOTS -> player.getInventory().getBoots();
            };

            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR, newArmorType, oldArmor, item);

            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);

            if (armorEquipEvent.isCancelled()) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlots().isEmpty()) return;

        ArmorType type = ArmorType.match(event.getOldCursor());
        if (type == null) return;

        int slot = event.getRawSlots().stream().findFirst().orElse(-1);
        if (type.getSlot() != slot) return;

        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                player,
                ArmorEquipEvent.EquipMethod.DRAG,
                type,
                null,
                event.getOldCursor()
        );

        Bukkit.getPluginManager().callEvent(armorEquipEvent);

        if (armorEquipEvent.isCancelled()) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent event) {
        ArmorType type = ArmorType.match(event.getBrokenItem());
        if (type == null) return;

        Player player = event.getPlayer();

        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                player,
                ArmorEquipEvent.EquipMethod.BROKE,
                type,
                event.getBrokenItem(),
                null
        );

        Bukkit.getPluginManager().callEvent(armorEquipEvent);

        if (armorEquipEvent.isCancelled()) {

            ItemStack broken = event.getBrokenItem().clone();
            broken.setAmount(1);

            ItemMeta meta = broken.getItemMeta();
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(broken.getType().getMaxDurability() + 1);
                broken.setItemMeta(meta);
            }

            switch (type) {
                case HELMET -> player.getInventory().setHelmet(broken);
                case CHESTPLATE -> player.getInventory().setChestplate(broken);
                case LEGGINGS -> player.getInventory().setLeggings(broken);
                case BOOTS -> player.getInventory().setBoots(broken);
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;

        Player player = event.getEntity();

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (isAirOrNull(item)) continue;

            Bukkit.getPluginManager().callEvent(
                    new ArmorEquipEvent(
                            player,
                            ArmorEquipEvent.EquipMethod.DEATH,
                            ArmorType.match(item),
                            item,
                            null
                    )
            );
        }
    }

    @EventHandler
    public void onDispenseArmorEvent(BlockDispenseArmorEvent event) {
        ArmorType type = ArmorType.match(event.getItem());
        if (type == null) return;
        if (!(event.getTargetEntity() instanceof Player player)) return;

        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                player,
                ArmorEquipEvent.EquipMethod.DISPENSER,
                type,
                null,
                event.getItem()
        );

        Bukkit.getPluginManager().callEvent(armorEquipEvent);

        if (armorEquipEvent.isCancelled()) event.setCancelled(true);
    }

    /**
     * Check if the item is null or air.
     *
     * @param item The ItemStack to check.
     * @return true if the item is null or air, false otherwise.
     */
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
