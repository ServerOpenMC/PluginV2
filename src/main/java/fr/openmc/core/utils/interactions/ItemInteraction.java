package fr.openmc.core.utils.interactions;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.PacketType;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerInfo;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

import static fr.openmc.core.utils.ItemUtils.isBundle;

public class ItemInteraction implements Listener {

    private static final Map<UUID, HashMap<String, Consumer<Location>>> playerCallbacks = new HashMap<>();
    private static final Map<UUID, HashMap<String, InteractionInfo>> playerChronometerData = new HashMap<>();

    private static final NamespacedKey NAMESPACE_KEY = new NamespacedKey(OMCPlugin.getInstance(), "interaction_item");

    /*
     * Méthode qui permet de donner un objet à une personne et de quand elle clique avec l'Item, la méthode renverra la positon ou il a cliqué
     */
    public static void runLocationInteraction(Player player, ItemStack item, String chronometerGroup, int chronometerTime, String startMessage, String endMessage, Consumer<Location> result) {
        if (!ItemUtils.hasAvailableSlot(player)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de place dans votre inventaire ! L'action a été annulée"), Prefix.OPENMC, MessageType.ERROR, false);
            return;
        }

        ItemStack itemInteraction = getItemInteraction(item, chronometerGroup);

        player.closeInventory();
        Chronometer.startChronometer(player, chronometerGroup, chronometerTime, ChronometerType.ACTION_BAR, startMessage, ChronometerType.ACTION_BAR, endMessage);
        player.getInventory().addItem(itemInteraction);

        playerCallbacks
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(chronometerGroup, result);

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

            Block targetBlock = null;

            if (event.getClickedBlock() != null) {
                BlockFace face = event.getBlockFace();
                targetBlock = event.getClickedBlock().getRelative(face);
            } else {
                targetBlock = player.getTargetBlockExact(5);
            }

            if (targetBlock != null) {
                HashMap<String, Consumer<Location>> playerCallbacksMap = playerCallbacks.get(player.getUniqueId());
                HashMap<String, InteractionInfo> playerChronometerMap = playerChronometerData.get(player.getUniqueId());

                if (playerCallbacksMap != null && playerChronometerMap != null) {
                    Consumer<Location> callback = playerCallbacksMap.remove(interactionId);
                    ChronometerInfo chronoInfo = playerChronometerMap.remove(interactionId).getChronometerInfo();

                    if (callback != null) callback.accept(targetBlock.getLocation().add(0.5, 0, 0.5));
                    if (chronoInfo != null) Chronometer.stopChronometer(player, chronoInfo.getChronometerGroup(), null, "%null%");

                    player.getInventory().remove(item);
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
     * Stopper l'interaction des que le Chronometre est fini
     */
    @EventHandler
    void onTimeEnd(Chronometer.ChronometerEndEvent e) {
        Player player = (Player) e.getEntity();
        String chronometerGroup = e.getGroup();

        stopInteraction(player, chronometerGroup);
    }

    @EventHandler
    public void onBundling(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (clickedItem != null && isBundle(clickedItem)) {
            if (isItemInteraction(cursorItem)) {
                event.setCancelled(true);
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cet objet dans un bundle"), Prefix.OPENMC, MessageType.ERROR, false);
            }
        } else if (cursorItem != null && isBundle(cursorItem)) {
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
            return;
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
        ItemStack itemInteraction = new ItemStack(item.getType());
        ItemMeta meta = itemInteraction.getItemMeta();
        if (meta != null) {
            meta.displayName(item.effectiveName());
            meta.lore(item.lore());
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(NAMESPACE_KEY, PersistentDataType.STRING, id);

            itemInteraction.setItemMeta(meta);
        }

        return itemInteraction;
    }

    /*
     * Méthode qui permet de verifier si l'item est celui avec qui on intéragit
     */
    private static boolean isItemInteraction(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;

        if (item != null && item.hasItemMeta()) {
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            return data.has(NAMESPACE_KEY, PersistentDataType.STRING);
        }
        return false;
    }

    /*
     * Méthode qui permet d'arreter une interaction
     */
    public static void stopInteraction(Player player, String chronometerGroup) {
        HashMap<String, Consumer<Location>> playerCallbacksMap = playerCallbacks.get(player.getUniqueId());
        HashMap<String, InteractionInfo> playerChronometerMap = playerChronometerData.get(player.getUniqueId());

        if (playerCallbacksMap != null && playerChronometerMap != null) {
            Consumer<Location> callback = playerCallbacksMap.get(chronometerGroup);
            ItemStack item = playerChronometerMap.get(chronometerGroup).getItem();
            ChronometerInfo chronoInfo = playerChronometerMap.get(chronometerGroup).getChronometerInfo();

            if (chronoInfo != null) {
                Chronometer.stopChronometer(player, chronoInfo.getChronometerGroup(), null, "%null%");
            }

            player.getInventory().remove(item);

            if (player.getInventory().getItemInOffHand().isSimilar(item)) {
                player.getInventory().setItemInOffHand(null);
            }

            if (player.getItemOnCursor().isSimilar(item)) {
                player.setItemOnCursor(null);
            }

            playerCallbacksMap.remove(chronometerGroup);
            playerChronometerMap.remove(chronometerGroup);

            callback.accept(null);
        }
    }

    /*
     * Méthode qui permet toutes les intéractions d'un joueur
     */
    public static void stopAllInteractions(Player player) {
        if (player == null) return;

        HashMap<String, Consumer<Location>> playerCallbacksMap = playerCallbacks.get(player.getUniqueId());
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

    public static void startDebugTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("Débogage des playerCallbacks:");
                for (UUID playerId : playerCallbacks.keySet()) {
                    HashMap<String, Consumer<Location>> callbacks = playerCallbacks.get(playerId);
                    Bukkit.getLogger().info("Joueur UUID: " + playerId);
                    for (String key : callbacks.keySet()) {
                        Bukkit.getLogger().info("  Interaction ID: " + key);
                    }
                }

                Bukkit.getLogger().info("Débogage des playerChronometerData:");
                for (UUID playerId : playerChronometerData.keySet()) {
                    HashMap<String, InteractionInfo> chronometerData = playerChronometerData.get(playerId);
                    Bukkit.getLogger().info("Joueur UUID: " + playerId);
                    for (String key : chronometerData.keySet()) {
                        InteractionInfo info = chronometerData.get(key);
                        Bukkit.getLogger().info("  Interaction ID: " + key + " | Chrono: " + info.getChronometerInfo().getChronometerGroup());
                    }
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 300L);
    }
}
