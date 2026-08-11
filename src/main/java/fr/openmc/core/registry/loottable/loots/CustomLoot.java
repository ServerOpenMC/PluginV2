package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.loottable.LootReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CustomLoot {
    Component getDisplayText();
    double getChance();
    void setChance(double chance);
    LootReward run(Player receiver);

    default ItemStack getRepresentativeItem() {
        if (this instanceof RepresentedItem representedItem)
            return representedItem.getRepresentativeItem();
        return null;
    }

    default Component buildLootComponent(int amount) {
        if (this.getDisplayText() == null || this instanceof TableLoot) return null;

        Component base = Component.text(" - ", NamedTextColor.GRAY);

        if (amount != -1)
            base = base.append(Component.text(amount + "x "));

        return base.append(this.getDisplayText())
                .append(Component.text(
                        " (" + Math.round(this.getChance() * 100.0) + "% ★)",
                        NamedTextColor.AQUA
                ));
    }

    /**
     * Envoie le message de loot contenant, le nom du loot et la chance du loot
     * @param player le joueur a qui envoyé le message
     * @param amount le nombre de loot
     */
    default void sendLootMessage(Player player, int amount) {
        Component component = buildLootComponent(amount);

        if (component != null) {
            player.sendMessage(component);
        }
    }
}