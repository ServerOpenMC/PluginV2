package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.milestones.*;
import fr.openmc.core.features.milestones.bossbar.MilestoneBossBarOptions;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TutorialMilestone implements Milestone<TutorialStep> {
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
    public Class<TutorialStep> getStepClass() {
        return TutorialStep.class;
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
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
    }

    @Override
    public boolean shouldDisplayBossBar(Player player) {
        return !DreamUtils.isInDreamWorld(player) && !MilestoneUtils.hasFinishedMilestone(this.getType(), player);
    }
}
