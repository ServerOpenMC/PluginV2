package fr.openmc.core.utils.serializer;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {

    private static final int MAX_LENGTH = 255; // Fit in TINYTEXT
    private static final int VALUES_LENGTH = 50; //251 can't be divided by 5 (-4 for separator)
    private static final String SEPARATOR = ";";

    /**
     * Serializes location coordinates without rotation
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return Serialized location string in format "x;y;z;0;0"
     * @throws IllegalArgumentException if the resulting string exceeds max length
     */
    public static String serialize(double x, double y, double z) {
        return serialize(x, y, z, 0, 0);
    }

    /**
     * Serializes location coordinates with rotation
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param yaw The yaw rotation
     * @param pitch The pitch rotation
     * @return Serialized location string in format "x;y;z;yaw;pitch"
     * @throws IllegalArgumentException if the resulting string exceeds max length
     */
    public static String serialize(double x, double y, double z, double yaw, double pitch) {
        // Format each value with proper precision
        String serialized = String.format("%.2f;%.2f;%.2f;%.2f;%.2f",
                x, y, z, yaw, pitch);

        if (serialized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Serialized location exceeds maximum length of " + MAX_LENGTH + " characters");
        }
        
        // Verify each value stays within the size limit
        String[] values = serialized.split(SEPARATOR);
        for (String value : values) {
            if (value.length() > VALUES_LENGTH) {
                throw new IllegalArgumentException(
                        "Value '" + value + "' exceeds maximum length of " + VALUES_LENGTH + " characters");
            }
        }

        return serialized;
    }

    /**
     * Deserializes a location string back into a Location object
     * @param data The serialized location string
     * @param world The world to create the location in
     * @return A new Location object
     * @throws IllegalArgumentException if the data format is invalid
     */
    public static Location deserialize(String data, World world) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        String[] parts = data.split(SEPARATOR);
        if (parts.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid location format. Expected 5 values but got " + parts.length);
        }

        try {
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            float yaw = Float.parseFloat(parts[3]);
            float pitch = Float.parseFloat(parts[4]);

            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in location data: " + data, e);
        }
    }

    /**
     * Serialize a {@link Location} to a {@link String}
     * @param location Location that will get serialized
     * @return A serialized location as String
     */
    public String serialize(Location location) {
        return serialize(
                location.x(),
                location.y(),
                location.z(),
                location.getYaw(),
                location.getPitch()
        );
    }
}