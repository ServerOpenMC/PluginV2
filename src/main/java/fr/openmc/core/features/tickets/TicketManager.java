package fr.openmc.core.features.tickets;

import com.google.gson.*;
import fr.openmc.core.OMCPlugin;
import lombok.Getter;

import java.io.*;
import java.util.*;

@Getter
public class TicketManager {

    // INFO: ticks -> seconds : 20 ticks = 1 second -> ticks / 20 = seconds
    // INFO: seconds -> ticks : 1 second = 20 ticks -> seconds * 20 = ticks

    public static TicketManager instance;
    public final List<PlayerStats> timePlayed = new ArrayList<>();

    private final Gson gson = new Gson();
    private File statsDirectory;

    public TicketManager() { }

    public static TicketManager getInstance() {
        if (instance == null) {
            instance = new TicketManager();
        }
        return instance;
    }

    public void loadPlayerStats(File statsDirectory) {
        this.statsDirectory = statsDirectory;

        if (!statsDirectory.exists() || ! statsDirectory.isDirectory()) {
            OMCPlugin.getInstance().getSLF4JLogger().info("Stats directory does not exist or is not a directory.");
            return;
        }

        File[] files = statsDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            OMCPlugin.getInstance().getSLF4JLogger().info("No stats files found.");
            return;
        }

        for (File statFile : files) {
            loadPlayerStat(statFile);
        }
    }

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

                            PlayerStats playerStats = new PlayerStats(playerUUID, playTimeSeconds, hasTicketGiven);
                            timePlayed.add(playerStats);
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Invalid UUID in filename: {}", statFile.getName(), e);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading stats from file: {}", statFile.getName(), e);
        }
    }

    public int getTotalPlayTime() {
        return timePlayed.stream().mapToInt(PlayerStats::getTimePlayed).sum();
    }

    public int getPlayTimeFromUUID(UUID uuid) {
        return timePlayed.stream()
                .filter(stats -> stats.getUniqueID().equals(uuid))
                .mapToInt(PlayerStats::getTimePlayed)
                .findFirst()
                .orElse(0);
    }

    public boolean ticketGiven(UUID uuid) {
        return timePlayed.stream().anyMatch(stats -> stats.getUniqueID().equals(uuid) && stats.isTicketGiven());
    }

    public void setTicketGiven(UUID uuid, boolean given) {
        for (PlayerStats stats : timePlayed) {
            if (stats.getUniqueID().equals(uuid)) {
                stats.setTicketGiven(given);
                break;
            }
        }

        updatePlayerJsonFile(uuid, given);
    }

    private void updatePlayerJsonFile(UUID uuid, boolean given) {
        File playerFile = new File(statsDirectory, uuid.toString() + ".json");
        if (!playerFile.exists()) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Player stats file not found for UUID: {}", uuid);
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

            custom.addProperty("openmc:ticket_given", given);

            try (FileWriter writer = new FileWriter(playerFile)) {
                gson.toJson(jsonObject, writer);
            }

        } catch (IOException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error updating stats file for UUID: {}", uuid, e);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Unexpected error updating stats file for UUID: {}", uuid, e);
        }
    }

    public boolean giveTicket(UUID uuid) {
        for (PlayerStats stats : timePlayed) {
            if (stats.getUniqueID().equals(uuid)) {
                if (!stats.isTicketGiven()) {
                    setTicketGiven(uuid, true);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

}