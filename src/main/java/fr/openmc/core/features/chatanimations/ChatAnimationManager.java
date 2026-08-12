package fr.openmc.core.features.chatanimations;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeListener;
import fr.openmc.core.features.chatanimations.contents.challenge.types.*;
import fr.openmc.core.features.chatanimations.contents.quizz.Quizz;
import fr.openmc.core.features.chatanimations.contents.quizz.QuizzListener;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Credit(developers = {"iambibi_"})
public class ChatAnimationManager extends Feature implements LoadAfterItemsAdder, HasListeners {

    private static final long MIN_DELAY_TICKS = 20 * 60 * 20L; // 20 min
    private static final long MAX_DELAY_TICKS = 20 * 60 * 30L; // 30 min

    private Set<ChatAnimation> ANIMATIONS;

    @Getter
    private static ChatAnimation currentAnimation;
    @Getter
    private static ChatAnimation lastAnimation;
    private BukkitTask scheduleTask;
    private static BukkitTask endAnimationTask;

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new QuizzListener(),
                new ChallengeListener()
        );
    }

    @Override
    public void init() {
        ANIMATIONS = new HashSet<>(Set.of(
                new MineBlocksChallenge(KeyBlock.vanilla(BlockType.DIAMOND_ORE), 2, 60L),
                new MineBlocksChallenge(KeyBlock.vanilla(BlockType.SCULK), 15, 60L),
                new MineBlocksChallenge(KeyBlock.vanilla(BlockType.BEEHIVE), 1, 60L),
                new MineBlocksChallenge(KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK), 1, 60L),
                new MineBlocksChallenge(KeyBlock.vanilla(BlockType.COBBLESTONE), 32, 90L),

                new JumpChallenge(10, 30L),
                new JumpChallenge(25, 45L),
                new JumpChallenge(50, 90L),

                new KillEntityChallenge(EntityType.COW, 15, 60L),
                new KillEntityChallenge(EntityType.ZOMBIE, 5, 60L),
                new KillEntityChallenge(EntityType.AXOLOTL, 1, 120L),

                new CraftItemChallenge(ItemStack.of(Material.STICK), 32, 45L),
                new CraftItemChallenge(ItemStack.of(Material.CRAFTING_TABLE), 1, 20L),
                new CraftItemChallenge(ItemStack.of(Material.BREAD), 10, 60L),
                new CraftItemChallenge(OMCRegistry.CUSTOM_ITEMS.THE_MIXTURE, 1, 60L),

                new FishingChallenge(3, 90L),
                new FishingChallenge(5, 120L),

                new PlaceBlocksChallenge(KeyBlock.vanilla(BlockType.OAK_PLANKS), 50, 60L),
                new PlaceBlocksChallenge(KeyBlock.vanilla(BlockType.TORCH), 20, 45L),
                new PlaceBlocksChallenge(KeyBlock.vanilla(BlockType.WHITE_WOOL), 10, 60L),
                new PlaceBlocksChallenge(KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.ELEVATOR_GRAY), 1, 120L),

                new WalkDistanceChallenge(200, 60L),
                new WalkDistanceChallenge(500, 120L),
                new WalkDistanceChallenge(1000, 180L),

                new Quizz(TranslationManager.translation("quizz.tech.01"), "python"),
                new Quizz(TranslationManager.translation("quizz.tech.02"), "hypertext markup language", "hyper text markup language"),
                new Quizz(TranslationManager.translation("quizz.tech.03"), "microsoft"),
                new Quizz(TranslationManager.translation("quizz.tech.04"), "linus torvalds", "torvalds"),
                new Quizz(TranslationManager.translation("quizz.tech.05"), "8"),
                new Quizz(TranslationManager.translation("quizz.tech.06"), "python"),
                new Quizz(TranslationManager.translation("quizz.tech.07"), "https"),
                new Quizz(TranslationManager.translation("quizz.tech.08"), "mark zuckerberg", "zuckerberg"),
                new Quizz(TranslationManager.translation("quizz.tech.09"), "mojang"),
                new Quizz(TranslationManager.translation("quizz.tech.10"), "png"),
                new Quizz(TranslationManager.translation("quizz.tech.11"), "chrome", "google chrome"),
                new Quizz(TranslationManager.translation("quizz.tech.12"), "random access memory"),
                new Quizz(TranslationManager.translation("quizz.tech.13"), "css"),
                new Quizz(TranslationManager.translation("quizz.tech.14"), "apple"),
                new Quizz(TranslationManager.translation("quizz.tech.15"), "central processing unit"),
                new Quizz(TranslationManager.translation("quizz.tech.16"), "java"),
                new Quizz(TranslationManager.translation("quizz.tech.17"), "git"),
                new Quizz(TranslationManager.translation("quizz.tech.18"), "uniform resource locator"),
                new Quizz(TranslationManager.translation("quizz.tech.19"), "android"),
                new Quizz(TranslationManager.translation("quizz.tech.20"), "microsoft"),
                new Quizz(TranslationManager.translation("quizz.tech.21"), "structured query language"),
                new Quizz(TranslationManager.translation("quizz.tech.22"), "ram", "mémoire vive", "memoire vive"),

                new Quizz(TranslationManager.translation("quizz.general.01"), "paris"),
                new Quizz(TranslationManager.translation("quizz.general.02"), "7"),
                new Quizz(TranslationManager.translation("quizz.general.03"), "léonard de vinci", "leonard de vinci", "de vinci"),
                new Quizz(TranslationManager.translation("quizz.general.04"), "pacifique", "océan pacifique", "ocean pacifique"),
                new Quizz(TranslationManager.translation("quizz.general.05"), "1789"),
                new Quizz(TranslationManager.translation("quizz.general.06"), "everest", "mont everest"),
                new Quizz(TranslationManager.translation("quizz.general.07"), "11"),
                new Quizz(TranslationManager.translation("quizz.general.08"), "mars"),
                new Quizz(TranslationManager.translation("quizz.general.09"), "victor hugo", "hugo"),
                new Quizz(TranslationManager.translation("quizz.general.10"), "guépard", "guepard"),
                new Quizz(TranslationManager.translation("quizz.general.11"), "yen"),
                new Quizz(TranslationManager.translation("quizz.general.12"), "366"),
                new Quizz(TranslationManager.translation("quizz.general.13"), "italie"),
                new Quizz(TranslationManager.translation("quizz.general.14"), "beethoven", "ludwig van beethoven"),
                new Quizz(TranslationManager.translation("quizz.general.15"), "nil", "le nil"),
                new Quizz(TranslationManager.translation("quizz.general.16"), "au"),
                new Quizz(TranslationManager.translation("quizz.general.17"), "groenland"),
                new Quizz(TranslationManager.translation("quizz.general.18"), "neil armstrong", "armstrong"),
                new Quizz(TranslationManager.translation("quizz.general.19"), "sumo", "le sumo"),
                new Quizz(TranslationManager.translation("quizz.general.20"), "8", "8 minutes", "8 min"),
                new Quizz(TranslationManager.translation("quizz.general.21"), "3"),
                new Quizz(TranslationManager.translation("quizz.general.22"), "rome"),
                new Quizz(TranslationManager.translation("quizz.general.23"), "6"),
                new Quizz(TranslationManager.translation("quizz.general.24"), "shakespeare", "william shakespeare"),
                new Quizz(TranslationManager.translation("quizz.general.25"), "vatican", "le vatican"),
                new Quizz(TranslationManager.translation("quizz.general.26"), "206"),
                new Quizz(TranslationManager.translation("quizz.general.27"), "tour eiffel", "la tour eiffel"),
                new Quizz(TranslationManager.translation("quizz.general.28"), "atlantique", "océan atlantique", "ocean atlantique"),
                new Quizz(TranslationManager.translation("quizz.general.29"), "einstein", "albert einstein"),
                new Quizz(TranslationManager.translation("quizz.general.30"), "euro"),
                new Quizz(TranslationManager.translation("quizz.general.31"), "7"),
                new Quizz(TranslationManager.translation("quizz.general.32"), "sahara"),
                new Quizz(TranslationManager.translation("quizz.general.33"), "salto angel", "chute angel", "angel"),
                new Quizz(TranslationManager.translation("quizz.general.34"), "24"),
                new Quizz(TranslationManager.translation("quizz.general.35"), "lune", "la lune"),
                new Quizz(TranslationManager.translation("quizz.general.36"), "portugais"),
                new Quizz(TranslationManager.translation("quizz.general.37"), "van gogh", "vincent van gogh"),
                new Quizz(TranslationManager.translation("quizz.general.38"), "baleine bleue"),
                new Quizz(TranslationManager.translation("quizz.general.39"), "365", "365 jours", "un an"),
                new Quizz(TranslationManager.translation("quizz.general.40"), "chine"),
                new Quizz(TranslationManager.translation("quizz.general.41"), "tokyo"),

                new Quizz(TranslationManager.translation("quizz.server.01"), "aywen1"),
                new Quizz(TranslationManager.translation("quizz.server.02"), "notch", "markus persson", "persson"),
                new Quizz(TranslationManager.translation("quizz.server.03"), "aywen"),
                new Quizz(TranslationManager.translation("quizz.server.04"), "jorbani"),
                new Quizz(TranslationManager.translation("quizz.server.05"), "2024")
        ));

        scheduleNext();
    }

    @Override
    public void save() {
        if (scheduleTask != null) scheduleTask.cancel();
        forceStopCurrent();
    }

    private void scheduleNext() {
        long delay = RandomUtils.randomBetween(MIN_DELAY_TICKS, MAX_DELAY_TICKS);

        scheduleTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (isAnimationRunning()) {
                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::scheduleNext, 20 * 30L);
                return;
            }
            startNext();
            scheduleNext();
        }, delay);
    }

    public boolean isAnimationRunning() {
        return currentAnimation != null;
    }

    public void startNext() {
        if (isAnimationRunning()) return;

        List<ChatAnimation> pool = ANIMATIONS.stream()
                .filter(a -> !a.equals(lastAnimation))
                .toList();
        if (pool.isEmpty()) return;

        ChatAnimation animation = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

        launch(animation);
    }

    private void launch(ChatAnimation animation) {
        animation.setFinished(false);
        currentAnimation = animation;
        Bukkit.broadcast(currentAnimation.getAnnounceStart());

        long timeBeforeEndTicks = animation.getTimeBeforeEnd() * 20L;
        endAnimationTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (currentAnimation == animation && !animation.isFinished()) {
                animation.complete(null);
            }
        }, timeBeforeEndTicks);
    }

    public static void onAnimationCompleted(ChatAnimation animation, Player winner, LootReward loot) {
        if (currentAnimation != animation) return;

        if (endAnimationTask != null) {
            endAnimationTask.cancel();
            endAnimationTask = null;
        }

        if (winner != null) {
            Bukkit.broadcast(TranslationManager.translation("feature.chatanimations.winner",
                    winner.name().color(NamedTextColor.GOLD),
                    animation.getName(),
                    animation.getDescriptionResult(),
                    loot.buildComponent()
            ));
        } else {
            Bukkit.broadcast(TranslationManager.translation("feature.chatanimations.no_winner",
                    animation.getName(), animation.getDescriptionResult()));
        }

        lastAnimation = currentAnimation;
        currentAnimation = null;
    }

    public void forceStopCurrent() {
        if (currentAnimation != null) {
            currentAnimation.complete(null);
        }
    }

    public static ChatAnimation getActive() {
        return currentAnimation;
    }
}
