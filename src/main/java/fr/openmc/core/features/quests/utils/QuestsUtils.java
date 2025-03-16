package fr.openmc.core.features.quests.utils;

import fr.openmc.core.features.quests.PlayerQuests;
import fr.openmc.core.features.quests.qenum.QUESTS;
import fr.openmc.core.utils.messages.MessagesManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestsUtils {
	
	public static void createItems(QUESTS quest, ItemStack item, ItemMeta meta, PlayerQuests pq, Player player) {
		int currentTier = pq.getCurrentTier(quest);
		int progress = pq.getProgress(quest);
		
		if(currentTier < 0 || currentTier >= quest.getQtTiers().length) {
			currentTier = 0;
		}
		
		int total = quest.getQt(currentTier);
		int progressPercent = (int) Math.min(100, Math.ceil((double) progress / total * 100));
		int progressBarLength = 20;
		int filledLength = (int) (progressBarLength * ((double) progress / total));
		StringBuilder progressBar = new StringBuilder("§8[§m§a");
		
		for(int i = 0; i < progressBarLength; i++) {
			progressBar.append(i < filledLength ? "§a§m " : "§8§m ");
		}
		
		progressBar.append("§8]");
		
		if (meta != null) {
			if (pq.isQuestCompleted(quest)) {
				meta.addEnchant(Enchantment.SHARPNESS, 5, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			meta.setDisplayName(
					MessagesManager.textToSmall(pq.isQuestCompleted(quest)
							? "§8§m§o" + quest.getName() + "§r §7§m§o(Tier " + (currentTier + 1) + "/" + quest.getQtTiers().length + ")§r §8§m(§a§mComplétée§8§m)"
							: "§a" + quest.getName() + " §7(Tier " + (currentTier + 1) + "/" + quest.getQtTiers().length + ")"
					)
			);
			
			List<String> lore = new ArrayList<>();
			lore.add(MessagesManager.textToSmall("§7Description") + " : §f" + quest.getDesc(currentTier));
			lore.add(MessagesManager.textToSmall("§7Récompense") + " : §f" + (quest.getRewardsMaterial() != null
					? "x" + quest.getRewardsQt(currentTier) + " " + PlainTextComponentSerializer.plainText().serialize(GlobalTranslator.render(Component.translatable(quest.getRewardsMaterial()), player.locale()))
					: quest.getRewardsQt(currentTier) + "$"));
			if(pq.isQuestCompleted(quest)) {
				lore.add(MessagesManager.textToSmall("§a§lQuête complétée !"));
			} else {
				lore.add(MessagesManager.textToSmall("§eEn cours") + " : " + pq.getProgress(quest) + "/" + quest.getQt(currentTier));
				if(currentTier < quest.getQtTiers().length - 1) {
					lore.add(MessagesManager.textToSmall("§7Prochain tier : §f") + quest.getDesc(currentTier + 1));
				}
			}
			lore.add("§7");
			lore.add(progressBar + " §7" + progressPercent + "%");
			
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}
	
}