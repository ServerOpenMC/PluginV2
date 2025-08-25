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

public class ItemDepositRequirement implements CityRequirement {
    private final ItemStack itemType;
    private final int amountRequired;

    public ItemDepositRequirement(Material itemMaterial, int amountRequired) {
        this.itemType = ItemStack.of(itemMaterial);
        this.amountRequired = amountRequired;
    }

    public ItemDepositRequirement(ItemStack itemType, int amountRequired) {
        this.itemType = itemType;
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isPredicateDone(City city) {
        return Objects.requireNonNull(
                CityStatisticsManager.getOrCreateStat(city.getUUID(), getScope())
        ).asInt() >= amountRequired;
    }

    @Override
    public String getScope() {
        return "deposit_" + itemType.getType().toString().toLowerCase();
    }

    @Override
    public ItemStack getIcon(City city) {
        return itemType;
    }

    @Override
    public Component getName(City city, CityLevels level) {
        if (city.getLevel() > level.ordinal()) {
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
                        CityStatisticsManager.getOrCreateStat(city.getUUID(), getScope())
                ).asInt(),
                amountRequired
        ));
    }

    @Override
    public Component getDescription() {
        return Component.text("§e§lCLIQUEZ ICI POUR DEPOSER");
    }

    public void runAction(Menu menu, City city, InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        int current = Objects.requireNonNull(
                CityStatisticsManager.getOrCreateStat(city.getUUID(), getScope())
        ).asInt();

        int remaining = amountRequired - current;
        if (remaining <= 0) return;

        int toRemove = e.isShiftClick() ? remaining : 1;

        int removed = ItemUtils.removeItemsFromInventory(player, itemType, toRemove);

        if (removed > 0) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous avez déposé §3" + (toRemove == 1 ? "un" : toRemove) + " ")
                            .append(ItemUtils.getItemTranslation(itemType).color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false)),
                    Prefix.CITY, MessageType.SUCCESS, false);
            CityStatisticsManager.increment(city.getUUID(), getScope(), removed);
            menu.open();
        }
    }
}
