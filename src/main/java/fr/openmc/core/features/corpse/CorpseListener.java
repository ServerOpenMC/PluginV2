package fr.openmc.core.features.corpse;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.damage.DeathMessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CorpseListener implements Listener {

    private final Sound equipSound = Sound.ITEM_ARMOR_EQUIP_CHAIN;

    public static final Map<UUID, Location> lastSafeLocation = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        Block blockUnder = loc.clone().subtract(0, 1, 0).getBlock();

        // On enregistre seulement si le joueur est sur un bloc solide
        if (blockUnder.getType().isSolid()) {
            lastSafeLocation.put(player.getUniqueId(), loc.clone());
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!(boolean) PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.CORPSE)) return;

        EntityDamageEvent.DamageCause cause = null;

        if (player.getLastDamageCause() != null)
            cause = player.getLastDamageCause().getCause();

        if (!CorpseManager.hasCorpseDB(player.getUniqueId())
                &&!CorpseNPCManager.hasNPC(player.getUniqueId())) {
            if (CorpseManager.createCorpse(player, (event.getEntity().getKiller() != null), cause)) {
                event.setDroppedExp(0);
                event.getDrops().clear();
            }
        }
    }

    @EventHandler
    public void onCooldownEndEvent(CooldownEndEvent event) {
        UUID ownerUUID = event.getCooldownUUID();
        String group = event.getGroup();

        if (ownerUUID == null) return;
        if (group == null || !group.equals(CorpseNPCManager.COOLDOWN_GROUP)) return;

        CorpseManager.deleteCorpse(ownerUUID, FoundTypes.NOT_FOUND);
    }

    @EventHandler
    public void onNPCInteraction(NpcInteractEvent event) {

        Player player = event.getPlayer();

        if (DynamicCooldownManager.isReady(player.getUniqueId(), "corpse")) return;

        if (event.getNpc().getData().getName().startsWith("corpse-")) {
            UUID ownerUUID = UUID.fromString(event.getNpc().getData().getName().replace("corpse-", ""));

            if (!CorpseManager.hasCorpseDB(ownerUUID)) {
                OMCPlugin.getInstance().getLogger().warning("Corpse found with no DB");
                return;
            }

            DBCorpse corpse = CorpseManager.getCorpsesDB().get(ownerUUID);

            if (!corpse.getPlayerUUID().equals(ownerUUID)) {
                OMCPlugin.getInstance().getLogger().warning("The ownerUUID did not match with the corpse's playerUUID");
                return;
            }

            if (corpse.isKillByPlayer()) {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(player.getUniqueId());
                OfflinePlayer offlineOwner = CacheOfflinePlayer.getOfflinePlayer(ownerUUID);

                if (offlinePlayer != null)
                    MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.strip",
                                    Component.text(offlineOwner != null ? offlineOwner.getName() : "Unknow Player"))
                                    .color(TextColor.color(Color.YELLOW.asRGB())),
                            Prefix.OPENMC, MessageType.INFO, true);

                if (offlineOwner != null)
                    MessagesManager.sendMessage(offlineOwner, TranslationManager.translation("feature.corpse.messages.warn_strip")
                                    .color(TextColor.color(Color.YELLOW.asRGB())),
                            Prefix.OPENMC, MessageType.WARNING, true);

                corpse.dropLoot();
                CorpseManager.deleteCorpse(ownerUUID, FoundTypes.STRIP);
                return;
            }

            if (!player.getUniqueId().equals(corpse.getPlayerUUID())) {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(ownerUUID);
                MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.not_owner"),
                        Prefix.OPENMC, MessageType.WARNING, true);
                return;
            }

            ItemStack[] inventory = corpse.getInventoryContent().clone();

            int exp = corpse.getExp();

            if (player.getInventory().isEmpty()) {
                player.getInventory().setContents(inventory);
            }
            else {
                List<ItemStack> remaining = new ArrayList<>();

                for (ItemStack item :  inventory) {
                    if (!ItemUtils.hasEnoughSpace(player, item, item.getAmount())) {
                        remaining.add(item);
                    } else {
                        player.getInventory().addItem(item);
                    }
                }

                if (!remaining.isEmpty()) {
                    ItemStack[] rm = remaining.toArray(ItemStack[]::new);
                    CorpseManager.sendMailItems(player, player, rm);
                }
            }

            player.setExperienceLevelAndProgress(exp);

            player.playSound(player.getLocation(), equipSound, 1f, 0);

            CorpseManager.deleteCorpse(ownerUUID, FoundTypes.FOUND);
        }
    }

}
