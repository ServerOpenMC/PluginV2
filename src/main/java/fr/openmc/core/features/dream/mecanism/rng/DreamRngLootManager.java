package fr.openmc.core.features.dream.mecanism.rng;

import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        List<Sound> sounds = new ArrayList<>();
        if (chance <= 0.001) { // 0.1%
            sounds.add(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 0.1f));
            player.getWorld().playSound(player.getLocation(), "minecraft:entity.ender_dragon.death", 1f, 0.1f);
        } else if (chance <= 0.05) { // 5%
            sounds.add(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 0.5f));
        } else if (chance <= 0.1) { // 10%
            sounds.add(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 1.0f));
        } else if (chance <= 0.25) { // 25%
            sounds.add(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 1.3f));
        } else {
            sounds.add(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 2f));
        }

        for (Sound sound : sounds) {
            player.playSound(sound);
        }
    }
}
