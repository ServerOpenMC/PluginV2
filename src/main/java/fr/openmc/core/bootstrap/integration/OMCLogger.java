package fr.openmc.core.bootstrap.integration;

import fr.openmc.core.OMCPlugin;
import lombok.Setter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

/**
 * Centralise les Loggers nécessaires pour le bootstrap et le runtime.
 * org.slf4j.Logger est prioritaire sur le net.kyori.adventure.text.logger.slf4j.ComponentLogger.
 */
public class OMCLogger {
    @Setter
    private static ComponentLogger bootstrapLogger = null;
    @Setter
    private static Logger runtimeLogger = null;

    /**
     * Affiche un message de level INFO
     * @param message le message
     * @param args les arguments
     */
    public static void info(String message, Object... args) {
        logInfo(message, args);
    }

    /**
     * Affiche un message de level WARN
     * @param message le message
     * @param args les arguments
     */
    public static void warn(String message, Object... args) {
        logWarn(message, args);
    }

    /**
     * Affiche un message de level ERROR
     * @param message le message
     * @param args les arguments
     */
    public static void error(String message, Object... args) {
        logError(message, args);
    }

    /**
     * Affiche un message de level DEBUG
     * @param message le message
     * @param args les arguments
     */
    public static void debug(String message, Object... args) {
        logDebug(message, args);
    }

    /**
     * Affiche un message formatté de succès (avec l'icone ✔)
     * @param message le message
     * @param args les arguments
     */
    public static void successFormatted(String message, Object... args) {
        info("\u001B[32m✔ " + message + "\u001B[0m", args);
    }

    /**
     * Affiche un message formatté d'info (avec l'icone 🛈)
     * @param message le message
     * @param args les arguments
     */
    public static void infoFormatted(String message, Object... args) {
        info("\u001B[36m\uD83D\uDEC8 " + message + "\u001B[0m", args);
    }

    /**
     * Affiche un message formatté de warn (avec l'icone ⚠)
     * @param message le message
     * @param args les arguments
     */
    public static void warnFormatted(String message, Object... args) {
        info("\u001B[33m⚠ " + message + "\u001B[0m", args);
    }

    /**
     * Affiche un message formatté d'erreur (avec l'icone ✘)
     * @param message le message
     * @param args les arguments
     */
    public static void errorFormatted(String message, Object... args) {
        info("\u001B[31m✘ " + message + "\u001B[0m", args);
    }

    /**
     * Affiche la bannière de demarrage et l'état des dependances.
     */
    public static void logLoadMessage(OMCPlugin plugin) {
        String pluginVersion = plugin.getPluginMeta().getVersion();
        String javaVersion = System.getProperty("java.version");
        String server = Bukkit.getName() + " " + Bukkit.getVersion();

        info("\u001B[1;35m   ____    _____   ______   _   _   __  __   _____       \u001B[0;90mOpenMC {}\u001B[0m", pluginVersion);
        info("\u001B[1;35m  / __ \\  |  __ \\ |  ____| | \\ | | |  \\/  | / ____|      \u001B[0;90m{}\u001B[0m", server);
        info("\u001B[1;35m | |  | | | |__) || |__    |  \\| | | \\  / || |           \u001B[0;90mJava {}\u001B[0m", javaVersion);
        info("\u001B[1;35m | |  | | |  ___/ |  __|   | . ` | | |\\/| || |          \u001B[0m");
        info("\u001B[1;35m | |__| | | |     | |____  | |\\  | | |  | || |____      \u001B[0m");
        info("\u001B[1;35m  \\____/  |_|     |______| |_| \\_| |_|  |_| \\_____|   \u001B[0m");
        info("");

        for (String requiredPlugins : plugin.getPluginMeta().getPluginDependencies()) {
            logPluginStatus(requiredPlugins, false);
        }

        for (String optionalPlugins : plugin.getPluginMeta().getPluginSoftDependencies()) {
            logPluginStatus(optionalPlugins, true);
        }
    }

    /**
     * Log l'état d'une dépendance (requise ou optionnelle).
     *
     * @param name Nom du plugin dépendance
     * @param optional True si la dépendance est optionnelle
     */
    private static void logPluginStatus(String name, boolean optional) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        boolean enabled = plugin != null && plugin.isEnabled();

        String icon = enabled ? "✔" : "✘";
        String color = enabled ? "\u001B[32m" : "\u001B[31m";
        String version = enabled ? " v" + plugin.getPluginMeta().getVersion() : "";
        String label = optional ? " (facultatif)" : "";

        info("  {}{} {}{}{}\u001B[0m", color, icon, name, version, label);
    }

    private static void logInfo(String message, Object... args) {
        if (runtimeLogger != null) {
            runtimeLogger.info(message, args);
        } else if (bootstrapLogger != null) {
            bootstrapLogger.info(message, args);
        } else {
            throw new IllegalStateException("No logger available");
        }
    }

    private static void logError(String message, Object... args) {
        if (runtimeLogger != null) {
            runtimeLogger.error(message, args);
        } else if (bootstrapLogger != null) {
            bootstrapLogger.error(message, args);
        }
    }

    private static void logWarn(String message, Object... args) {
        if (runtimeLogger != null) {
            runtimeLogger.warn(message, args);
        } else if (bootstrapLogger != null) {
            bootstrapLogger.warn(message, args);
        }
    }

    private static void logDebug(String message, Object... args) {
        if (runtimeLogger != null) {
            runtimeLogger.debug(message, args);
        } else if (bootstrapLogger != null) {
            bootstrapLogger.debug(message, args);
        }
    }
}
