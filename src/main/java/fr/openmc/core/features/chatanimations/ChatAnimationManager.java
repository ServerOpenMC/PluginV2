package fr.openmc.core.features.chatanimations;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Credit(developers = {"iambibi_", "PuppyTransGirl"})
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
    public Set<ListenerFactory> getListeners() {
        return Set.of(QuizzListener::new, ChallengeListener::new);
    }

    @Override
    public void init() {
        ANIMATIONS = new HashSet<>(Set.of(
                new MineBlocksChallenge("challenge.mine.diamond_ore", KeyBlock.vanilla(BlockType.DIAMOND_ORE), 2, 60L),
                new MineBlocksChallenge("challenge.mine.sculk", KeyBlock.vanilla(BlockType.SCULK), 15, 60L),
                new MineBlocksChallenge("challenge.mine.beehive", KeyBlock.vanilla(BlockType.BEEHIVE), 1, 60L),
                new MineBlocksChallenge("challenge.mine.aywenite_block", KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK), 1, 60L),
                new MineBlocksChallenge("challenge.mine.cobblestone", KeyBlock.vanilla(BlockType.COBBLESTONE), 32, 90L),

                new JumpChallenge("challenge.jump.10", 10, 30L),
                new JumpChallenge("challenge.jump.25", 25, 45L),
                new JumpChallenge("challenge.jump.50", 50, 90L),

                new KillEntityChallenge("challenge.kill.cow", EntityType.COW, 15, 60L),
                new KillEntityChallenge("challenge.kill.zombie", EntityType.ZOMBIE, 5, 60L),
                new KillEntityChallenge("challenge.kill.axolotl", EntityType.AXOLOTL, 1, 120L),

                new CraftItemChallenge("challenge.craft.stick", ItemStack.of(Material.STICK), 32, 45L),
                new CraftItemChallenge("challenge.craft.crafting_table", ItemStack.of(Material.CRAFTING_TABLE), 1, 20L),
                new CraftItemChallenge("challenge.craft.bread", ItemStack.of(Material.BREAD), 10, 60L),
                new CraftItemChallenge("challenge.craft.the_mixture", OMCRegistry.CUSTOM_ITEMS.THE_MIXTURE, 1, 60L),

                new FishingChallenge("challenge.fish.3", 3, 90L),
                new FishingChallenge("challenge.fish.5", 5, 120L),

                new PlaceBlocksChallenge("challenge.place.oak_planks", KeyBlock.vanilla(BlockType.OAK_PLANKS), 50, 60L),
                new PlaceBlocksChallenge("challenge.place.torch", KeyBlock.vanilla(BlockType.TORCH), 20, 45L),
                new PlaceBlocksChallenge("challenge.place.white_wool", KeyBlock.vanilla(BlockType.WHITE_WOOL), 10, 60L),
                new PlaceBlocksChallenge("challenge.place.elevator_gray", KeyBlock.custom(OMCRegistry.CUSTOM_ITEMS.ELEVATOR_GRAY), 1, 120L),

                new WalkDistanceChallenge("challenge.walk.200", 200, 60L),
                new WalkDistanceChallenge("challenge.walk.500", 500, 120L),
                new WalkDistanceChallenge("challenge.walk.1000", 1000, 180L),

                new Quizz("quiz.tech.python", TranslationManager.translation("quizz.tech.01"), "python"),
                new Quizz("quiz.tech.html", TranslationManager.translation("quizz.tech.02"), "hypertext markup language", "hyper text markup language"),
                new Quizz("quiz.tech.windows", TranslationManager.translation("quizz.tech.03"), "microsoft"),
                new Quizz("quiz.tech.linux", TranslationManager.translation("quizz.tech.04"), "linus torvalds", "torvalds"),
                new Quizz("quiz.tech.byte", TranslationManager.translation("quizz.tech.05"), "8"),
                new Quizz("quiz.tech.serpent", TranslationManager.translation("quizz.tech.06"), "python"),
                new Quizz("quiz.tech.protocol", TranslationManager.translation("quizz.tech.07"), "https"),
                new Quizz("quiz.tech.facebook", TranslationManager.translation("quizz.tech.08"), "mark zuckerberg", "zuckerberg"),
                new Quizz("quiz.tech.minecraft", TranslationManager.translation("quizz.tech.09"), "mojang"),
                new Quizz("quiz.tech.extension", TranslationManager.translation("quizz.tech.10"), "png"),
                new Quizz("quiz.tech.google", TranslationManager.translation("quizz.tech.11"), "chrome", "google chrome"),
                new Quizz("quiz.tech.ram", TranslationManager.translation("quizz.tech.12"), "random access memory"),

                new Quizz("quiz.general.france", TranslationManager.translation("quizz.general.01"), "paris"),
                new Quizz("quiz.general.earth", TranslationManager.translation("quizz.general.02"), "7"),
                new Quizz("quiz.general.joconde", TranslationManager.translation("quizz.general.03"), "léonard de vinci", "leonard de vinci", "de vinci"),
                new Quizz("quiz.general.ocean", TranslationManager.translation("quizz.general.04"), "pacifique", "océan pacifique", "ocean pacifique"),
                new Quizz("quiz.general.revolution", TranslationManager.translation("quizz.general.05"), "1789"),
                new Quizz("quiz.general.highest", TranslationManager.translation("quizz.general.06"), "everest", "mont everest"),
                new Quizz("quiz.general.football", TranslationManager.translation("quizz.general.07"), "11"),
                new Quizz("quiz.general.planet", TranslationManager.translation("quizz.general.08"), "mars"),
                new Quizz("quiz.general.novel", TranslationManager.translation("quizz.general.09"), "victor hugo", "hugo"),
                new Quizz("quiz.general.animal", TranslationManager.translation("quizz.general.10"), "guépard", "guepard"),
                new Quizz("quiz.general.money", TranslationManager.translation("quizz.general.11"), "yen"),
                new Quizz("quiz.general.year", TranslationManager.translation("quizz.general.12"), "366"),
                new Quizz("quiz.general.country", TranslationManager.translation("quizz.general.13"), "italie"),
                new Quizz("quiz.general.music", TranslationManager.translation("quizz.general.14"), "beethoven", "ludwig van beethoven"),
                new Quizz("quiz.general.river", TranslationManager.translation("quizz.general.15"), "nil", "le nil"),
                new Quizz("quiz.general.chemistry", TranslationManager.translation("quizz.general.16"), "au"),
                new Quizz("quiz.general.island", TranslationManager.translation("quizz.general.17"), "groenland"),
                new Quizz("quiz.general.moon", TranslationManager.translation("quizz.general.18"), "neil armstrong", "armstrong"),
                new Quizz("quiz.general.sport", TranslationManager.translation("quizz.general.19"), "sumo", "le sumo"),
                new Quizz("quiz.general.light", TranslationManager.translation("quizz.general.20"), "8", "8 minutes", "8 min"),
                new Quizz("quiz.general.thunderstorm", TranslationManager.translation("quizz.general.21"), "3"),

                new Quizz("quiz.server.github", TranslationManager.translation("quizz.server.01"), "aywen1"),
                new Quizz("quiz.server.minecraft", TranslationManager.translation("quizz.server.02"), "notch", "markus persson", "persson"),
                new Quizz("quiz.server.creation", TranslationManager.translation("quizz.server.03"), "aywen"),
                new Quizz("quiz.server.aywen", TranslationManager.translation("quizz.server.04"), "jorbani"),
                new Quizz("quiz.server.openmc", TranslationManager.translation("quizz.server.05"), "2024")
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
        if (!Bukkit.getOnlinePlayers().isEmpty())
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

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
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
