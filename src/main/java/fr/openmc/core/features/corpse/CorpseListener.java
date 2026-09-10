package fr.openmc.core.features.corpse;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CorpseListener implements Listener {

    private final Sound equipSound = Sound.ITEM_ARMOR_EQUIP_CHAIN;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (CorpseManager.hasCorpseDB(player.getUniqueId())) {

            DBCorpse dbCorpse = CorpseManager.getCorpsesDB().get(player.getUniqueId());

            if (dbCorpse.isKillByPlayer()) return;

            if (!player.hasPermission("fancynpcs.npc.corpse-" + player.getUniqueId() + ".see"))
                player.addAttachment(OMCPlugin.getInstance(), "fancynpcs.npc.corpse-" + player.getUniqueId() + ".see", true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (!CorpseManager.ALLOWED_DIM.contains(event.getPlayer().getWorld().getName())) return;

        Player player = event.getPlayer();

        OMCPlayer omcPlayer = OMCPlayer.of(player);

        boolean killByPlayer = false;

        if (omcPlayer.city().hasCity())
            if (omcPlayer.city().getCity().isInWar()) return;

        EntityDamageEvent.DamageCause cause = null;

        if (player.getLastDamageCause() != null)
            cause = player.getLastDamageCause().getCause();

        if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().getUniqueId().equals(player.getUniqueId()))
            killByPlayer = true;

        if (!CorpseManager.hasCorpseDB(player.getUniqueId())
                &&!CorpseNPCManager.hasNPC(player.getUniqueId())) {
            if (CorpseManager.createCorpse(player, killByPlayer, cause)) {
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

            if (DynamicCooldownManager.isReady(ownerUUID, "corpse")) return;

            if (corpse.isKillByPlayer() && !player.getUniqueId().equals(corpse.getPlayerUUID())) {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(player.getUniqueId());
                OfflinePlayer offlineOwner = CacheOfflinePlayer.getOfflinePlayer(ownerUUID);

                if (offlinePlayer != null)
                    MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.strip",
                                            Component.text(offlineOwner != null ? offlineOwner.getName() : "Unknow Player"))
                                    .color(TextColor.color(Color.YELLOW.asRGB())),
                            Prefix.CORPSE, MessageType.INFO, true);

                if (offlineOwner != null)
                    MessagesManager.sendMessage(offlineOwner, TranslationManager.translation("feature.corpse.messages.warn_strip")
                                    .color(TextColor.color(Color.YELLOW.asRGB())),
                            Prefix.CORPSE, MessageType.WARNING, true);

                corpse.dropLoot();
                CorpseManager.deleteCorpse(ownerUUID, FoundTypes.STRIP);
                return;
            }

            if (!player.getUniqueId().equals(corpse.getPlayerUUID())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.corpse.messages.not_owner"),
                        Prefix.CORPSE, MessageType.WARNING, true);
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
