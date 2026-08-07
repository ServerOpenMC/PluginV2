package fr.openmc.core.features.chatanimations;


import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.LootReward;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class ChatAnimation {
    @Setter
    private boolean finished = false;
    @Getter
    protected CustomLootTable reward;

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
        LootReward loot;
        if (winner == null)
            loot = null;
        else
            loot = reward.rollLoots(winner);
        ChatAnimationManager.onAnimationCompleted(this, winner, loot);
    }

    public final boolean isFinished() {
        return finished;
    }
}

