package fr.openmc.core.features.events.contents.christmas.mecanism.gift.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.GiftManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("christmas admin")
@CommandPermission("omc.admins.commands.christmas.admin")
public class AdminGiftCommands {
    @Subcommand("placeGiftBlock")
    @CommandPermission("omc.admins.commands.christmas.admin.placegiftblock")
    void placeGiftBlock(
            OMCPlayer player
    ) {
        Block block = player.getTargetBlockExact(10);
        if (block == null) {
            player.message().send(Component.text("Vous devez regarder un bloc"));
            return;
        }

        block.setType(Material.ICE);
        GiftManager.placeGiftInBlock(block, true);
    }
}
