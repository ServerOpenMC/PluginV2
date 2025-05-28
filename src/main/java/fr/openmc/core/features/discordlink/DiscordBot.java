package fr.openmc.core.features.discordlink;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.UUID;
import java.util.logging.Level;

public class DiscordBot extends ListenerAdapter {
    private final OMCPlugin plugin;
    private JDA jda;
    @Getter
    private static DiscordBot instance;

    /**
     * Constructs a new DiscordBot instance
     * @param plugin The main plugin instance
     */
    public DiscordBot(OMCPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    /**
     * Starts the Discord bot asynchronously
     * Initializes JDA and registers event listeners
     */
    public void startBot() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                jda = JDABuilder.createDefault(plugin.getConfig().getString("bot-discord.bot-token"))
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                        .addEventListeners(this)
                        .build();

                jda.awaitReady();

                // Enregistrement des commandes slash
                registerCommands();

                plugin.getLogger().log(Level.INFO, "Bot Discord connecté avec succès!");
            } catch (InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, "Erreur lors du démarrage du bot Discord: " + e.getMessage());
            }
        });
    }

    /**
     * Registers slash commands for the Discord bot
     * Currently registers the /link command with a code parameter
     */
    private void registerCommands() {
        Guild guild = jda.getGuildById(plugin.getConfig().getString("bot-discord.guild-id"));
        if (guild != null) {
            guild.updateCommands().addCommands(
                    Commands.slash("link", "Lie ton compte Minecraft à Discord")
                            .addOption(OptionType.STRING, "code", "Code de vérification", true)
            ).queue();
        }
    }

    /**
     * Stops the Discord bot gracefully
     */
    public void stopBot() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    /**
     * Handles slash command interactions
     * @param event The slash command interaction event
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("link")) {
            handleLinkCommand(event);
        }
    }

    /**
     * Processes the /link command from Discord
     * @param event The slash command interaction event containing the verification code
     */
    private void handleLinkCommand(SlashCommandInteractionEvent event) {
        String code = event.getOption("code").getAsString();
        User user = event.getUser();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean success = DiscordLinkManager.getInstance().linkAccounts(code, user.getId()).join();

            if (success) {
                event.reply("✅ Ton compte Discord a bien été lié à ton compte Minecraft!")
                        .setEphemeral(true)
                        .queue();

                // Notifier le joueur en jeu
                String minecraftUUID = DiscordLinkManager.getInstance().getMinecraftUUID(user.getId()).join();
                Player player = Bukkit.getPlayer(UUID.fromString(minecraftUUID));

                if (player != null && player.isOnline()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        MessagesManager.sendMessage(player,
                                Component.text("Ton compte a été lié avec succès à Discord: ")
                                        .append(Component.text(user.getAsTag(), NamedTextColor.GREEN)),
                                Prefix.DISCORD, MessageType.SUCCESS, false);
                    });
                }
            } else {
                event.reply("❌ Code invalide ou expiré. Génère un nouveau code avec /link en jeu.")
                        .setEphemeral(true)
                        .queue();
            }
        });
    }

    /**
     * Sends a private message to a Discord user
     * @param discordId The Discord user ID to message
     * @param message The message content to send
     */
    public void sendMessageToUser(String discordId, String message) {
        if (jda == null) return;

        jda.retrieveUserById(discordId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessage(message).queue();
            });
        }, error -> {
            plugin.getLogger().log(Level.WARNING, "Impossible d'envoyer un message à l'utilisateur Discord: " + discordId);
        });
    }

    /**
     * Sends a message to a Discord text channel
     * @param channelId The ID of the channel to message
     * @param message The message content to send
     */
    public void sendMessageToChannel(String channelId, String message) {
        if (jda == null) return;

        jda.getTextChannelById(channelId).sendMessage(message).queue(null, error -> {
            plugin.getLogger().log(Level.WARNING, "Impossible d'envoyer un message au canal Discord: " + channelId);
        });
    }
}