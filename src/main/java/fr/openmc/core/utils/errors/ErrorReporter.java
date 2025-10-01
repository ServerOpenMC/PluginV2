package fr.openmc.core.utils.errors;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.DiscordWebhook;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ErrorReporter {
    private final PrintStream originalErr;
    private static final Set<String> reportedErrors = new HashSet<>();
    private static List<String> notifIds;
    private static String webhookUrl;

    public ErrorReporter() {
        originalErr = System.err;

        if (!OMCPlugin.getInstance().getConfig().isConfigurationSection("error")) {
            OMCPlugin.getInstance().getLogger().info("\u001B[31m✘ ErrorHandler désactivé (pas de section error)\u001B[0m");
            return;
        }

        webhookUrl = OMCPlugin.getInstance().getConfig().getString("error.webhook", "").trim();
        notifIds = OMCPlugin.getInstance().getConfig().getStringList("error.notif");

        if (webhookUrl.isBlank()) {
            OMCPlugin.getInstance().getLogger().info("\u001B[31m✘ ErrorHandler désactivé (pas de webhook)\u001B[0m");
            return;
        }

        OMCPlugin.getInstance().getLogger().info("\u001B[32m✔ ErrorHandler activé\u001B[0m");

        System.setErr(new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();
            private final List<String> currentError = new ArrayList<>();
            private boolean capturing = false;

            @Override
            public void write(int b) {
                char c = (char) b;
                buffer.append(c);

                if (c == '\n') {
                    String line = buffer.toString().trim();
                    buffer.setLength(0);

                    originalErr.println(line);

                    String cleanLine = line.replaceFirst("^\\[.*?STDERR\\]\\s*", "");

                    if (cleanLine.contains("Exception") || cleanLine.contains("Error")) {
                        capturing = true;
                        currentError.clear();
                        currentError.add(cleanLine);
                        return;
                    }

                    if (capturing) {
                        if (cleanLine.startsWith("at ") || cleanLine.startsWith("\tat ")) {
                            currentError.add(cleanLine);
                            if (currentError.size() >= 4) {
                                handleException();
                                capturing = false;

                                if (cleanLine.contains("Exception") || cleanLine.contains("Error")) {
                                    capturing = true;
                                    currentError.clear();
                                    currentError.add(cleanLine);
                                }
                            }
                        } else {
                            handleException();
                            capturing = false;

                            if (cleanLine.contains("Exception") || cleanLine.contains("Error")) {
                                capturing = true;
                                currentError.clear();
                                currentError.add(cleanLine);
                            }
                        }
                    }
                }
            }

            private void handleException() {
                if (currentError.isEmpty()) return;

                String firstLine = currentError.get(0);
                String firstStack = currentError.size() > 1 ? currentError.get(1) : "";
                String signature = firstLine + "|" + firstStack;

                boolean alreadyReported = !reportedErrors.add(signature);

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String pluginVersion = OMCPlugin.getInstance().getPluginMeta().getVersion();
                String mcVersion = Bukkit.getBukkitVersion();

                String prefix = alreadyReported ? "⚠️" : "🚨";
                String mention = (alreadyReported || notifIds.isEmpty())
                        ? ""
                        : notifIds.stream().map(id -> "<@" + id + ">").collect(Collectors.joining(" "));

                String discordMsg = prefix + " **Erreur interceptée !** " + mention + "\n"
                        + "Date: `" + timestamp + "`\n"
                        + "Plugin: `" + OMCPlugin.getInstance().getName() + " " + pluginVersion + "`\n"
                        + "MC: `" + mcVersion + "`\n"
                        + "```\n" + String.join("\n", currentError) + "\n```";

                try {
                    DiscordWebhook.sendMessage(webhookUrl, discordMsg);
                } catch (Exception e) {
                    OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to send Discord Webhook", e);
                }
                currentError.clear();
            }

        }, true));
    }
}
