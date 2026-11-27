package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.loottable.LootReward;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@Getter
public class LootboxLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final CustomLootbox lootbox;

    public LootboxLoot(CustomLootbox lootbox) {
        this.chance = 1;
        this.lootbox = lootbox;
    }

    public LootboxLoot(CustomLootbox lootbox, double chance) {
        this.chance = chance;
        this.lootbox = lootbox;
    }

    @Override
    public Component getDisplayText() {
        return lootbox.getName();
    }

    @Override
    public LootReward run(OfflinePlayer receiver) {
        if (receiver.isOnline() && receiver.getPlayer() != null) {
            lootbox.open(receiver.getPlayer());
        } else {
            MailboxManager.sendItemsToOfflinePlayer(receiver, new ItemStack[] {lootbox.getItemDisplayed()});
        }

        return LootReward.loots(Collections.singleton(this));
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return lootbox.getItemDisplayed();
    }
}