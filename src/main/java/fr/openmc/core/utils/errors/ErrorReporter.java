package fr.openmc.core.utils.errors;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.DiscordWebhook;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ErrorReporter {
    private final PrintStream originalErr;
    private static final Set<String> reportedErrors = new HashSet<>();


    public ErrorReporter() {
        originalErr = System.err;

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> handleException(throwable, "Uncaught in thread " + thread.getName()));

        System.setErr(new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                char c = (char) b;
                buffer.append(c);

                if (c == '\n') {
                    String line = buffer.toString().trim();
                    buffer.setLength(0);

                    if (line.contains("Exception") || line.contains("Error")) {
                        handleException(new RuntimeException(line), "System.err capture");
                    }

                    originalErr.println(line);
                }
            }
        }, true));
    }

    public static void handleException(Throwable throwable, String context) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String pluginVersion = OMCPlugin.getInstance().getPluginMeta().getVersion();
        String mcVersion = Bukkit.getBukkitVersion();

        String signature = throwable.getClass().getName();
        if (throwable.getStackTrace().length > 0) {
            signature += "@" + throwable.getStackTrace()[0].toString();
        }

        boolean alreadyReported = !reportedErrors.add(signature);

        StringBuilder stackSnippet = new StringBuilder();
        int lines = Math.min(5, throwable.getStackTrace().length);
        for (int i = 0; i < lines; i++) {
            stackSnippet.append("\n   at ").append(throwable.getStackTrace()[i]);
        }

        String prefix = alreadyReported ? "⚠️" : "🚨";
        List<String> notifIds = OMCPlugin.getInstance().getConfig().getStringList("error.notif");
        String webhookUrl = OMCPlugin.getInstance().getConfig().getString("error.webhook");

        String mention;
        if (alreadyReported || notifIds.isEmpty()) {
            mention = "";
        } else {
            mention = notifIds.stream()
                    .map(id -> "<@" + id + ">")
                    .collect(Collectors.joining(" "));
        }

        String discordMsg = prefix + " **Erreur interceptée !** " + mention + "\n"
                + "Date: `" + timestamp + "`\n"
                + "Plugin: `" + OMCPlugin.getInstance().getName() + " " + pluginVersion + "`\n"
                + "MC: `" + mcVersion + "`\n"
                + "Context: `" + context + "`\n"
                + "```" + throwable.getClass().getSimpleName() + ": "
                + (throwable.getMessage() != null ? throwable.getMessage() : "no message")
                + stackSnippet + "\n```";

        try {
            DiscordWebhook.sendMessage(webhookUrl, discordMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
