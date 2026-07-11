package fr.openmc.core.features.dream.mecanism.rng;

import fr.openmc.core.utils.RngUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class DreamRngLootManager {
    public static void sendMessageLoot(DreamRngLootEvent event) {
        double chance = event.getChance() != null ? event.getChance() : 0.0;
        Component prefix;
        if (chance <= 0.001) { // 0.1%
            prefix = TranslationManager.translation("feature.dream.rng.prefix.crazy_rare");
        } else if (chance <= 0.05) { // 5%
            prefix = TranslationManager.translation("feature.dream.rng.prefix.wow");
        } else if (chance <= 0.1) { // 10%
            prefix = TranslationManager.translation("feature.dream.rng.prefix.pretty_nice");
        } else if (chance <= 0.25) { // 25%
            prefix = TranslationManager.translation("feature.dream.rng.prefix.nice");
        } else {
            prefix = TranslationManager.translation("feature.dream.rng.prefix.good");
        }

        sendSoundLoot(event);
        Component chanceComponent = event.getChance() == null
                ? Component.empty()
                : TranslationManager.translation(
                        "feature.dream.rng.message.chance",
                        Component.text(Math.round(event.getChance() * 1000.0) / 10.0).color(NamedTextColor.AQUA)
                );
        MessagesManager.sendMessage(event.getPlayer(),
                TranslationManager.translation(
                        "feature.dream.rng.message.loot",
                        prefix,
                        Component.text(event.getAmount()).color(NamedTextColor.YELLOW),
                        event.getItem().displayName(),
                        chanceComponent
                ), Prefix.DREAM, MessageType.INFO,false);
    }

    private static void sendSoundLoot(DreamRngLootEvent event) {
        double chance = event.getChance() != null ? event.getChance() : 0.0;
        Player player = event.getPlayer();

        RngUtils.sendSoundRng(player, chance);
    }
}
