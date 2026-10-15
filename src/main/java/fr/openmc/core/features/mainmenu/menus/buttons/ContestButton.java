package fr.openmc.core.features.mainmenu.menus.buttons;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.ContestCommand;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.mainmenu.menus.ButtonItems;
import fr.openmc.core.features.mainmenu.menus.ButtonMenu;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.time.DayOfWeek;
import java.util.Set;

public class ContestButton implements ButtonMenu {

    @Override
    public Set<Integer> slots() {
        return Set.of(54, 55, 56, 57, 58);
    }

    @Override
    public ItemStack item(OMCPlayer player) {
        if (!(WeeklyEventsManager.getCurrentEvent() instanceof Contest && WeeklyEventsManager.getCurrentPhase() != ContestPhase.END_PHASE.getPhase())) {
            return ButtonItems.item(
                    ButtonItems.name("feature.mainmenu.item.contest.name"),
                    ButtonItems.gray("feature.mainmenu.item.contest.command"));
        }

        Component time = Component.text(DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY))
                .color(NamedTextColor.RED);

        return ButtonItems.item(
                ContestManager.data.getCampVSComponent(),
                TranslationManager.translation("feature.mainmenu.item.contest.ends_in", time)
                        .color(NamedTextColor.RED),
                ButtonItems.gray("feature.mainmenu.item.contest.command"));
    }

    @Override
    public void open(OMCPlayer player) {
        ContestCommand.mainCommand(player);
    }
}