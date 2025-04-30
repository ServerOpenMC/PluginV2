package fr.openmc.core.utils;

import org.bukkit.Bukkit;

public class InputUtils {

    private InputUtils() {
        // for Sonar
    }

    /**
     * Check if input was for money
     * @param input Input of Player
     * @return Boolean
     */
    public static boolean isInputMoney(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String regex = "^(\\d+)([kKmM]?)$";

        if (!input.matches(regex)) {
            return false;
        }

        char lastChar = input.charAt(input.length() - 1);
        if (Character.isLetter(lastChar)) {
            char lowerChar = Character.toLowerCase(lastChar);
            return lowerChar == 'k' || lowerChar == 'm' ||lowerChar == 'K' || lowerChar == 'M';
        }

        try {
            long value = Long.parseLong(input);
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

        String removeKM = input.replaceAll("[kKmM]", "");
        char suffix = input.charAt(input.length() - 1);

        try {
            double value = Double.parseDouble(removeKM);

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

        if (input.length() > 24) {
            return false;
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

        return Bukkit.getPlayer(input) != null;
    }
}
