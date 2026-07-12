package fr.openmc.core.utils.world.entities;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class TextDisplay {

    private org.bukkit.entity.TextDisplay entity;

    public TextDisplay(Component text, Location location, Vector3f scale) {
        Location loc = at90(location);
        sync(() -> entity = loc.getWorld().spawn(loc, org.bukkit.entity.TextDisplay.class, display -> {
            display.text(text);
            display.setBillboard(Display.Billboard.VERTICAL);
            display.setBrightness(new Display.Brightness(15, 15));
            display.setViewRange(2.0f);
            display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), scale, new AxisAngle4f()));
            //garder les chunks des holo chargés
            //passer en persistant + tag PDC nettoyé au démarrage si un jour les holo sortent des spawn chunks
            display.setPersistent(false);
        }));
    }

    public void updateText(Component text) {
        sync(() -> {
            if (entity != null) entity.text(text);
        });
    }

    public void setScale(Vector3f scale) {
        sync(() -> {
            if (entity == null) return;
            Transformation t = entity.getTransformation();
            entity.setTransformation(new Transformation(t.getTranslation(), t.getLeftRotation(), scale, t.getRightRotation()));
        });
    }

    public void setLocation(Location location) {
        Location loc = at90(location);
        sync(() -> {
            if (entity != null) entity.teleport(loc);
        });
    }

    /** Force le yaw à 90° (pitch 0) pour que l'holo soit toujours orienté pareil. */
    private static Location at90(Location location) {
        Location loc = location.clone();
        loc.setYaw(90f);
        loc.setPitch(0f);
        return loc;
    }

    public void remove() {
        sync(() -> {
            if (entity != null) {
                entity.remove();
                entity = null;
            }
        });
    }

    private static void sync(Runnable action) {
        if (Bukkit.isPrimaryThread()) action.run();
        else Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), action);
    }
}
