package fr.openmc.core.features.dream.milestone.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.DreamEnterEvent;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.items.tools.CrystalizedPickaxe;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.DirectionUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CrystallizedPickaxeQuest extends MilestoneQuest implements Listener {
	
	private static final int cubeX = -171;
	private static final int cubeZ = -117;
	private static final Location cubeLoc = new Location(DreamDimensionManager.DREAM_WORLD, cubeX, DreamDimensionManager.DREAM_WORLD.getHighestBlockYAt(cubeX, cubeZ), cubeZ);

	public CrystallizedPickaxeQuest() {
		super(
				TranslationManager.translation("feature.dream.milestone.quest.crystallized_pickaxe.name"),
				TranslationManager.translationLore("feature.dream.milestone.quest.crystallized_pickaxe.description"),
				DreamItemRegistry.CRYSTALIZED_PICKAXE,
				MilestoneType.DREAM,
				DreamSteps.CRYSTALLIZED_PICKAXE,
				new QuestTier(1),
				TranslationManager.translationLore("feature.dream.milestone.quest.crystallized_pickaxe.dialog",
						Component.text(cubeX).color(NamedTextColor.RED),
						Component.text(cubeZ).color(NamedTextColor.BLUE)
				),
				(player) -> new BukkitRunnable() {
					@Override
					public void run() {
						if (!player.isOnline() || !DreamUtils.isInDream(player)) {
							this.cancel();
							return;
						}
						
						if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) > DreamSteps.FIND_CUBE.ordinal()) {
							this.cancel();
							return;
						}
						int distance = (int) player.getLocation().distance(CrystallizedPickaxeQuest.cubeLoc);
						String direction = DirectionUtils.getDirectionArrow(player, CrystallizedPickaxeQuest.cubeLoc);
						player.sendActionBar(TranslationManager.translation(
								"feature.dream.actionbar.cube_distance",
								Component.text(distance).color(NamedTextColor.GOLD),
								Component.text(direction)
						));
					}
				}.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L)
		);
	}
	
	@EventHandler
	public void onPickUp(MetalDetectorLootEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;

		for (CustomLoot loot : e.getLoot()) {
			if (!(loot instanceof ItemLoot itemLoot)) continue;

			for (ItemStack item : itemLoot.getItems()) {
				DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
				if (dreamItem == null) return;
				if (dreamItem instanceof CrystalizedPickaxe) {
					if (MilestonesManager.getPlayerStep(getType(), player) != getStep().ordinal()) continue;
					this.incrementProgressInDream(player.getUniqueId());
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerReturnDim(DreamEnterEvent e) {
		Player player = e.getPlayer();
		if (MilestonesManager.getPlayerStep(MilestoneType.DREAM, player) == DreamSteps.FIND_CUBE.ordinal()) {
			this.actionsAfterDialog.accept(player);
		}
	}
}
