package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestBuilder;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CraftDiamondArmorQuest extends Quest implements Listener {

    private static final int HELMET_STEP = 0;
    private static final int CHESTPLATE_STEP = 1;
    private static final int LEGGINGS_STEP = 2;
    private static final int BOOTS_STEP = 3;

    public CraftDiamondArmorQuest() {
        super(
                "Armure précieuse",
                "Fabriquez une armure complète en diamant",
                new ItemStack(Material.DIAMOND_CHESTPLATE)
        );

        Quest quest = new QuestBuilder("Armure précieuse", "Fabriquez une armure complète en diamant", new ItemStack(Material.DIAMOND_CHESTPLATE))
                .tier(4, new QuestItemReward(Material.DIAMOND, 10), "Fabriquer une armure complète en diamant")
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

    @EventHandler
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
                incrementStepProgress(playerUUID, HELMET_STEP, 1);
                incrementProgress(playerUUID, 1);
            }
            case DIAMOND_CHESTPLATE -> {
                incrementStepProgress(playerUUID, CHESTPLATE_STEP, 1);
                incrementProgress(playerUUID, 1);
            }
            case DIAMOND_LEGGINGS -> {
                incrementStepProgress(playerUUID, LEGGINGS_STEP, 1);
                incrementProgress(playerUUID, 1);
            }
            case DIAMOND_BOOTS -> {
                incrementStepProgress(playerUUID, BOOTS_STEP, 1);
                incrementProgress(playerUUID, 1);
            }
        }
    }
}