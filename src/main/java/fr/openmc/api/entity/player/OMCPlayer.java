package fr.openmc.api.entity.player;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import lombok.experimental.Delegate;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Wrapper autour d'un {@link Player} pour les methodes propres a OpenMC
 * (economie, ville, menus...).
 * <p>
 * Exemples :
 * <pre>{@code
 * // Dans une commande Lamp, en sender ou en argument :
 * @Command("balance")
 * public void balance(OMCPlayer player) {
 *      player.message().sendInfo(Component.text("Vous avez " + player.getFormattedBalance()));
 * }
 *
 * // Dans un event :
 * OMCPlayer player = OMCPlayer.of(event.getPlayer());
 * if (player.hasCity()) { ... }
 * }</pre>
 */
@SuppressWarnings({"unused", "removal", "deprecation"})
public class OMCPlayer implements Player {

    @Delegate(types = Player.class)
    private final Player player;

    private OMCPlayer(Player player) {
        this.player = player;
    }

    /**
     * Enveloppe un {@link Player} dans un {@link OMCPlayer}.
     * Si le joueur est deja un {@link OMCPlayer}, il est retourne tel quel.
     *
     * @param player le joueur a envelopper
     * @return le {@link OMCPlayer} correspondant
     */
    public static OMCPlayer of(@NotNull Player player) {
        if (player instanceof OMCPlayer omcPlayer)
            return omcPlayer;
        return new OMCPlayer(player);
    }

    /**
     * Return the {@link Player} wrapped by this {@link OMCPlayer}.
     *
     * @return the wrapped {@link Player}
     */
    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OMCPlayer omcPlayer) {
            obj = omcPlayer.getPlayer();
        }
        return player.equals(obj);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    @Override
    public String toString() {
        return "OMCPlayer{" +
                "username=" + player.getDisplayName() +
                ", uuid=" + player.getUniqueId() +
                "}";
    }

    private final Message message = new Message();
    private final Economy economy = new Economy();
    private final City city = new City();

    /**
     * Acces au namespace messages du joueur.
     * <pre>{@code
     * player.message().sendSuccess(Component.text("Ville creee !"));
     * player.message().send(msg, Prefix.CITY, MessageType.ERROR);
     * }</pre>
     */
    public Message message() {
        return message;
    }

    public Economy economy() {
        return economy;
    }

    public City city() {
        return city;
    }

    public class Message {

        public void send(Component message, Prefix prefix, MessageType type, boolean sound) {
            MessagesManager.sendMessage(player, message, prefix, type, sound);
        }

        public void send(Component message, Prefix prefix, MessageType type) {
            send(message, prefix, type, false);
        }

        public void send(Component message, Prefix prefix) {
            send(message, prefix, MessageType.NONE, false);
        }

        public void send(Component message, MessageType type) {
            send(message, Prefix.OPENMC, type, false);
        }

        public void send(Component message) {
            send(message, Prefix.OPENMC, MessageType.NONE, false);
        }

        public void sendSuccess(Component message, boolean sound) {
            send(message, Prefix.OPENMC, MessageType.SUCCESS, sound);
        }

        public void sendSuccess(Component message) {
            sendSuccess(message, true);
        }

        public void sendError(Component message, Prefix prefix, boolean sound) {
            send(message, prefix, MessageType.ERROR, sound);
        }

        public void sendError(Component message, Prefix prefix) {
            sendError(message, prefix, false);
        }

        public void sendError(Component message, boolean sound) {
            sendError(message, Prefix.OPENMC, sound);
        }

        public void sendError(Component message) {
            sendError(message, true);
        }

        public void sendWarning(Component message, boolean sound) {
            send(message, Prefix.OPENMC, MessageType.WARNING, sound);
        }

        public void sendWarning(Component message) {
            sendWarning(message, true);
        }

        public void sendInfo(Component message, Prefix prefix, boolean sound) {
            send(message, prefix, MessageType.INFO, sound);
        }

        public void sendInfo(Component message, Prefix prefix) {
            send(message, prefix, MessageType.INFO, false);
        }

        public void sendInfo(Component message, boolean sound) {
            send(message, Prefix.OPENMC, MessageType.INFO, sound);
        }

        public void sendInfo(Component message) {
            sendInfo(message, true);
        }
    }

    public class Economy {
        /**
         * Recupere la balance du joueur
         *
         * @return la balance du joueur
         */
        public double getBalance() {
            return EconomyManager.getBalance(getUniqueId());
        }

        /**
         * Recupere la balance du joueur formatee avec le symbole de la monnaie
         *
         * @return la balance du joueur formatee
         */
        public String getFormattedBalance() {
            return EconomyManager.getFormattedBalance(getUniqueId());
        }

        public void addBalance(double amount) {
            EconomyManager.addBalance(getUniqueId(), amount);
        }

        public void addBalance(double amount, @Nullable String reason) {
            EconomyManager.addBalance(getUniqueId(), amount, reason);
        }

        public boolean withdrawBalance(double amount) {
            return EconomyManager.withdrawBalance(getUniqueId(), amount);
        }

        public boolean withdrawBalance(double amount, @Nullable String reason) {
            return EconomyManager.withdrawBalance(getUniqueId(), amount, reason);
        }

        public void setBalance(double amount) {
            EconomyManager.setBalance(getUniqueId(), amount);
        }

        public boolean pay(UUID targetUUID, double amount, @Nullable String reason) {
            return EconomyManager.transferBalance(getUniqueId(), targetUUID, amount, reason);
        }
    }

    public class City {
        @Nullable
        public fr.openmc.core.features.city.City getCity() {
            return CityManager.getCity(player.getUniqueId());
        }

        public boolean hasCity() {
            return getCity() != null;
        }
    }
}
