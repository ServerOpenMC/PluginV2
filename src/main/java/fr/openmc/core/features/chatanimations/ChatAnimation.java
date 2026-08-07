package fr.openmc.core.features.chatanimations;


import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class ChatAnimation {
    @Setter
    private boolean finished = false;

    public abstract Component getName();
    public abstract Component getAnnounceStart();
    public abstract Component getDescriptionResult();

    public abstract long getTimeBeforeEnd();

    public void stop() {
        // a override si besoin
    }

    public void complete(Player winner) {
        if (finished) return;
        finished = true;
        ChatAnimationManager.onAnimationCompleted(this, winner);
    }

    public final boolean isFinished() {
        return finished;
    }
}

