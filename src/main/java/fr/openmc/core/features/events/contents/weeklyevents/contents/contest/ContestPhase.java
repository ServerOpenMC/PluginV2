package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.time.DayOfWeek;
import java.util.List;

@Getter
public enum ContestPhase {
    VOTE_CAMP(new WeeklyEventPhase() {
        @Override
        public Component getName() {
            return TranslationManager.translation("feature.events.contest.phase.vote.name");
        }

        @Override
        public List<Component> getDescription() {
            return TranslationManager.translationLore("feature.events.contest.phase.vote.lore");
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
            return TranslationManager.translation("feature.events.contest.phase.trade.name");
        }

        @Override
        public List<Component> getDescription() {
            return TranslationManager.translationLore("feature.events.contest.phase.trade.lore");
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
            return TranslationManager.translation("feature.events.contest.phase.end.name");
        }

        @Override
        public List<Component> getDescription() {
            return TranslationManager.translationLore("feature.events.contest.phase.end.lore");
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
