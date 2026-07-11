package fr.openmc.core.features.dimopener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.dimopener.commands.DimensionCommands;
import fr.openmc.core.features.dimopener.data.DimensionData;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DimensionOpenerManager extends Feature implements HasCommands, LoadAfterItemsAdder {

    private static final long TICK_PERIOD_TICKS = 20L * 60; // 1 minutes

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, DimensionData> dimensions = new ConcurrentHashMap<>();
    private static final Map<String, DimensionProgress> progressMap = new ConcurrentHashMap<>();

    private static File dimensionsFolder;
    private static File progressFile;

    private BukkitTask tickTask;

    @Override
    protected void init() {
        dimensionsFolder = new File(OMCPlugin.getInstance().getDataFolder(), "data/dimensions");
        if (!dimensionsFolder.exists()) dimensionsFolder.mkdir();

        progressFile = new File(OMCPlugin.getInstance().getDataFolder(), "data/dimensions_progress.json");

        loadDimensions();
        loadProgress();
        startTicking();
    }

    @Override
    protected void save() {
        saveProgress();
        if (tickTask != null) tickTask.cancel();
    }

    public static void loadDimensions() {
        dimensions.clear();

        File[] files = dimensionsFolder.listFiles((_, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            String id = file.getName().replace(".json", "").toLowerCase();
            try (FileReader reader = new FileReader(file)) {
                DimensionData data = GSON.fromJson(reader, DimensionData.class);
                if (data == null) {
                    OMCLogger.error("Could not load dimension data from file: " + file.getName());
                    continue;
                }
                data.setId(id);
                dimensions.put(id, data);
                progressMap.computeIfAbsent(id, DimensionProgress::new);
            } catch (IOException e) {
                OMCLogger.error("Failed to load dimensions file: " + file.getName());
            }
        }

        OMCLogger.info("{} dimension(s) chargée", dimensions.size());
    }

    public static Collection<DimensionData> getDimensions() {
        return dimensions.values();
    }

    public static List<DimensionData> getEnabledDimensions() {
        return dimensions.values().stream()
                .filter(DimensionData::isEnabled)
                .collect(Collectors.toList());
    }

    public static DimensionData getDimension(String id) {
        return dimensions.get(id.toLowerCase());
    }

    public static DimensionProgress getProgress(String id) {
        return progressMap.get(id.toLowerCase());
    }

    public static IStepDimension getCurrentStep(String id) {
        DimensionData dim = dimensions.get(id.toLowerCase());
        DimensionProgress progress = progressMap.get(id.toLowerCase());
        if (dim == null || progress == null) return null;

        List<IStepDimension> steps = dim.getDimensionsStep();
        if (progress.getCurrentStepIndex() >= steps.size()) return null;

        return steps.get(progress.getCurrentStepIndex());
    }

    public enum ContributeResult {
        SUCCESS,
        STEP_COMPLETED,
        DIMENSION_ALREADY_OPENED,
        WRONG_STEP_STATE,
        INVALID_STEP
    }

    public static ContributeResult contributeItems(Player player, String dimensionId, int amount) {
        return contribute(player, dimensionId, amount, IStepDimension.Type.ITEMS);
    }

    public static ContributeResult contributeMoney(Player player, String dimensionId, double amount) {
        if (amount <= 0) return ContributeResult.WRONG_STEP_STATE;
        if (!EconomyManager.withdrawBalance(player.getUniqueId(), amount, "Dimension Opener : " + dimensionId)) {
            return ContributeResult.WRONG_STEP_STATE;
        }
        return contribute(player, dimensionId, amount, IStepDimension.Type.MONEY);
    }

    private static ContributeResult contribute(Player player, String dimensionId, double amount, IStepDimension.Type expectedType) {
        String id = dimensionId.toLowerCase();
        DimensionProgress progress = progressMap.get(id);
        IStepDimension step = getCurrentStep(id);

        if (progress == null || step == null) return ContributeResult.INVALID_STEP;
        if (step.getType() != expectedType) return ContributeResult.WRONG_STEP_STATE;

        if (progress.getState() != DimensionState.LOCKED
                && progress.getState() != DimensionState.STEP_IN_PROGRESS) {
            return ContributeResult.DIMENSION_ALREADY_OPENED;
        }

        progress.setState(DimensionState.STEP_IN_PROGRESS);
        progress.addAmount(player.getUniqueId(), amount);

        if (progress.getCurrentAmount() >= step.getRequired()) {
            onStepCompleted(id, progress);
            return ContributeResult.STEP_COMPLETED;
        }
        return ContributeResult.SUCCESS;
    }

    private static void onStepCompleted(String dimensionId, DimensionProgress progress) {
        DimensionData dim = dimensions.get(dimensionId);
        List<IStepDimension> steps = dim.getDimensionsStep();

        IStepDimension completedStep = steps.get(progress.getCurrentStepIndex());
        boolean isLastStep = progress.getCurrentStepIndex() >= steps.size() - 1;

        progress.setState(isLastStep ? DimensionState.ALL_STEPS_DONE : DimensionState.STEP_COOLDOWN);
        progress.startCooldown(completedStep.getCooldownSeconds() * 1000L);
        progress.resetStepProgress();

        Component msgComponent = isLastStep
                ? Component.text("Toutes les etape de", NamedTextColor.WHITE)
                .append(Component.text(dim.getName(), NamedTextColor.YELLOW))
                .append(Component.text("sont remplies ! Ouverture bientot.", NamedTextColor.WHITE))
                : Component.text("Une etape de", NamedTextColor.WHITE)
                .append(Component.text(dim.getName(), NamedTextColor.YELLOW))
                .append(Component.text("est terminee !", NamedTextColor.WHITE));

        MessagesManager.broadcastMessage(
                msgComponent,
                Prefix.DIMOPENER,
                MessageType.INFO
        );
    }

    private void startTicking() {
        tickTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), DimensionOpenerManager::tick, 20L, TICK_PERIOD_TICKS);
    }

    private static void tick() {
        boolean changed = false;
        for (DimensionProgress progress : progressMap.values()) {
            if (!progress.isCooldownOver()) continue;

            switch (progress.getState()) {
                case STEP_COOLDOWN -> {
                    advanceToNextStep(progress);
                    changed = true;
                }
                case ALL_STEPS_DONE -> {
                    openDimension(progress);
                    changed = true;
                }
                default -> {
                }
            }
        }
        if (changed) saveProgress();
    }

    private static void advanceToNextStep(DimensionProgress progress) {
        progress.setCurrentStepIndex(progress.getCurrentStepIndex() + 1);
        progress.setState(DimensionState.STEP_IN_PROGRESS);
        progress.clearCooldown();

        DimensionData dim = dimensions.get(progress.getDimensionId());
        if (dim != null) {
            MessagesManager.broadcastMessage(
                    Component.text("Une nouvelle étape est disponible pour", NamedTextColor.WHITE)
                            .appendSpace()
                            .append(Component.text(dim.getName(), NamedTextColor.YELLOW))
                            .appendSpace()
                            .append(Component.text("!", NamedTextColor.WHITE)),
                    Prefix.DIMOPENER,
                    MessageType.INFO
            );
        }
    }

    private static void openDimension(DimensionProgress progress) {
        progress.setState(DimensionState.OPENED);
        progress.clearCooldown();

        DimensionData dim = dimensions.get(progress.getDimensionId());
        if (dim != null) {
            MessagesManager.broadcastMessage(
                    Component.text("La dimension", NamedTextColor.WHITE)
                            .appendSpace()
                            .append(Component.text(dim.getName(), NamedTextColor.YELLOW))
                            .appendSpace()
                            .append(Component.text("est desormais ouverte !", NamedTextColor.WHITE)),
                    Prefix.DIMOPENER,
                    MessageType.SUCCESS
            );
            // TODO: ouverture dimension
        }
    }

    private static void loadProgress() {
        if (!progressFile.exists()) return;

        try (FileReader reader = new FileReader(progressFile)) {
            Type type = new TypeToken<Map<String, DimensionProgress>>() {
            }.getType();
            Map<String, DimensionProgress> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                progressMap.putAll(loaded);
            }
        } catch (IOException e) {
            OMCLogger.error("Erreur lors du chargement de la progression des dimensions : {}", e.getMessage(), e);
        }
    }

    private static void saveProgress() {
        try {
            if (!progressFile.getParentFile().exists()) progressFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(progressFile)) {
                GSON.toJson(progressMap, writer);
            }
        } catch (IOException e) {
            OMCLogger.error("Erreur lors de la sauvegarde de la progression des dimensions : {}", e.getMessage(), e);
        }
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new DimensionCommands()
        );
    }
}
