package fr.openmc.core.features.contest;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public record ContestPlayer(String name, int points, int camp, ChatColor color) {
}

