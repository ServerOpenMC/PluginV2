package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.chests;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.chests.menu.LootChestMenu;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

public class LootChest {
    private final Map<UUID, Integer> playerUsage = new HashMap<>();

    private final int timeToReload;
    @Getter public final BlockVector3 pos;

    private final CustomLootTable lootTable = OMCRegistry.CUSTOM_LOOT_TABLES.LOOTCHEST;

    public LootChest(int timeToReload, BlockVector3 pos) {
        this.timeToReload = timeToReload;
        this.pos = pos;
    }

    public void use(Player player) {
        if (!canUse(player.getUniqueId())) {
            MessagesManager.sendMessage(
                    player,
                    Component.text(DateUtils.convertSecondToTime(playerUsage.get(player.getUniqueId()))).color(NamedTextColor.RED),
                    Prefix.HALLOWEEN,
                    MessageType.INFO,
                    true
            );
            return;
        }

        playerUsage.put(player.getUniqueId(), timeToReload);

        LootReward loot = lootTable.rollLoots();

        if (loot == null)
            return;

        new LootChestMenu(player, loot).open();
    }

    public boolean canUse(UUID uuid) {
        return !playerUsage.containsKey(uuid);
    }

    public void update(int time) {
        playerUsage.entrySet().removeIf(entry -> {
            int newTime = entry.getValue() - time;

            if (newTime <= 0) {
                return true;
            }

            entry.setValue(newTime);
            return false;
        });
    }
}
