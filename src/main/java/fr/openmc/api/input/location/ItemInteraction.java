package fr.openmc.api.input.location;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.chronometer.ChronometerInfo;
import fr.openmc.api.chronometer.ChronometerType;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.MaterialUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;


public class ItemInteraction implements Listener {

    private static final Map<UUID, HashMap<String, Function<Location, Boolean>>> playerCallbacks = new HashMap<>();
    private static final Map<UUID, HashMap<String, Runnable>> playerCallbacksFail = new HashMap<>();
    private static final Map<UUID, HashMap<String, InteractionInfo>> playerChronometerData = new HashMap<>();
    private static final Map<UUID, HashMap<String, ItemStack>> playerOldItemHand = new HashMap<>();

    private static final NamespacedKey NAMESPACE_KEY = new NamespacedKey(OMCPlugin.getInstance(), "interaction_item");

    /*
     * Méthode qui permet de donner un objet à une personne et de quand elle clique avec l'Item, la méthode renverra la positon ou il a cliqué
     */
    public static void runLocationInteraction(Player player, ItemStack item, String chronometerGroup, int chronometerTime, String startMessage, String endMessage, Function<Location, Boolean> result, Runnable onFail) {
        if (!ItemUtils.hasAvailableSlot(player)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de place dans votre inventaire ! L'action a été annulée"), Prefix.OPENMC, MessageType.ERROR, false);
            return;
        }

        if (Chronometer.containsChronometer(player.getUniqueId(), chronometerGroup)) {
            MessagesManager.sendMessage(player, Component.text("Vous avez déjà l'Item !"), Prefix.OPENMC, MessageType.ERROR, false);
            return;
        }

        ItemStack itemInteraction = getItemInteraction(item, chronometerGroup);

        player.closeInventory();
        Chronometer.startChronometer(player, chronometerGroup, chronometerTime, ChronometerType.ACTION_BAR, startMessage, ChronometerType.ACTION_BAR, endMessage);

        ItemStack oldItemHand = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(itemInteraction);

        playerOldItemHand
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(chronometerGroup, oldItemHand);

        playerCallbacks
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(chronometerGroup, result);

        playerCallbacksFail
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(chronometerGroup, onFail);

        playerChronometerData
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(chronometerGroup, new InteractionInfo(itemInteraction, new ChronometerInfo(chronometerGroup, chronometerTime)));
    }

