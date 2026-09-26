package fr.openmc.core.features.events.contents.halloween.halloween.listeners;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.halloween.CandyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CandyListener implements Listener {

    private final Map<UUID, Integer> diabetes = new HashMap<>();

    @EventHandler
    public void onCandyEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        CustomStack stack = CustomStack.byItemStack(item);

        if (stack == null) return;

        if (!CandyItem.candies.containsKey(stack.getId())) return;

        CandyItem candy = CandyItem.candies.get(stack.getId());

        UUID uuid = player.getUniqueId();

        int diabete = diabetes.computeIfAbsent(uuid, key -> 0);

        diabete += candy.getDiabetesIndex();

        if (diabete >= 20) {
            //TODO faire un truc
            diabetes.put(uuid, 0);
        } else
            diabetes.put(uuid, diabete);
    }

}
