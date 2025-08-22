package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.conditions.CityChestConditions;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static fr.openmc.core.features.city.conditions.CityChestConditions.UPGRADE_PER_AYWENITE;
import static fr.openmc.core.features.city.conditions.CityChestConditions.UPGRADE_PER_MONEY;

public class CityChestAction {

    public static void upgradeChest(Player player, City city) {
        if (!CityChestConditions.canCityChestUpgrade(city, player)) return;

        int price = city.getChestPages() * UPGRADE_PER_MONEY;
        int aywenite = city.getChestPages() * UPGRADE_PER_AYWENITE;

        city.updateBalance(-price);

        if (ItemUtils.takeAywenite(player, aywenite)) {
            city.saveChestContent(city.getChestPages() + 1, null);
            MessagesManager.sendMessage(player, Component.text("Le coffre a été amélioré"), Prefix.CITY, MessageType.SUCCESS, true);
        }
    }
}