package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityChestAction;
import fr.openmc.core.features.city.conditions.CityChestConditions;
import fr.openmc.core.features.city.menu.CityChestMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityChestCommand {
    @Command({"city chest", "ville coffre"})
    @Description("Ouvre le coffre de la ville")
    @CommandPermission("omc.commands.city.chest")
    void chest(
            Player player,
            @Optional @Named("page") @Range(min = 0) Integer page
    ) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityChestConditions.canCityChestOpen(city, player)) return;

        if ((page == null)) page = 1;
        if (page < 1) page = 1;
        if (page > city.getChestPages()) page = city.getChestPages();

        new CityChestMenu(player, city, page).open();
    }

    @Command({"city upgradechest", "ville upgradecoffre"})
    @Description("Améliore la coffre de la ville")
    @CommandPermission("omc.commands.city.chest_upgrade")
    void upgrade(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityChestConditions.canCityChestUpgrade(city, player)) return;

        CityChestAction.upgradeChest(player, city);
    }
}
