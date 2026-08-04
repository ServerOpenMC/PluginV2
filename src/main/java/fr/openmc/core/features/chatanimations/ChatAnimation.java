package fr.openmc.core.features.chatanimations;


import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class ChatAnimation {
    @Getter
    private final String id;

    @Setter
    private boolean finished = false;

    public ChatAnimation(String id) {
        this.id = id;
    }

    public abstract Component getName();
    public abstract Component getAnnounceStart();
    public abstract Component getDescriptionResult();

    public abstract long getTimeBeforeEnd();

    public void complete(Player winner) {
        if (finished) return;
        finished = true;
        ChatAnimationManager.onAnimationCompleted(this, winner);
    }

    public final boolean isFinished() {
        return finished;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChatAnimation animation && animation.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

