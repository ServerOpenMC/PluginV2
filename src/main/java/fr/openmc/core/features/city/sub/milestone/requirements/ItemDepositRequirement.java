package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Représente une condition avec laquelle le joueur doit déposer un certain nombre d'items.
 */
public class ItemDepositRequirement implements CityRequirement {

    /**
     * Type d'item requis.
     */
    private final ItemStack itemType;

    /**
     * Nombre d'items à déposer pour satisfaire la condition.
     */
    private final int amountRequired;

    /**
     * Crée une condition de dépôt en spécifiant un type d'item via un matériau.
     *
     * @param itemMaterial   le matériau de l'item requis
     * @param amountRequired le nombre d'items requis
     */
    public ItemDepositRequirement(Material itemMaterial, int amountRequired) {
        this.itemType = ItemStack.of(itemMaterial);
        this.amountRequired = amountRequired;
    }

    /**
     * Crée une condition de dépôt en spécifiant directement un ItemStack.
     *
     * @param itemType       l'ItemStack requis
     * @param amountRequired le nombre d'items requis
     */
    public ItemDepositRequirement(ItemStack itemType, int amountRequired) {
        this.itemType = itemType;
        this.amountRequired = amountRequired;
    }

    /**
     * Vérifie si la condition de dépôt est satisfaite pour la ville.
     *
     * @param city la ville concernée
     * @return {@code true} si le nombre d'items déposés est suffisant, {@code false} sinon
     */
    @Override
    public boolean isPredicateDone(City city) {
        return Objects.requireNonNull(
                CityStatisticsManager.getOrCreateStat(city.getUniqueId(), getScope())
        ).asInt() >= amountRequired;
    }

    /**
     * Retourne le scope utilisé pour identifier la statistique de dépôt.
     *
     * @return le scope sous forme de chaîne de caractères
     */
    @Override
    public String getScope() {
        return "deposit_" + itemType.getType().toString().toLowerCase();
    }

    /**
     * Retourne l'icône associée à la condition.
     *
     * @param city la ville concernée
     * @return l'ItemStack représentant l'icône
     */
    @Override
    public ItemStack getIcon(City city) {
        return itemType;
    }

    /**
     * Retourne le nom de la condition sous forme de composant texte.
     * Affiche la progression si la ville n'a pas dépassé le niveau courant.
     *
     * @param city  la ville concernée
     * @param level le niveau courant de la ville
     * @return un composant texte décrivant la condition de dépôt
     */
    @Override
    public Component getName(City city, CityLevels level) {
        if (city.getLevel() != level.ordinal()) {
            return Component.text(String.format(
                    "Déposer %d %s",
                    amountRequired,
                    ItemUtils.getItemName(itemType)
            ));
        }

        return Component.text(String.format(
                "Déposer %d %s (%d/%d)",
                amountRequired,
                ItemUtils.getItemName(itemType),
                Objects.requireNonNull(
                        CityStatisticsManager.getOrCreateStat(city.getUniqueId(), getScope())
                ).asInt(),
                amountRequired
        ));
    }

    /**
     * Retourne la description de la condition.
     *
     * @return un composant texte indiquant à cliquer pour déposer
     */
    @Override
    public Component getDescription() {
        return Component.text("§e§lCLIQUEZ ICI POUR DEPOSER");
    }

    /**
     * Exécute l'action de dépôt d'item.
     * Si le joueur dépose des items, la statistique associée est mise à jour.
     *
     * @param menu la menu à réouvrir après action
     * @param city la ville concernée
     * @param e    l'événement de clic sur l'inventaire
     */
    public void runAction(Menu menu, City city, InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        int current = Objects.requireNonNull(
                CityStatisticsManager.getOrCreateStat(city.getUniqueId(), getScope())
        ).asInt();

        int remaining = amountRequired - current;
        if (remaining <= 0) return;

        int toRemove = e.isShiftClick() ? remaining : 1;

        int removed = ItemUtils.removeItemsFromInventory(player, itemType, toRemove);

        if (removed > 0) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous avez déposé §3" + (toRemove == 1 ? "un" : toRemove) + " ")
                            .append(Component.text(ItemUtils.getItemName(itemType))
                                    .color(NamedTextColor.DARK_AQUA)
                                    .decoration(TextDecoration.ITALIC, false)),
                    Prefix.CITY, MessageType.SUCCESS, false);
            CityStatisticsManager.increment(city.getUniqueId(), getScope(), removed);
            menu.open();
        }
    }
}
