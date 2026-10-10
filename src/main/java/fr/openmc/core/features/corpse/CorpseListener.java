package fr.openmc.core.features.corpse;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CorpseListener implements Listener {

    private final DynamicCooldownManager dynamicCooldownManager = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();
    private final CorpseManager corpseManager;
    private final CorpseNPCManager corpseNPCManager;

    public CorpseListener(CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
        this.corpseNPCManager = corpseManager.corpseNPCManager;
    }

    private final Sound equipSound = Sound.ITEM_ARMOR_EQUIP_CHAIN;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        OMCPlayer player = OMCPlayer.of(event.getPlayer());

        if (player.corpse().hasCorpseDB()) {
            DBCorpse dbCorpse = corpseManager.getCorpsesDB().get(player.getUniqueId());

            if (dbCorpse.isKillByPlayer()) return;

            if (!player.hasPermission("fancynpcs.npc.corpse-" + player.getUniqueId() + ".see"))
                player.addAttachment(OMCPlugin.getInstance(), "fancynpcs.npc.corpse-" + player.getUniqueId() + ".see", true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!corpseManager.ALLOWED_DIM.contains(event.getPlayer().getWorld().getName())) return;

        OMCPlayer player = OMCPlayer.of(event.getPlayer());

        boolean killByPlayer = false;

        if (player.city().hasCity())
            if (player.city().getCity().isInWar()) return;

        EntityDamageEvent.DamageCause cause = null;

        if (player.getLastDamageCause() != null)
            cause = player.getLastDamageCause().getCause();

        if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().getUniqueId().equals(player.getUniqueId()))
            killByPlayer = true;

        if (!player.corpse().hasCorpseDB()
                && !corpseNPCManager.hasNPC(player.getUniqueId())) {
            if (player.corpse().createCorpse(killByPlayer, cause)) {
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
        if (group == null || !group.equals(corpseNPCManager.COOLDOWN_GROUP)) return;

        corpseManager.deleteCorpse(ownerUUID, FoundTypes.NOT_FOUND);
    }

    @EventHandler
    public void onNPCInteraction(NpcInteractEvent event) {
        OMCPlayer player = OMCPlayer.of(event.getPlayer());

        if (event.getNpc().getData().getName().startsWith("corpse-")) {
            UUID ownerUUID = UUID.fromString(event.getNpc().getData().getName().replace("corpse-", ""));

            if (!corpseManager.hasCorpseDB(ownerUUID)) {
                OMCPlugin.getInstance().getLogger().warning("Corpse found with no DB");
                return;
            }

            DBCorpse corpse = corpseManager.getCorpsesDB().get(ownerUUID);

            if (!corpse.getPlayerUUID().equals(ownerUUID)) {
                OMCPlugin.getInstance().getLogger().warning("The ownerUUID did not match with the corpse's playerUUID");
                return;
            }

            if (dynamicCooldownManager.isReady(ownerUUID, "corpse")) return;

            if (corpse.isKillByPlayer() && !player.getUniqueId().equals(corpse.getPlayerUUID())) {
                OMCOfflinePlayer offlinePlayer = OMCOfflinePlayer.of(player.getUniqueId());
                OMCOfflinePlayer offlineOwner = OMCOfflinePlayer.of(ownerUUID);

                offlinePlayer.message().sendInfo(TranslationManager.translation("feature.corpse.messages.strip",
                                            Component.text(offlineOwner != null ? offlineOwner.getName() : "Unknow Player"))
                                    .color(TextColor.color(Color.YELLOW.asRGB())),
                            Prefix.CORPSE, true);

                offlinePlayer.message().sendWarning(TranslationManager.translation("feature.corpse.messages.warn_strip")
                                .color(TextColor.color(Color.YELLOW.asRGB())),
                            Prefix.CORPSE, true);

                corpse.dropLoot();
                corpseManager.deleteCorpse(ownerUUID, FoundTypes.STRIP);
                return;
            }

            if (!player.getUniqueId().equals(corpse.getPlayerUUID())) {
                player.message().sendWarning(TranslationManager.translation("feature.corpse.messages.not_owner"),
                        Prefix.CORPSE, false);
                return;
            }

            ItemStack[] inventory = corpse.getInventoryContent().clone();

            int exp = corpse.getExp();

            if (player.getInventory().isEmpty()) {
                player.getInventory().setContents(inventory);
            } else {
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
                    corpseManager.sendMailItems(player, player, rm);
                }
            }

            player.setExperienceLevelAndProgress(exp);

            player.playSound(player.getLocation(), equipSound, 1f, 0);

            corpseManager.deleteCorpse(ownerUUID, FoundTypes.FOUND);
        }
    }

}
