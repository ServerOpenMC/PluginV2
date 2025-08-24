package fr.openmc.core.features.tickets;

import com.google.gson.*;
import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

@Getter
public class TicketManager {

    public static int hoursPerTicket = 8;
    public static final List<PlayerStats> timePlayed = new ArrayList<>();

    private static final Gson gson = new Gson();
    @Setter private static File statsDirectory;

    public TicketManager() { }

    /**
     * Load player statistics from JSON files in the specified directory.
     *
     * @param statsDirectory The {@link File} directory containing player stats JSON files.
     */
    public static void loadPlayerStats(File statsDirectory) {
        TicketManager.setStatsDirectory(statsDirectory);

        if (!statsDirectory.exists() || !statsDirectory.isDirectory()) {
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


    /**
     * Load a single player's statistics from a JSON file.
     *
     * @param statFile The {@link File} containing the player's stats.
     */
    private static void loadPlayerStat(File statFile) {
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
            OMCPlugin.getInstance().getSLF4JLogger().warn("Invalid UUID in filename: {}", statFile.getName(), e);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Error loading stats from file: {}", statFile.getName(), e);
        }
    }

    /**
     * Get the PlayerStats for a given UUID.
     *
     * @param uuid The {@link UUID} of the player.
     * @return The {@link PlayerStats} if found, otherwise null.
     */
    public static PlayerStats getPlayerStats(UUID uuid) {
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
    public static int getPlayTimeFromUUID(UUID uuid) {
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
    public static void setTicketGiven(UUID uuid, int ticketToGive, boolean given) {
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
    private static void updatePlayerJsonFile(UUID uuid, int ticketToGive, boolean given) {
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
            OMCPlugin.getInstance().getSLF4JLogger().error("Error updating stats file for UUID: {}", uuid, e);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Unexpected error updating stats file for UUID: {}", uuid, e);
        }
    }

    /**
     * Attempt to use a ticket for the player with the given UUID.
     *
     * @param uuid The UUID of the player.
     * @return true if a ticket was used, false if no tickets are remaining or player not found.
     */
    public static boolean useTicket(UUID uuid) {
        for (PlayerStats stats : timePlayed) {
            if (stats.getUniqueID().equals(uuid)) {
                if (stats.getTicketRemaining() > 0) {
                    stats.setTicketRemaining(stats.getTicketRemaining() - 1);
                    updatePlayerJsonFile(uuid, stats.getTicketRemaining(), stats.isTicketGiven());
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Calculate and give tickets based on playtime if not already given.
     *
     * @param uuid The UUID of the player.
     * @return The number of tickets given, or 0 if already given or player not found.
     */
    public static int giveTicket(UUID uuid) {
        for (PlayerStats stats : timePlayed) {
            if (stats.getUniqueID().equals(uuid)) {
                if (!stats.isTicketGiven()) {
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
        }
        return 0;
    }
}