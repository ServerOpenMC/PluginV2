package fr.openmc.core.features.tickets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.displays.holograms.Hologram;
import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Credit(developers = {"Axeno"}, graphist = {"Tfloa"})
@Getter
public class TicketManager extends Feature implements HasListeners {

    private final HologramLoader hologramLoader;

    public int hoursPerTicket = 8;
    public final List<PlayerStats> timePlayed = new ArrayList<>();

    private final Gson gson = new Gson();
    @Setter
    private File statsDirectory;

    private final Map<Location, String> machineHolograms = new ConcurrentHashMap<>();
    private int hologramCounter = 0;

    public TicketManager(File statsDirectory) {
        this.setStatsDirectory(statsDirectory);
        this.hologramLoader = OMCRegistry.FEATURES.HOLOGRAM_LOADER.get();
    }

    @Override
    public void init() {
        this.loadPlayerStats(statsDirectory);
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(() -> new TicketListener(this));
    }

    /**
     * Load player statistics from JSON files in the specified directory.
     *
     * @param statsDirectory The {@link File} directory containing player stats JSON files.
     */
    public void loadPlayerStats(File statsDirectory) {
        if (!statsDirectory.exists() || !statsDirectory.isDirectory()) {
            OMCLogger.info("Stats directory does not exist or is not a directory.");
            return;
        }

        File[] files = statsDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            OMCLogger.info("No stats files found.");
            return;
        }

