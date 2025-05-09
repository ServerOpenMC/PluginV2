package fr.openmc.core.features.accountdetection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.accountdetection.commands.AccountDetectionCommand;
import fr.openmc.core.utils.DiscordWebhook;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountDetectionManager implements Listener {
    @Getter
    private static AccountDetectionManager instance;
    private final BiMap<String, UUID> ipMap = HashBiMap.create();
    private final OMCPlugin plugin;
    private final File configFile;
    private boolean isAntiVpnEnabled;
    private boolean isAntiDoubleAccountEnabled;
    private Set<UUID> exemptedPlayers;
    private String webhookUrl;

    public AccountDetectionManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.configFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "accountdetection.yml");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        CommandsManager.getHandler().register(new AccountDetectionCommand());
        loadConfig();
    }

    public static JsonObject getVpnApiResponse(String ip) throws IOException, URISyntaxException {
        HttpsURLConnection con = (HttpsURLConnection) new URI("https://de.ipapi.is/?q=" + ip)
                .toURL()
                .openConnection(); // 1000 requêtes par jours sans api key avec une moyenne de 500ms de réponse quand j'ai testé
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        String body = sb.toString();
        return JsonParser.parseString(body).getAsJsonObject();
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/accountdetection.yml", false);
        }
        reload();
    }

    public void reload() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        isAntiVpnEnabled = config.getBoolean("anti-vpn");
        isAntiDoubleAccountEnabled = config.getBoolean("anti-double-account");
        webhookUrl = config.getString("discord-webhook");
        exemptedPlayers = config.getStringList("exempted-players").stream().map(UUID::fromString).collect(Collectors.toSet());
    }

    /**
     * Ajoute un joueur qui sera exempté lors de la vérification des doubles comptes/Vpn
     *
     * @param playersUUID l'UUID du joueur à exempter
     * @throws IOException si il y a une erreur lors de la sauvegarde
     */
    public void addExemptedPlayer(UUID playersUUID) throws IOException {
        exemptedPlayers.add(playersUUID);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("exempted-players", exemptedPlayers);
        config.save(configFile);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = Objects.requireNonNull(player.getAddress()).getHostString();
        if (exemptedPlayers.contains(player.getUniqueId())) return;
        if (isAntiDoubleAccountEnabled) verifyAccount(ip, player);
        if (isAntiVpnEnabled) verifyIpAddress(ip, player);
    }

    /**
     * Fonction asynchrone qui vérifie si le joueur a un double compte et exécute handleDoubleAccount si c'est le cas
     *
     * @param ip     l'Ip de joueur au format texte
     * @param player le joueur à vérifier
     */
    private void verifyAccount(String ip, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ipMap.containsKey(ip)) {
                    if (!ipMap.get(ip).equals(player.getUniqueId())) {
                        handleDoubleAccount(Bukkit.getOfflinePlayer(ipMap.get(ip)), player);
                    }
                } else {
                    ipMap.inverse().put(player.getUniqueId(), ip);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Code exécuté quand le joueur utilise un double compte
     *
     * @param firstPlayer  c'est le joueur qui s'était connecté avant avec la même Ip que secondPlayer
     * @param secondPlayer c'est le joueur qui vient de se connecter avec la même Ip que firstPlayer
     */
    private void handleDoubleAccount(OfflinePlayer firstPlayer, Player secondPlayer) {
        Component message = Component.text("On dirait que vous utilisez un double compte, nous tenons à rappeler que cela est strictement interdit, ").color(NamedTextColor.RED)
                .append(Component.text("aucune sanction ne vous est donnée pour le moment").color(NamedTextColor.RED).decorate(TextDecoration.UNDERLINED))
                .append(Component.text(", des modérateurs appliquerons une sanction si c'est bien le cas.").color(NamedTextColor.RED));
        MessagesManager.sendMessage(secondPlayer, message, Prefix.ACCOUTDETECTION, MessageType.WARNING, true);
        try {
            DiscordWebhook.sendMessage(webhookUrl, "Double compte détecté: `" + firstPlayer.getName() + "` et `" + secondPlayer.getName() + "`");
        } catch (Exception e) {
            plugin.getLogger().warning("Impossible d'envoyer le message sur Discord: " + e.getMessage());
        }
    }

    /**
     * Code exécuté quand le serveur détecte un Vpn
     *
     * @param player Le joueur qui a un Vpn
     */
    private void handleVpn(Player player, String detectedFlags) {
        Component message = Component.text("On dirait que vous utilisez un VPN, nous tenons à rappeler que cela est strictement interdit, ").color(NamedTextColor.RED)
                .append(Component.text("aucune sanction ne vous est donnée pour le moment").color(NamedTextColor.RED).decorate(TextDecoration.UNDERLINED))
                .append(Component.text(", des modérateurs appliquerons une sanction si c'est bien le cas.").color(NamedTextColor.RED));
        MessagesManager.sendMessage(player, message, Prefix.ACCOUTDETECTION, MessageType.WARNING, true);
        try {
            DiscordWebhook.sendMessage(
                    webhookUrl,
                    "Vpn détecté: " + player.getName() + " (flags: " + detectedFlags + "). Pour plus d'information exécutez `/accountdetection check " + player.getName() + "` sur le serveur minecraft."
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Impossible d'envoyer le message sur Discord: " + e.getMessage());
        }
    }

    /**
     * Vérifie si l'Ip est un VPN ou un proxy (etc) grâce à l'api ipapi.is.
     *
     * @param ip     L'Ip du joueur
     * @param player Le joueur
     */
    private void verifyIpAddress(String ip, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    JsonObject json = getVpnApiResponse(ip);
                    StringBuilder flags = new StringBuilder();
                    if (json.get("is_vpn").getAsBoolean()) flags.append("vpn, ");
                    if (json.get("is_proxy").getAsBoolean()) flags.append("proxy, ");
                    if (json.get("is_datacenter").getAsBoolean()) flags.append("datacenter, ");
                    if (json.get("is_tor").getAsBoolean()) flags.append("tor, ");
                    if (json.get("is_abuser").getAsBoolean()) flags.append("abuser, ");
                    if (!flags.isEmpty()) {
                        flags.setLength(flags.length() - 2); // retire le dernier ", "
                        handleVpn(player, flags.toString());
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Impossible de vérifier l'adresse IP: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}
