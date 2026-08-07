package fr.openmc.core.features.chatanimations.contents.challenge.types;

import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeAnimation;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MineBlocksChallenge extends ChallengeAnimation {
    private final Material material;
    private final int target;
    private final Map<UUID, Integer> progress = new HashMap<>();

    public MineBlocksChallenge(Material material, int target, long time) {
        super(TranslationManager.translation("feature.chatanimations.challenge.mine_blocks.name",
                Component.text(target), Component.translatable(material.translationKey())), time);
        this.material = material;
        this.target = target;
    }

    public MineBlocksChallenge(Material material, int target, CustomLootTable reward, long time) {
        super(TranslationManager.translation("feature.chatanimations.challenge.mine_blocks.name",
                Component.text(target), Component.translatable(material.translationKey())), reward, time);
        this.material = material;
        this.target = target;
    }

    @Override
    public void stop() {
        progress.clear();
    }
}
