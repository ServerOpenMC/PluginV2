package fr.openmc.core.features.displays.scoreboards.sb;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public interface InfoSwapper {
    boolean isActive();
    void addInfosTo(Player player, List<Component> lines);
}
