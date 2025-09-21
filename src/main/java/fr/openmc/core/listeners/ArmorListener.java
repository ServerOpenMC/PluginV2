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

public class ArmorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void onInventoryClick(final InventoryClickEvent event) {
        boolean shift = false,
                numberkey = false;
        if(event.isCancelled()) return;
        if(event.getAction().equals(InventoryAction.NOTHING)) return;
        if(event.getClick().equals(ClickType.SHIFT_LEFT)
                || event.getClick().equals(ClickType.SHIFT_RIGHT)) shift = true;
        if(event.getClick().equals(ClickType.NUMBER_KEY)) numberkey = true;
        if(event.getSlotType() != InventoryType.SlotType.ARMOR
                && event.getSlotType() != InventoryType.SlotType.QUICKBAR && event.getSlotType() != InventoryType.SlotType.CONTAINER) return;
        if(event.getClickedInventory() != null
                && !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if(!event.getInventory().getType().equals(InventoryType.CRAFTING)
                && !event.getInventory().getType().equals(InventoryType.PLAYER)) return;
        if(!(event.getWhoClicked() instanceof Player player)) return;
        ArmorType newArmorType = ArmorType.match(shift ? event.getCurrentItem() : event.getCursor());
        if (!shift && newArmorType != null && event.getRawSlot() != newArmorType.getSlot()) return;

        if(shift) {
            if (newArmorType == null) return;
            boolean equipping = true;
            if(event.getRawSlot() == newArmorType.getSlot()) equipping = false;
            if(newArmorType.equals(ArmorType.HELMET)
                    && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getHelmet()))
                    || newArmorType.equals(ArmorType.CHESTPLATE)
                    && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getChestplate()))
                    || newArmorType.equals(ArmorType.LEGGINGS)
                    && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getLeggings()))
                    || newArmorType.equals(ArmorType.BOOTS)
                    && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getBoots()))) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : event.getCurrentItem(), equipping ? event.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if(armorEquipEvent.isCancelled()){
                        event.setCancelled(true);
                    }
            }
        } else {
            ItemStack newArmorPiece = event.getCursor();
            ItemStack oldArmorPiece = event.getCurrentItem();
            if (numberkey) {
                if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
                ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
                if (!isAirOrNull(hotbarItem)) {
                    newArmorType = ArmorType.match(hotbarItem);
                    newArmorPiece = oldArmorPiece;
                    oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
                }
                else {
                    newArmorType = ArmorType.match(!isAirOrNull(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor());
                }
            } else {
                if(isAirOrNull(event.getCursor()) && !isAirOrNull(event.getCurrentItem())) {
                    newArmorType = ArmorType.match(event.getCurrentItem());
                }
            }

            if (newArmorType == null || event.getRawSlot() != newArmorType.getSlot()) return;
            ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
            if (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, method, newArmorType, oldArmorPiece, newArmorPiece);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if(event.useItemInHand().equals(Event.Result.DENY))return;
        if(event.getAction() == Action.PHYSICAL) return;
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = event.getPlayer();
            if(!event.useInteractedBlock().equals(Event.Result.DENY)){
                if(event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
                    Material mat = event.getClickedBlock().getType();
                }
            }
            ArmorType newArmorType = ArmorType.match(event.getItem());
            if (newArmorType == null) return;
            if(newArmorType.equals(ArmorType.HELMET)
                    && isAirOrNull(player.getInventory().getHelmet())
                    || newArmorType.equals(ArmorType.CHESTPLATE)
                    && isAirOrNull(player.getInventory().getChestplate())
                    || newArmorType.equals(ArmorType.LEGGINGS)
                    && isAirOrNull(player.getInventory().getLeggings())
                    || newArmorType.equals(ArmorType.BOOTS)
                    && isAirOrNull(player.getInventory().getBoots())) {
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.match(event.getItem()), null, event.getItem());
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if(armorEquipEvent.isCancelled()) {
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event) {
        ArmorType type = ArmorType.match(event.getOldCursor());
        if(event.getRawSlots().isEmpty()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if(type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e) {
        ArmorType type = ArmorType.match(e.getBrokenItem());
        if(type != null){
            Player p = e.getPlayer();
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                switch (type) {
                    case HELMET -> p.getInventory().setHelmet(i);
                    case CHESTPLATE -> p.getInventory().setChestplate(i);
                    case LEGGINGS -> p.getInventory().setLeggings(i);
                    case BOOTS -> p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(e.getKeepInventory()) return;
        for(ItemStack i : p.getInventory().getArmorContents()){
            if (isAirOrNull(i)) return;
            Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.match(i), i, null));
        }
    }

    @EventHandler
    public void onDispenseArmorEvent(BlockDispenseArmorEvent event) {
        ArmorType type = ArmorType.match(event.getItem());
        if (type == null) return;
        if(event.getTargetEntity() instanceof Player player) {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.DISPENSER, type, null, event.getItem());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()) event.setCancelled(true);
        }
    }

    /**
     * Check if the item is null or air.
     *
     * @param item  The ItemStack to check.
     * @return true if the item is null or air, false otherwise.
     */
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