    /*
     * Detecter l'interaction de location
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (isItemInteraction(item)) {
            event.setCancelled(true);

            String interactionId = item.getItemMeta().getPersistentDataContainer().get(NAMESPACE_KEY, PersistentDataType.STRING);
            if (interactionId == null) return;

            Block targetBlock;

            if (event.getClickedBlock() != null) {
                BlockFace face = event.getBlockFace();
                targetBlock = event.getClickedBlock().getRelative(face);
            } else {
                targetBlock = player.getTargetBlockExact(5);
            }

            if (targetBlock != null) {
                HashMap<String, Function<Location, Boolean>> playerCallbacksMap = playerCallbacks.get(player.getUniqueId());
                HashMap<String, InteractionInfo> playerChronometerMap = playerChronometerData.get(player.getUniqueId());

                if (playerCallbacksMap != null && playerChronometerMap != null) {
                    Function<Location, Boolean> callback = playerCallbacksMap.get(interactionId);
                    InteractionInfo interactionInfo = playerChronometerMap.get(interactionId);

                    if (callback != null && interactionInfo != null) {
                        boolean success = callback.apply(targetBlock.getLocation().add(0.5, 0, 0.5));

                        if (success) {
                            playerCallbacksMap.remove(interactionId);
                            playerChronometerMap.remove(interactionId);

                            ChronometerInfo chronoInfo = interactionInfo.chronometerInfo();
                            if (chronoInfo != null) {
                                Chronometer.stopChronometer(player, chronoInfo.chronometerGroup(), null, "%null%");
                            }

                            ItemStack oldItem = playerOldItemHand.getOrDefault(player.getUniqueId(), new HashMap<>()).remove(interactionId);

                            int slotOfItem = ItemUtils.getSlotOfItem(player, item);

                            player.getInventory().remove(item);
                            if (slotOfItem != -1) player.getInventory().setItem(slotOfItem, oldItem);
                        }
                    }
                }
            }
        }
    }

    /*
     * Stopper les intéractions quand le joueur quitte
     */
    @EventHandler
    void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        stopAllInteractions(player);
    }

    /*
     * Stopper les intéractions quand le joueur meurt
     */
    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        stopAllInteractions(player);
    }

    /*
     * Méthode qui permet de verifier si l'item est celui avec qui on interagit
     */
    private static boolean isItemInteraction(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;

        if (item.hasItemMeta()) {
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            return data.has(NAMESPACE_KEY, PersistentDataType.STRING);
        }
        return false;
    }

    @EventHandler
    public void onBundling(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (clickedItem != null && MaterialUtils.isBundle(clickedItem)) {
            if (isItemInteraction(cursorItem)) {
                event.setCancelled(true);
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cet objet dans un bundle"), Prefix.OPENMC, MessageType.ERROR, false);
            }
        } else if (MaterialUtils.isBundle(cursorItem)) {
            if (isItemInteraction(clickedItem)) {
                event.setCancelled(true);
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cet objet dans un bundle"), Prefix.OPENMC, MessageType.ERROR, false);
            }
        }
    }

    /*
     * Empecher de déplacer l'Item d'intéraction
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack protectedItem = null;
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            protectedItem = event.getCurrentItem();
        } else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            protectedItem = event.getCursor();
        }
        if (protectedItem == null)
            return;

        if (!isItemInteraction(protectedItem))
            return;

        if (event.getClickedInventory() != null) {
            InventoryType invType = event.getClickedInventory().getType();
            if (invType != InventoryType.PLAYER &&
                    invType != InventoryType.CREATIVE &&
                    invType != InventoryType.CRAFTING) {
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cet objet ici"), Prefix.OPENMC, MessageType.ERROR, false);
                event.setCancelled(true);
                return;
            }
        }

        if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cet objet ici"), Prefix.OPENMC, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas jeter cet objet"), Prefix.OPENMC, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick()) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cet objet par shift-click"), Prefix.OPENMC, MessageType.ERROR, false);
            event.setCancelled(true);
        }
    }

    /*
     * Empecher de jeter l'Item d'interaction
     */
    @EventHandler
    void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (isItemInteraction(item)) {
            event.setCancelled(true);
            MessagesManager.sendMessage(event.getPlayer(), Component.text("§cVous ne pouvez pas jeter cet item"), Prefix.OPENMC, MessageType.ERROR, false);
        }
    }

    /*
     * Empecher de mettre l'Item dans une ItemFrame
     */
    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isItemInteraction(item))
            return;

        event.setCancelled(true);
        MessagesManager.sendMessage(event.getPlayer(), Component.text("§cVous ne pouvez pas mettre cet item dans un cadre"), Prefix.OPENMC, MessageType.ERROR, false);
    }

    /*
     * Méthode qui permet de donner l'Item spécial pour les intéractions
     */
    private static ItemStack getItemInteraction(ItemStack item, String id) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(item.effectiveName());
            meta.lore(item.lore());
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(NAMESPACE_KEY, PersistentDataType.STRING, id);

            item.setItemMeta(meta);
        }

        return item;
    }

    /*
     * Stopper l'interaction dès que le Chronometre est fini
     */
    @EventHandler
    void onTimeEnd(Chronometer.ChronometerEndEvent e) {
        Player player = (Player) e.getEntity();
        String chronometerGroup = e.getGroup();

        HashMap<String, Runnable> playerCallbacksMap = playerCallbacksFail.get(player.getUniqueId());

        if (playerCallbacksMap != null) {
            Runnable onFail = playerCallbacksMap.get(chronometerGroup);

            if (onFail == null) return;

            onFail.run();
        }

        stopInteraction(player, chronometerGroup);
    }

    /*
     * Méthode qui permet d'arreter une interaction
     */
    public static void stopInteraction(Player player, String chronometerGroup) {
        HashMap<String, Function<Location, Boolean>> playerCallbacksMap = playerCallbacks.get(player.getUniqueId());
        HashMap<String, InteractionInfo> playerChronometerMap = playerChronometerData.get(player.getUniqueId());

        if (playerCallbacksMap != null && playerChronometerMap != null) {
            Function<Location, Boolean> callback = playerCallbacksMap.get(chronometerGroup);
            ItemStack item = playerChronometerMap.get(chronometerGroup).item();
            ChronometerInfo chronoInfo = playerChronometerMap.get(chronometerGroup).chronometerInfo();
            ItemStack oldItem = playerOldItemHand.getOrDefault(player.getUniqueId(), new HashMap<>()).remove(chronometerGroup);

            if (chronoInfo != null) {
                Chronometer.stopChronometer(player, chronoInfo.chronometerGroup(), null, "%null%");
            }

            int slotOfItem = ItemUtils.getSlotOfItem(player, item);

            player.getInventory().remove(item);
            if (slotOfItem != -1) player.getInventory().setItem(slotOfItem, oldItem);

            if (player.getInventory().getItemInOffHand().isSimilar(item)) {
                player.getInventory().setItemInOffHand(null);
            }

            if (player.getItemOnCursor().isSimilar(item)) {
                player.setItemOnCursor(null);
            }

            playerCallbacksMap.remove(chronometerGroup);
            playerChronometerMap.remove(chronometerGroup);

            callback.apply(null);
        }
    }

    /*
     * Méthode qui permet d'arreter toutes les intéractions d'un joueur
     */
    public static void stopAllInteractions(Player player) {
        if (player == null) return;

        HashMap<String, Function<Location, Boolean>> playerCallbacksMap = playerCallbacks.get(player.getUniqueId());
        HashMap<String, InteractionInfo> playerChronometerMap = playerChronometerData.get(player.getUniqueId());

        if (playerCallbacksMap != null) {
            for (String interactionId : playerCallbacksMap.keySet()) {
                stopInteraction(player, interactionId);
            }
        }
        if (playerChronometerMap != null) {
            for (String interactionId : playerChronometerMap.keySet()) {
                stopInteraction(player, interactionId);
            }
        }
    }

}
