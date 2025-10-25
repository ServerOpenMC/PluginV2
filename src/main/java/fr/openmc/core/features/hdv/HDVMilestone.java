package fr.openmc.core.features.hdv;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.milestones.Milestone;
import fr.openmc.core.features.quests.objects.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class HDVMilestone implements Milestone {

    private static final String PREFIX = "§6[§eOpenMC-HDV§6] §r";

    private final String name;
    private final String description;

    public HDVMilestone(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public List<Quest> getSteps() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public List<Component> getDescription() {
        return Collections.singletonList(Component.text(description));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Menu getMenu(Player player) {
        return null;
    }

    @Override
    public fr.openmc.core.features.milestones.MilestoneType getType() {
        return null;
    }

    @SuppressWarnings("unused")
    public void onComplete(Player player) {
        player.sendMessage(PREFIX + "§aBravo! Vous avez débloqué le succès: §e" + getName());
    }
}
