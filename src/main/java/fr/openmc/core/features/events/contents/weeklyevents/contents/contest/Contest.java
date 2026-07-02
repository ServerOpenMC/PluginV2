package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.MoreInfoMenu;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.models.HasMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Contest extends WeeklyEvent implements HasMenu {
    @Override
    public Component getName() {
        return Component.text("Contest", NamedTextColor.GOLD, TextDecoration.BOLD);
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.text("2 camps s'affrontent pendant le weekend", NamedTextColor.GRAY),
                Component.text("Votez pour votre camp, et participez à l'affrontement !", NamedTextColor.GRAY),
                Component.text("Le camp gagnant remportera des récompenses exclusives !", NamedTextColor.GRAY)
        );
    }

    @Override
    public ItemStack getIcon() {
        return OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL.getBest();
    }

    @Override
    public List<WeeklyEventPhase> getPhases() {
        return Arrays.stream(ContestPhase.values()).map(ContestPhase::getPhase).toList();
    }

    @Override
    public Menu getInfoMenu(Player player) {
        return new MoreInfoMenu(player);
    }
}
