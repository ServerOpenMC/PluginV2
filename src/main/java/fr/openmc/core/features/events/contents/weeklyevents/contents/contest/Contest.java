package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.MoreInfoMenu;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.models.HasMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Contest extends WeeklyEvent implements HasMenu {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.events.contest.name");
    }

    @Override
    public List<Component> getDescription() {
        return TranslationManager.translationLore("feature.events.contest.description");
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
