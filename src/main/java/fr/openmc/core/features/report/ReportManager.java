package fr.openmc.core.features.report;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.utils.DiscordWebhookUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ReportManager extends Feature implements HasCommands {
    private static String webhookUrl;

    @Override
    public void init() {
        if (!OMCPlugin.getInstance().getConfig().isConfigurationSection("report")) {
            OMCLogger.errorFormatted("ReportManager désactivé (pas de section report)");
            return;
        }

        webhookUrl = OMCPlugin.getInstance().getConfig().getString("report.webhook", "").trim();

        if (webhookUrl.isBlank()) {
            OMCLogger.warnFormatted("ReportManager désactivé (pas de webhook)");
            return;
        }

        OMCLogger.successFormatted("ReportManager activé");
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new ReportCommand()
        );
    }

    public static void sendReport(Player sender, OfflinePlayer target, String reason) {
        String discordMsg = "**Report de " + sender.getName() + " en vers " + target.getName() + " !** \n"
                + "Date: `" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "`\n"
                + "Description du report: `" + reason + "`\n";

        try {
            DiscordWebhookUtils.sendMessage(webhookUrl, discordMsg);
        } catch (Exception e) {
            OMCLogger.error("Échec lors de l'envoie du webhook {}", e);
        }
    }
}
