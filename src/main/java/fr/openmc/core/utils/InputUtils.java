package fr.openmc.core.utils;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import org.bukkit.Bukkit;

public class InputUtils {

    public static final int MAX_LENGTH = 100;
    public static final int MAX_LENGTH_CITY = 24;
    public static final int MAX_LENGTH_PLAYERNAME = 16;

    private InputUtils() {
        // for Sonar
    }

    public static boolean isInputMoney(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        String regex = "^(\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)([kKmM]?)$";

        if (!input.matches(regex)) {
            return false;
        }

        String numericPart = input.replaceAll("[kKmM]", "");
        try {
            double value = Double.parseDouble(numericPart);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Convertit une chaîne représentant une somme d'argent en sa valeur numérique.
     *
     * @param input Chaîne représentant une somme d'argent (e.g., "3m", "2.5m", "200k", "500").
     * @return La valeur numérique correspondant à l'entrée, ou -1 si l'entrée est invalide.
     */
    public static double convertToMoneyValue(String input) {
        if (!isInputMoney(input)) {
            return -1;
        }

        char suffix = input.charAt(input.length() - 1);
        String numericPart = input.replaceAll("[kKmM]", "");

        try {
            double value = Double.parseDouble(numericPart);

            if (Character.isLetter(suffix)) {
                char lowerChar = Character.toLowerCase(suffix);
                if (lowerChar == 'k') {
                    return value * 1_000;
                } else if (lowerChar == 'm') {
                    return value * 1_000_000;
                }
            }

            return value;

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Check if input was for a name
     * @param input Input of Player
     * @return Boolean
     */
    public static boolean isInputCityName(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }

        if (input.length() > MAX_LENGTH_CITY) {
            return false;
        }

        //TODO: Attendre la PR des alliances.
        for (City city : CityManager.getCities()) {
            String testCityName = city.getName();
            if (testCityName.equalsIgnoreCase(input)) {
                return false;
            }
        }

        return input.matches("[a-zA-Z0-9\\s]+");
    }

    /**
     * Check if input was for a player
     * @param input Input of Player
     * @return Boolean
     */
    public static boolean isInputPlayer(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        if (input.length() > MAX_LENGTH_PLAYERNAME) {
            return false;
        }

        return Bukkit.getPlayer(input) != null;
    }
}
