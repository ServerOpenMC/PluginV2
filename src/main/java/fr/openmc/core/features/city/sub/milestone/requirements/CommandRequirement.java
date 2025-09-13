package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.EventCityRequirement;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Condition liée à l'exécution d'une commande.
 * La vérification se fait sur le nombre d'exécutions de la commande.
 */
public class CommandRequirement implements EventCityRequirement {

    /**
     * La commande à exécuter.
     */
    private final String command;

    /**
     * Le nombre d'exécutions requis.
     */
    private final int amountRequired;

    /**
     * Initialise la condition avec la commande et le nombre d'exécutions requis.
     *
     * @param command        la commande à suivre
     * @param amountRequired le nombre d'exécutions requis
     */
    public CommandRequirement(String command, int amountRequired) {
        this.command = command;
        this.amountRequired = amountRequired;
    }

    /**
     * Vérifie si le nombre d'exécutions de la commande est suffisant pour la ville donnée.
     *
     * @param city la ville concernée
     * @return true si la condition est remplie, false sinon.
     */
    @Override
    public boolean isPredicateDone(City city) {
        return Objects.requireNonNull(
                CityStatisticsManager.getOrCreateStat(city.getUniqueId(), getScope())
        ).asInt() >= amountRequired;
    }

    /**
     * Retourne le scope associé à cette condition.
     *
     * @return le scope sous la forme d'une chaîne de caractères.
     */
    @Override
    public String getScope() {
        return "command_" + command;
    }

    /**
     * Retourne l'icône représentant la condition.
     *
     * @param city la ville concernée
     * @return un ItemStack représentant un bloc de commande avec la quantité correspondante.
     */
    @Override
    public ItemStack getIcon(City city) {
        return ItemStack.of(Material.COMMAND_BLOCK, amountRequired);
    }

    /**
     * Retourne un composant texte décrivant la condition.
     * Affiche le nombre d'exécutions requis et (si applicable) la progression en cours.
     *
     * @param city  la ville concernée
     * @param level le niveau de la ville
     * @return un composant texte décrivant la condition.
     */
    @Override
    public Component getName(City city, CityLevels level) {
        if (city.getLevel() != level.ordinal()) {
            return Component.text(String.format(
                    "Exécuter %d fois %s",
                    amountRequired, command
            ));
        }

        return Component.text(String.format(
                "Exécuter %d fois %s (%d/%d)",
                amountRequired, command,
                Objects.requireNonNull(
                        CityStatisticsManager.getOrCreateStat(city.getUniqueId(), getScope())
                ).asInt(), amountRequired
        ));
    }

    /**
     * Retourne la description de la condition.
     *
     * @return null, aucune description pour le moment.
     */
    @Override
    public Component getDescription() {
        return null;
    }

    /**
     * Traite l'événement lié à la commande.
     * Incrémente le compteur de la ville si la commande correspond et que la condition n'est pas déjà satisfaite.
     *
     * @param event l'événement déclencheur
     */
    @Override
    public void onEvent(Event event) {
        if (!(event instanceof PlayerCommandPreprocessEvent e)) return;

        String cmd = e.getMessage();
        if (!cmd.equals(command)) return;

        Player player = e.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        if (Objects.requireNonNull(CityStatisticsManager.getOrCreateStat(playerCity.getUniqueId(), getScope())).asInt() >= amountRequired)
            return;

        CityStatisticsManager.increment(playerCity.getUniqueId(), getScope(), 1);
    }
}
