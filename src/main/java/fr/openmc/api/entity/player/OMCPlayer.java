package fr.openmc.api.entity.player;

import fr.openmc.api.menulib.Menu;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

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
@SuppressWarnings({"deprecation", "removal"})
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

    public void open(@NotNull Menu menu) {
        menu.open();
    }

    /**
     * Construit puis ouvre un {@link Menu} pour ce joueur.
     * Le joueur est automatiquement insere comme premier argument du constructeur :
     * <pre>{@code
     * player.open(CityChestMenu.class, city, page);
     * // equivaut a : new CityChestMenu(player, city, page).open();
     * }</pre>
     *
     * @param menuClass la classe du menu a ouvrir
     * @param args      les arguments du constructeur, sans le joueur
     * @return le menu ouvert
     * @throws IllegalArgumentException si aucun constructeur public ne correspond aux arguments
     */
    public <T extends Menu> T open(@NotNull Class<T> menuClass, Object... args) {
        for (Constructor<?> constructor : menuClass.getConstructors()) {
            if (!matches(constructor, args)) {
                continue;
            }

            Object[] fullArgs = new Object[args.length + 1];
            fullArgs[0] = constructor.getParameterTypes()[0] == OMCPlayer.class ? this : player;
            System.arraycopy(args, 0, fullArgs, 1, args.length);

            try {
                T menu = menuClass.cast(constructor.newInstance(fullArgs));
                menu.open();
                return menu;
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Erreur dans le constructeur de " + menuClass.getSimpleName(), e.getCause());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Impossible d'instancier le menu " + menuClass.getSimpleName(), e);
            }
        }

        String argTypes = Arrays.stream(args)
                .map(arg -> arg == null ? "null" : arg.getClass().getSimpleName())
                .collect(Collectors.joining(", "));
        throw new IllegalArgumentException("Aucun constructeur public de " + menuClass.getSimpleName()
                + " ne correspond a (Player" + (argTypes.isEmpty() ? "" : ", " + argTypes) + ")");
    }

    /**
     * Verifie que le constructeur accepte un joueur en premier parametre,
     * suivi des arguments donnes.
     */
    private static boolean matches(Constructor<?> constructor, Object[] args) {
        Class<?>[] params = constructor.getParameterTypes();
        if (params.length != args.length + 1) {
            return false;
        }
        if (!params[0].isAssignableFrom(Player.class) && params[0] != OMCPlayer.class) {
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                if (params[i + 1].isPrimitive()) {
                    return false;
                }
            } else if (!wrap(params[i + 1]).isInstance(args[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retourne la classe wrapper d'un type primitif (int -> Integer...),
     * ou le type lui-meme s'il n'est pas primitif.
     */
    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == boolean.class) return Boolean.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        return Character.class;
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
        return "OMCPlayer{" + player.getName() + "}";
    }

    private final Message message = new Message();
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

    public City city() {
        return city();
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
