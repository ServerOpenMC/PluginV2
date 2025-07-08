package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestBuilder;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CraftDiamondArmorQuest extends Quest implements Listener {

    public CraftDiamondArmorQuest() {
        super(
                "Armure précieuse",
                "Fabriquer une armure complète en diamant",
                new ItemStack(Material.DIAMOND_CHESTPLATE)
        );

        Quest quest = new QuestBuilder("Armure précieuse", "Fabriquer une armure complète en diamant", new ItemStack(Material.DIAMOND_CHESTPLATE))
                .tier(4, "Fabriquer une armure complète en diamant",  new QuestItemReward(Material.DIAMOND, 10))
                .step("Casque en diamant", 1)
                .step("Plastron en diamant", 1)
                .step("Pantalon en diamant", 1)
                .step("Bottes en diamant", 1)
                .requireAllSteps(true)
                .build();

        for (int i = 0; i < quest.getTiers().size(); i++) {
            this.addTier(quest.getTiers().get(i));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        UUID playerUUID = player.getUniqueId();
        Material craftedItem = event.getCurrentItem().getType();

        if (getCurrentTierIndex(playerUUID) != 0) {
            return;
        }

        switch (craftedItem) {
            case DIAMOND_HELMET -> {
                this.incrementStepProgress(playerUUID, 0);
                this.incrementProgress(playerUUID);
            }
            case DIAMOND_CHESTPLATE -> {
                this.incrementStepProgress(playerUUID, 1);
                this.incrementProgress(playerUUID);
            }
            case DIAMOND_LEGGINGS -> {
                this.incrementStepProgress(playerUUID, 2);
                this.incrementProgress(playerUUID);
            }
            case DIAMOND_BOOTS -> {
                this.incrementStepProgress(playerUUID, 3);
                this.incrementProgress(playerUUID);
            }
        }
    }
}