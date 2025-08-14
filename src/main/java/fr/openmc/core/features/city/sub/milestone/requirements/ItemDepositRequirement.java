package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class ItemDepositRequirement implements CityRequirement {
    private final ItemStack itemType;
    private final int amountRequired;

    public ItemDepositRequirement(ItemStack itemType, int amountRequired) {
        this.itemType = itemType;
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isDone(City city) {
        return false; //todo: CityStatistics
    }

    @Override
    public ItemStack getIcon() {
        return itemType;
    }

    @Override
    public Component getName() {
        return Component.text("Déposer " + amountRequired + " ").append(ItemUtils.getItemTranslation(itemType));
    }

    @Override
    public Component getDescription() {
        return Component.text("§e§lCLIQUEZ ICI POUR DEPOSER");
    }

    public void runAction(City city, InventoryClickEvent e) {
        //todo: implement deposit logic (like contest)
    }
}
