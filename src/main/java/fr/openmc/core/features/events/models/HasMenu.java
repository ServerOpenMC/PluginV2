package fr.openmc.core.features.events.models;

import fr.openmc.api.menulib.Menu;
import org.bukkit.entity.Player;

public interface HasMenu {
    Menu getInfoMenu(Player player);
}

