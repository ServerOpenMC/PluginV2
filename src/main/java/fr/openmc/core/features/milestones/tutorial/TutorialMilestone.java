package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.milestones.MilestoneUtils;
import fr.openmc.core.features.milestones.bossbar.MilestoneBossBarOptions;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneModel;
import fr.openmc.core.features.milestones.models.MilestoneType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TutorialMilestone implements Milestone<TutorialSteps> {

    private static final HashMap<UUID, MilestoneModel> playerData = new HashMap<>();

    @Override
    public HashMap<UUID, MilestoneModel> getPlayerData() {
        return playerData;
    }

    @Override
    public String getName() {
        return "§7Tutoriel d'OpenMC";
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.text("§7Découvrez §dOpenMC §7!"),
                Component.text("§7Passez en revue les §dfeatures"),
                Component.text("§8§oLes villes, les contests, l'adminshop, les quêtes, ..."),
                Component.text("§7Idéal pour se lancer dans l'aventure !")
        );
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GRASS_BLOCK);
    }

    @Override
    public Class<TutorialSteps> getStepClass() {
        return TutorialSteps.class;
    }

    @Override
    public MilestoneType getType() {
        return MilestoneType.TUTORIAL;
    }

    @Override
    public Menu getMenu(Player player) {
        return new MilestoneMenu(player, this);
    }

    @Override
    public MilestoneBossBarOptions getBossBarOptions() {
        return new MilestoneBossBarOptions(
                NamedTextColor.GOLD,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
    }

    @Override
    public boolean shouldDisplayBossBar(Player player) {
        return !DreamUtils.isInDreamWorld(player) && !MilestoneUtils.hasFinishedMilestone(this.getType(), player);
    }
}