        for (File statFile : files) {
            loadPlayerStat(statFile);
        }
    }


    /**
     * Load a single player's statistics from a JSON file.
     *
     * @param statFile The {@link File} containing the player's stats.
     */
    private void loadPlayerStat(File statFile) {
        try {
            String fileName = statFile.getName();
            String uuidString = fileName.substring(0, fileName.lastIndexOf('.'));
            UUID playerUUID = UUID.fromString(uuidString);

            try (FileReader reader = new FileReader(statFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                if (jsonObject.has("stats")) {
                    JsonObject stats = jsonObject.getAsJsonObject("stats");
                    if (stats.has("minecraft:custom")) {
                        JsonObject custom = stats.getAsJsonObject("minecraft:custom");
                        if (custom.has("minecraft:play_time")) {
                            int playTimeTicks = 0;
                            if (custom.has("minecraft:play_time")) {
                                playTimeTicks = custom.get("minecraft:play_time").getAsInt();
                            }
                            int playTimeSeconds = playTimeTicks / 20;

                            boolean hasTicketGiven = false;
                            if (custom.has("openmc:ticket_given")) {
                                hasTicketGiven = custom.get("openmc:ticket_given").getAsBoolean();
                            }

                            int ticketsRemaining = 0;
                            if (custom.has("openmc:tickets_remaining")) {
                                ticketsRemaining = custom.get("openmc:tickets_remaining").getAsInt();
                            }

                            Map<String, Integer> maxItemsGiven = new HashMap<>();
                            if (custom.has("openmc:max_items_given")) {
                                JsonObject itemsGiven = custom.getAsJsonObject("openmc:max_items_given");
                                Map<String, Integer> givenMap = new HashMap<>();
                                for (Map.Entry<String, JsonElement> entry : itemsGiven.entrySet()) {
                                    givenMap.put(entry.getKey(), entry.getValue().getAsInt());
                                }
                                maxItemsGiven = givenMap;
                            }

                            PlayerStats playerStats = new PlayerStats(playerUUID, playTimeSeconds, ticketsRemaining, hasTicketGiven, maxItemsGiven);
                            timePlayed.add(playerStats);
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            OMCLogger.warn("Invalid UUID in filename: {}", statFile.getName(), e);
        } catch (Exception e) {
            OMCLogger.error("Error loading stats from file: {}", statFile.getName(), e);
        }
    }

    /**
     * Get the PlayerStats for a given UUID.
     *
     * @param uuid The {@link UUID} of the player.
     * @return The {@link PlayerStats} if found, otherwise null.
     */
    public PlayerStats getPlayerStats(UUID uuid) {
        return timePlayed.stream()
                .filter(stats -> stats.getUniqueID().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the playtime in seconds for a given UUID.
     *
     * @param uuid The {@link UUID} of the player.
     * @return The playtime in seconds, or 0 if not found.
     */
    public int getPlayTimeFromUUID(UUID uuid) {
        return timePlayed.stream()
                .filter(stats -> stats.getUniqueID().equals(uuid))
                .mapToInt(PlayerStats::getTimePlayed)
                .findFirst()
                .orElse(0);
    }

    /**
     * Set the ticket information for a player and update their JSON stats file.
     *
     * @param uuid         The {@link UUID} of the player.
     * @param ticketToGive The number of tickets to set.
     * @param given        Whether the ticket has been given.
     */
    public void setTicketGiven(UUID uuid, int ticketToGive, boolean given) {
        for (PlayerStats stats : timePlayed) {
            if (stats.getUniqueID().equals(uuid)) {
                stats.setTicketRemaining(ticketToGive);
                stats.setTicketGiven(given);
                break;
            }
        }

        updatePlayerJsonFile(uuid, ticketToGive, given);
    }

    /**
     * Update the player's JSON stats file with the new ticket information.
     *
     * @param uuid         The {@link UUID} of the player.
     * @param ticketToGive The number of tickets to set.
     * @param given        Whether the ticket has been given.
     */
    private void updatePlayerJsonFile(UUID uuid, int ticketToGive, boolean given) {
        File playerFile = new File(statsDirectory, uuid.toString() + ".json");
        if (!playerFile.exists()) {
            OMCLogger.warn("Player stats file not found for UUID: {}", uuid);
            return;
        }

        try {
            JsonObject jsonObject;

            try (FileReader reader = new FileReader(playerFile)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            }

            if (!jsonObject.has("stats")) {
                jsonObject.add("stats", new JsonObject());
            }

            JsonObject stats = jsonObject.getAsJsonObject("stats");
            if (!stats.has("minecraft:custom")) {
                stats.add("minecraft:custom", new JsonObject());
            }

            JsonObject custom = stats.getAsJsonObject("minecraft:custom");

            custom.addProperty("openmc:tickets_remaining", ticketToGive);
            custom.addProperty("openmc:ticket_given", given);

            JsonObject itemsGiven = new JsonObject();
            PlayerStats ps = getPlayerStats(uuid);
            if (ps != null && ps.getMaxItemsGiven() != null) {
                for (Map.Entry<String, Integer> entry : ps.getMaxItemsGiven().entrySet()) {
                    itemsGiven.addProperty(entry.getKey(), entry.getValue());
                }
            }
            custom.add("openmc:max_items_given", itemsGiven);


            try (FileWriter writer = new FileWriter(playerFile)) {
                gson.toJson(jsonObject, writer);
            }

        } catch (IOException e) {
            OMCLogger.error("Error updating stats file for UUID: {}", uuid, e);
        } catch (Exception e) {
            OMCLogger.error("Unexpected error updating stats file for UUID: {}", uuid, e);
        }
    }

    /**
     * Attempt to use a ticket for the player with the given UUID.
     *
     * @param uuid The UUID of the player.
     * @return true if a ticket was used, false if no tickets are remaining or player not found.
     */
    public boolean useTicket(UUID uuid) {
        for (PlayerStats stats : timePlayed) {
            if (!stats.getUniqueID().equals(uuid)) continue;
            if (stats.getTicketRemaining() <= 0) return false;

            stats.setTicketRemaining(stats.getTicketRemaining() - 1);
            updatePlayerJsonFile(uuid, stats.getTicketRemaining(), stats.isTicketGiven());
            return true;
        }

        return false;
    }

    /**
     * Calculate and give tickets based on playtime if not already given.
     *
     * @param uuid The UUID of the player.
     * @return The number of tickets given, or 0 if already given or player not found.
     */
    public int giveTicket(UUID uuid) {
        for (PlayerStats stats : timePlayed) {
            if (!stats.getUniqueID().equals(uuid)) continue;
            if (stats.isTicketGiven()) return 0;

            int playtime = getPlayTimeFromUUID(uuid);
            float secondsPerTicket = hoursPerTicket * 3600;
            float ticketsToGiveF = playtime / secondsPerTicket;

            int ticketsToGive = (int) Math.ceil(ticketsToGiveF);

            stats.setTicketRemaining(ticketsToGive);
            setTicketGiven(uuid, ticketsToGive, true);
            return ticketsToGive;
        }

        return 0;
    }

    public void createMachineHologram(Location machineLocation) {
        if (machineHolograms.containsKey(machineLocation)) return;

        String hologramName = "ball_machine_" + (++hologramCounter);

        Location hologramLocation = machineLocation.clone().add(0, 2.3, 0);

        Hologram hologram = new Hologram(hologramName);
        hologram.setLocation(hologramLocation.getX(), hologramLocation.getY(), hologramLocation.getZ());
        hologram.setScale(0.7f);
        hologram.setLines(
                TranslationManager.translation("feature.tickets.machine.hologram_line1"),
                TranslationManager.translation("feature.tickets.machine.hologram_line2"),
                TranslationManager.translation("feature.tickets.machine.hologram_line3")
        );

        hologramLoader.registerHolograms(hologram);

        machineHolograms.put(machineLocation, hologramName);
    }

    public void removeMachineHologram(Location machineLocation) {
        String hologramName = machineHolograms.remove(machineLocation);
        var hologramInfo = hologramName != null ? hologramLoader.displays.get(hologramName) : null;
        if (hologramInfo == null) return;

        hologramInfo.display().remove();
        hologramLoader.displays.remove(hologramName);

        if (hologramInfo.file().exists()) {
            hologramInfo.file().delete();
        }
    }
}