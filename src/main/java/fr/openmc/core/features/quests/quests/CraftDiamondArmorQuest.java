package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestBuilder;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class CraftDiamondArmorQuest extends Quest implements Listener {

    public CraftDiamondArmorQuest() {
        super(
                TranslationManager.translationString("feature.quests.craft_diamond_armor.name"),
                List.of(TranslationManager.translationString("feature.quests.craft_diamond_armor.description")),
                new ItemStack(Material.DIAMOND_CHESTPLATE)
        );

        Quest quest = new QuestBuilder(
                TranslationManager.translationString("feature.quests.craft_diamond_armor.name"),
                List.of(TranslationManager.translationString("feature.quests.craft_diamond_armor.description")),
                new ItemStack(Material.DIAMOND_CHESTPLATE)
        )
                .tier(4, TranslationManager.translationString("feature.quests.craft_diamond_armor.description"), new QuestItemReward(Material.DIAMOND, 10))
                .step(TranslationManager.translationString("feature.quests.craft_diamond_armor.step.helmet"), 1)
                .step(TranslationManager.translationString("feature.quests.craft_diamond_armor.step.chestplate"), 1)
                .step(TranslationManager.translationString("feature.quests.craft_diamond_armor.step.leggings"), 1)
                .step(TranslationManager.translationString("feature.quests.craft_diamond_armor.step.boots"), 1)
                .requireAllSteps(true)
                .build();

        quest.getTiers().forEach(this::addTier);
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