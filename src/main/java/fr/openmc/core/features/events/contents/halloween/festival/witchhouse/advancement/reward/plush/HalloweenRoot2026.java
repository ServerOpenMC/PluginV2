package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward.plush;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward.PointReward;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class HalloweenRoot2026 implements PointReward {

    CustomItem reward = OMCRegistry.CUSTOM_ITEMS.PELUCHE_ROOT_HALLOWEEN_2026;

    @Override
    public void giveReward() {

        //Get all participant and give them the HalloweenRootPlush2026

        ItemStack[] rewardToGive = new ItemStack[]{reward.getBest()};

        for (UUID uuid : HalloweenManager.getParticipant()) {

            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {

                OMCPlayer omcPlayer = OMCPlayer.of(player);

                HalloweenManager.sendMailItems(omcPlayer, omcPlayer, rewardToGive);
            } else {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);

                if (offlinePlayer == null) {
                    //TODO problème
                    continue;
                }

                MailboxManager.sendItemsToOfflinePlayer(offlinePlayer, rewardToGive);
            }
        }

    }

    @Override
    public ItemStack getItemMenuPreview() {
        return reward.getBest();
    }

    @Override
    public Component getRewardName() {
        return Component.empty(); //TODO item name
    }

    @Override
    public List<Component> getRewardLore() {
        return List.of(Component.empty()); //TODO item lore
    }
}
