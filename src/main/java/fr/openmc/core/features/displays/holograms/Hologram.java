package fr.openmc.core.features.displays.holograms;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
public class Hologram {
    private final String name;
    private Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0); // valeur par défaut
    @Setter
    private float scale = 1.0f;
    private Component[] lines = new Component[0];

    public Hologram(String name) {
        this.name = name;
    }

    public void setLocation(double x, double y, double z) {
        this.location = new Location(Bukkit.getWorld("world"), x, y, z);
    }

    public void setLines(Component... lines) {
        this.lines = lines;
    }

    public Component toComponent() {
        Component component = null;

        for (Component rawLine : lines) {
            if (component == null) {
                component = rawLine;
            } else {
                component = component.append(Component.newline()).append(rawLine);
            }
        }
        return component;
    }
}
