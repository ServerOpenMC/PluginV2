package fr.openmc.core.features.corpse;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CorpseListener implements Listener {

    @EventHandler
    public void onCooldownEndEvent(CooldownEndEvent event) {
        UUID ownerUUID = event.getCooldownUUID();
        String group = event.getGroup();

        if (ownerUUID == null) return;
        if (group == null || !group.equals(CorpseNPCManager.COOLDOWN_GROUP)) return;

        CorpseManager.deleteCorpse(ownerUUID, false);
    }

    @EventHandler
    public void onNPCInteraction(NpcInteractEvent event) {

        Player player = event.getPlayer();

        if (DynamicCooldownManager.isReady(player.getUniqueId(), "corpse")) return;

        if (event.getNpc().getData().getName().startsWith("corpse-")) {
            UUID ownerUUID = UUID.fromString(event.getNpc().getData().getName().replace("corpse-", ""));

            if (!CorpseManager.hasCorpseDB(ownerUUID)) {
                System.out.println("pas dans la db");
                return;
            } // TODO erreur chelou

            DBCorpse corpse = CorpseManager.getCorpsesDB().get(ownerUUID);

            if (!corpse.getPlayerUUID().equals(ownerUUID)) {
                System.out.println("wtf ???");
                return;
            }

            if (corpse.isKillByPlayer()) {
                // Drop Stuff
                return;
            }

            if (!player.getUniqueId().equals(corpse.getPlayerUUID())) {
                System.out.println("pas ton cadavre");
                System.out.println(player.getUniqueId());
                System.out.println(corpse.getPlayerUUID());
                return;
            }

            ItemStack[] inventory = corpse.getInventoryContent().clone();
            float exp = corpse.getExp();
            int level = corpse.getLevel();

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

            player.setExp(exp);
            player.setLevel(level);

            CorpseManager.deleteCorpse(ownerUUID, true);
        }
    }

}
