package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ContestPhase {
    VOTE_CAMP(new WeeklyEventPhase() {
        @Override
        public Component getName() {
            return Component.text("Les votes");
        }

        @Override
        public List<Component> getDescription() {
            return Arrays.asList(
                    Component.text("§7Tout les vendredi, le contest commence"),
                    Component.text("§7Et les votes s'ouvrent, et il faut choisir"),
                    Component.text("§7Entre 2 camps, une ambience se crée dans le spawn...")
            );
        }

        @Override
        public DayOfWeek getStartDay() {
            return DayOfWeek.FRIDAY;
        }

        @Override
        public int getStartHour() {
            return 0;
        }

        @Override
        public int getStartMinutes() {
            return 0;
        }

        @Override
        public Runnable runAction() {
            return ContestManager::initPhase1;
        }
    }),
    TRADE_PHASE(new WeeklyEventPhase() {
        @Override
        public Component getName() {
            return Component.text("L'affrontement");
        }

        @Override
        public List<Component> getDescription() {
            return Arrays.asList(
                    Component.text("§7La nuit tombe sur le spawn pendant 2 jours"),
                    Component.text("§7Que la fête commence !"),
                    Component.text("§7Des trades sont disponible"),
                    Component.text("§7Donnant des coquillages de contest !"));
        }

        @Override
        public DayOfWeek getStartDay() {
            return DayOfWeek.SATURDAY;
        }

        @Override
        public int getStartHour() {
            return 0;
        }

        @Override
        public int getStartMinutes() {
            return 0;
        }

        @Override
        public Runnable runAction() {
            return ContestManager::initPhase2;
        }
    }),
    END_PHASE(new WeeklyEventPhase() {
        @Override
        public Component getName() {
            return Component.text("Les résultats");
        }

        @Override
        public List<Component> getDescription() {
            return Arrays.asList(
                    Component.text("§7Le levé de soleil sur le spawn !"),
                    Component.text("§7Les résultats tombent, et un camp"),
                    Component.text("§7sera gagnant. Et des récompenses seront attribuées"),
                    Component.text(("§7à chacun.")));
        }

        @Override
        public DayOfWeek getStartDay() {
            return DayOfWeek.MONDAY;
        }

        @Override
        public int getStartHour() {
            return 0;
        }

        @Override
        public int getStartMinutes() {
            return 0;
        }

        @Override
        public Runnable runAction() {
            return ContestManager::initPhase3;
        }
    })
    ;

    private final WeeklyEventPhase phase;
    ContestPhase(WeeklyEventPhase phase) {
        this.phase = phase;
    }
}
