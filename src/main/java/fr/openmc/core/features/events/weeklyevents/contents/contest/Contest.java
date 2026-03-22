package fr.openmc.core.features.events.weeklyevents.contents.contest;

import fr.openmc.core.features.events.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.registry.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Contest extends WeeklyEvent {
    @Override
    public Component getName() {
        return Component.text("Contest");
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.text("2 camps s'affrontent pendant le weekend"),
                Component.text("Votez pour votre camp, et participez à l'affrontement !"),
                Component.text("Le camp gagnant remportera des récompenses exclusives !")
        );
    }

    @Override
    public ItemStack getIcon() {
        return CustomItemRegistry.getByName("omc_contest:contest_shell").getBest();
    }

    @Override
    public List<WeeklyEventPhase> getPhases() {
        return Arrays.stream(ContestPhase.values()).map(ContestPhase::getPhase).toList();
    }
}
