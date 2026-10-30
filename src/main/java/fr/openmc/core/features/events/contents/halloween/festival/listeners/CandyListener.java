package fr.openmc.core.features.events.contents.halloween.festival.listeners;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.features.events.contents.halloween.festival.CandyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CandyListener implements Listener {

    private final Map<UUID, Integer> sugarIndex = new HashMap<>();

    @EventHandler
    public void onCandyEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        CustomStack stack = CustomStack.byItemStack(item);

        if (stack == null) return;

        if (!CandyItem.candies.containsKey(stack.getId())) return;

        CandyItem candy = CandyItem.candies.get(stack.getId());

        UUID uuid = player.getUniqueId();

        int sugarIdx = sugarIndex.computeIfAbsent(uuid, key -> 0);

        sugarIdx += candy.getDiabetesIndex();

        if (sugarIdx >= 20) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 20, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1));
            sugarIndex.put(uuid, 0);
        } else
            sugarIndex.put(uuid, sugarIdx);
    }

}
