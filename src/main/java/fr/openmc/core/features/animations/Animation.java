package fr.openmc.core.features.animations;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Animation {
    JOIN_RIFT(
            "join_rift",
            "omc_sounds:ambient.join_rift"
    );

    private final String nameAnimation;
    private final String soundName;
    public final Map<Integer, Vector> cameraPositions = new HashMap<>();
    public final Map<Integer, Vector> cameraViews = new HashMap<>();
    @Setter
    private int totalTicks;

    // nameAnimationJson = file .animation.json
    // nameAnimation
    Animation(String nameAnimation, String soundName) {
        this.nameAnimation = nameAnimation;
        this.soundName = soundName;
    }
}
