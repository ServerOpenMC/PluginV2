package fr.openmc.core.features.chat.animations;

import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ChatAnimations {

    // Durées en secondes : 5 minutes pour les challenges, 60s pour les quizzes
    private static final int CHALLENGE_DURATION_SECONDS = 300; // 5 minutes
    private static final int QUIZ_DURATION_SECONDS = 60;
    private static final Random RANDOM = new Random();

    // Use enums defined in ChatAnimationsEnum
    private static final List<ChatAnimationsEnum.ChallengeType> CHALLENGES = Arrays.asList(ChatAnimationsEnum.ChallengeType.values());
    private static final List<ChatAnimationsEnum.QuizType> QUIZZES = Arrays.asList(ChatAnimationsEnum.QuizType.values());

    private static FinalState currentState = null;
    private static QuizState currentQuizState = null;

    private static class FinalState {
        ChatAnimationsEnum.ChallengeType challenge;
        long endTimeMillis;
        UUID winner;
        Map<UUID, Integer> progress = new HashMap<>();
        BukkitTask timeoutTask;
    }

    private static class QuizState {
        ChatAnimationsEnum.QuizType quiz;
        long endTimeMillis;
        UUID winner;
        BukkitTask timeoutTask;
    }

    public static void startChallenge() {
        startRandomChallenge();
    }

    public static void startQuiz() {
        startRandomQuiz();
    }

    private static void startRandomChallenge() {
        if (currentState != null || currentQuizState != null) return; // one event at a time

        ChatAnimationsEnum.ChallengeType chosen = CHALLENGES.get(RANDOM.nextInt(CHALLENGES.size()));
        FinalState s = new FinalState();
        s.challenge = chosen;
        s.endTimeMillis = System.currentTimeMillis() + CHALLENGE_DURATION_SECONDS * 1000L;
        s.progress = new HashMap<>();
        currentState = s;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            MessagesManager.sendMessage(p, Component.text("§aUn nouveau défi a commencé : §e" + chosen.getDescription() + " §7(Temps: " + CHALLENGE_DURATION_SECONDS + "s)"), Prefix.OPENMC, MessageType.INFO, true);
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentState == null) return;
                if (currentState.winner == null) {
                    Bukkit.broadcast(Component.text("§cLe défi est terminé, personne n'a gagné."));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        MessagesManager.sendMessage(p, Component.text("§cPersonne n'a réussi le défi : §e" + chosen.getDescription()), Prefix.OPENMC, MessageType.ERROR, true);
                    }
                }
                currentState = null;
            }
        }.runTaskLater(fr.openmc.core.OMCPlugin.getInstance(), CHALLENGE_DURATION_SECONDS * 20L);
        s.timeoutTask = task;
    }

    private static void startRandomQuiz() {
        if (currentQuizState != null || currentState != null) return;

        ChatAnimationsEnum.QuizType chosen = QUIZZES.get(RANDOM.nextInt(QUIZZES.size()));
        QuizState s = new QuizState();
        s.quiz = chosen;
        s.endTimeMillis = System.currentTimeMillis() + QUIZ_DURATION_SECONDS * 1000L;
        currentQuizState = s;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            MessagesManager.sendMessage(p, Component.text("§aRépondez dans le chat : §e" + chosen.getQuestion() + " §7(Temps: " + QUIZ_DURATION_SECONDS + "s)"), Prefix.OPENMC, MessageType.INFO, true);
        }

        BukkitTask qtask = new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (ChatAnimations.class) {
                    if (currentQuizState == null) return;
                    if (currentQuizState.winner == null) {
                        // Quiz finished with no winner — announce timeout and show one possible correct answer (français)
                        String sampleAnswer = currentQuizState.quiz.getRandomAnswer();
                        Bukkit.broadcast(Component.text("§cLe quiz est terminé, personne n'a donné la bonne réponse."));
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (sampleAnswer != null && !sampleAnswer.isEmpty()) {
                                MessagesManager.sendMessage(p, Component.text("§cPersonne n'a répondu correctement au quiz : §e" + currentQuizState.quiz.getQuestion() + " §7| Réponse possible : §e" + sampleAnswer), Prefix.OPENMC, MessageType.ERROR, true);
                            } else {
                                MessagesManager.sendMessage(p, Component.text("§cPersonne n'a répondu correctement au quiz : §e" + currentQuizState.quiz.getQuestion()), Prefix.OPENMC, MessageType.ERROR, true);
                            }
                        }
                    }
                    currentQuizState = null;
                }
            }
        }.runTaskLater(fr.openmc.core.OMCPlugin.getInstance(), QUIZ_DURATION_SECONDS * 20L);
        s.timeoutTask = qtask;
    }

    // Called by listeners to report progress (block breaks, kills, places, jumps, fish etc.)
    public static void notifyProgress(Player player, Object... args) {
        synchronized (ChatAnimations.class) {
            if (currentState == null) return;
            if (currentState.winner != null) return;

            ChatAnimationsEnum.ChallengeType ct = currentState.challenge;
            UUID uuid = player.getUniqueId();
            int newVal;
            switch (ct.getKind()) {
                case MINE_COUNT -> {
                    if (args.length == 0 || !(args[0] instanceof Integer)) return;
                    int amount = (Integer) args[0];
                    newVal = currentState.progress.getOrDefault(uuid, 0) + amount;
                    currentState.progress.put(uuid, newVal);
                    if (newVal >= ct.getTarget()) {
                        currentState.winner = uuid;
                        // cancel timeout task to avoid timeout executing after win
                        if (currentState.timeoutTask != null) {
                            currentState.timeoutTask.cancel();
                            currentState.timeoutTask = null;
                        }
                        giveReward(player);
                        Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a remporté le défi !"));
                        currentState = null;
                    }
                }
                case JUMP -> {
                    newVal = currentState.progress.getOrDefault(uuid, 0) + 1;
                    currentState.progress.put(uuid, newVal);
                    if (newVal >= ct.getTarget()) {
                        currentState.winner = uuid;
                        // cancel timeout task to avoid timeout executing after win
                        if (currentState.timeoutTask != null) {
                            currentState.timeoutTask.cancel();
                            currentState.timeoutTask = null;
                        }
                        giveReward(player);
                        Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a remporté le défi !"));
                        currentState = null;
                    }
                }
                case KILL_MOB -> {
                    if (args.length == 0) return;
                    EntityType killed = null;
                    if (args[0] instanceof EntityType) killed = (EntityType) args[0];
                    else if (args[0] instanceof String) {
                        try { killed = EntityType.valueOf(((String) args[0]).toUpperCase()); } catch (Exception ignored) {}
                    }
                    if (killed == null) return;
                    if (killed != ct.getEntityType()) return;
                    newVal = currentState.progress.getOrDefault(uuid, 0) + 1;
                    currentState.progress.put(uuid, newVal);
                    if (newVal >= ct.getTarget()) {
                        currentState.winner = uuid;
                        // cancel timeout task to avoid timeout executing after win
                        if (currentState.timeoutTask != null) {
                            currentState.timeoutTask.cancel();
                            currentState.timeoutTask = null;
                        }
                        giveReward(player);
                        Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a remporté le défi !"));
                        currentState = null;
                    }
                }
                case PLACE_BLOCK -> {
                    if (args.length == 0 || !(args[0] instanceof Material)) return;
                    Material mat = (Material) args[0];
                    if (!ct.getMaterials().contains(mat)) return;
                    newVal = currentState.progress.getOrDefault(uuid, 0) + 1;
                    currentState.progress.put(uuid, newVal);
                    if (newVal >= ct.getTarget()) {
                        currentState.winner = uuid;
                        // cancel timeout task to avoid timeout executing after win
                        if (currentState.timeoutTask != null) {
                            currentState.timeoutTask.cancel();
                            currentState.timeoutTask = null;
                        }
                        giveReward(player);
                        Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a remporté le défi !"));
                        currentState = null;
                    }
                }
                case MINE_MATERIAL -> {
                    if (args.length == 0 || !(args[0] instanceof Material)) return;
                    Material mat = (Material) args[0];
                    if (!ct.getMaterials().contains(mat)) return;
                    newVal = currentState.progress.getOrDefault(uuid, 0) + 1;
                    currentState.progress.put(uuid, newVal);
                    if (newVal >= ct.getTarget()) {
                        currentState.winner = uuid;
                        // cancel timeout task to avoid timeout executing after win
                        if (currentState.timeoutTask != null) {
                            currentState.timeoutTask.cancel();
                            currentState.timeoutTask = null;
                        }
                        giveReward(player);
                        Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a remporté le défi !"));
                        currentState = null;
                    }
                }
                case FISH -> {
                    if (args.length == 0 || !(args[0] instanceof String)) return;
                    if (!"FISH".equals(args[0])) return;
                    newVal = currentState.progress.getOrDefault(uuid, 0) + 1;
                    currentState.progress.put(uuid, newVal);
                    if (newVal >= ct.getTarget()) {
                        currentState.winner = uuid;
                        // cancel timeout task to avoid timeout executing after win
                        if (currentState.timeoutTask != null) {
                            currentState.timeoutTask.cancel();
                            currentState.timeoutTask = null;
                        }
                        giveReward(player);
                        Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a remporté le défi !"));
                        currentState = null;
                    }
                }
            }
        }
    }

    // Called by chat listener to handle quiz answers
    public static boolean processChatAnswer(Player player, String message) {
        synchronized (ChatAnimations.class) {
            if (currentQuizState == null) return false;
            if (currentQuizState.winner != null) return false;
            long now = System.currentTimeMillis();
            if (currentQuizState.endTimeMillis <= now) return false; // already expired

            if (currentQuizState.quiz.matches(message)) {
                currentQuizState.winner = player.getUniqueId();
                // cancel timeout task to avoid timeout executing after win
                if (currentQuizState.timeoutTask != null) {
                    currentQuizState.timeoutTask.cancel();
                    currentQuizState.timeoutTask = null;
                }
                giveReward(player);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                MessagesManager.sendMessage(player, Component.text("§aBien joué ! Vous avez répondu correctement au quiz."), Prefix.OPENMC, MessageType.SUCCESS, true);
                Bukkit.broadcast(Component.text("§aLe joueur §e" + player.getName() + " §a a répondu correctement au quiz !"));
                currentQuizState = null;
                return true;
            }
            return false;
        }
    }

    // Reward: 300..700
    public static void giveReward(Player player) {
        // Rewards now 300..700
        int amount = 300 + RANDOM.nextInt(401); // 300..700
        EconomyManager.addBalance(player.getUniqueId(), amount);
        String formatted = EconomyManager.getFormattedNumber(amount);
        MessagesManager.sendMessage(player, Component.text("§aVous avez reçu §e" + formatted), Prefix.OPENMC, MessageType.SUCCESS, true);
    }
}
