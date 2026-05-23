package fr.openmc.core.features.cube;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.cube.events.CubeDisableBubbleEvent;
import fr.openmc.core.features.cube.multiblocks.MultiBlock;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("cube")
@CommandPermission("omc.admins.commands.cube")
public class CubeCommands {

    @Subcommand("startShock")
    @CommandPermission("omc.admins.commands.cube.shock")
    public void startShock(
            Player player,
            @Named("cubeLoc") @SuggestWith(CubeLocationAutoComplete.class) String cubeLoc
    ) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        cube.startMagneticShock();
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.start_shock"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("startBubble")
    @CommandPermission("omc.admins.commands.cube.bubble")
    public void startCorruptedBubble(
            Player player,
            @Named("cubeLoc") @SuggestWith(CubeLocationAutoComplete.class) String cubeLoc
    ) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        cube.startCorruptedBubble();
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.start_bubble"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("stopShock")
    @CommandPermission("omc.admins.commands.cube.shock")
    public void stopShock(
            Player player,
            @Named("cubeLoc") @SuggestWith(CubeLocationAutoComplete.class) String cubeLoc
    ) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.stop_shock"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("stopBubble")
    @CommandPermission("omc.admins.commands.cube.bubble")
    public void stopCorruptedBubble(
            Player player,
            @Named("cubeLoc") @SuggestWith(CubeLocationAutoComplete.class) String cubeLoc
    ) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        if (cube.particuleBubbleTask != null) {
            cube.particuleBubbleTask.cancel();
            cube.particuleBubbleTask = null;
        }

        if (cube.corruptedBubbleTask != null) {
            cube.corruptedBubbleTask.cancel();
            cube.corruptedBubbleTask = null;
        }
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CubeDisableBubbleEvent(cube)));
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.stop_bubble"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("reproduce")
    @CommandPermission("omc.admins.commands.cube.reproduce")
    public void reproduceCube(
            Player player,
            @Named("cubeLoc") @SuggestWith(CubeLocationAutoComplete.class) String cubeLoc
    ) {
        Cube cube = getInputCubes(player, cubeLoc);
        if (cube == null) return;

        cube.startReproduction();
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.reproduce"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("reproduceForce")
    @CommandPermission("omc.admins.commands.cube.reproduce_force")
    public void reproduceForceCube(
            Player player,
            @Named("cubeLoc") @SuggestWith(CubeLocationAutoComplete.class) String cubeLoc
    ) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        if (cube.reproductionTask == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.cube.command.reproduce_force.unavailable",
                    Component.text("/cube reproduce").color(NamedTextColor.GOLD)
            ), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        cube.reproductionTask.forceReproduction();
        
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.reproduce_force.success"), Prefix.STAFF, MessageType.SUCCESS, false);

    }

    private Cube getInputCubes(Player player, String cubeLoc) {
        String[] split = cubeLoc.split(":");
        if (split.length != 2) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.invalid_format"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.world_not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        String[] coords = split[1].split(",");
        if (coords.length != 3) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.invalid_coords"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        int z = Integer.parseInt(coords[2]);

        MultiBlock mb = MultiBlockManager.getMultiBlocks().stream()
                .filter(m -> m instanceof Cube)
                .filter(m -> m.origin.getBlockX() == x
                        && m.origin.getBlockY() == y
                        && m.origin.getBlockZ() == z
                        && m.origin.getWorld().equals(world))
                .findFirst()
                .orElse(null);

        if (mb == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        if (mb instanceof Cube cube) {
            return cube;
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.cube.command.not_cube"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }
    }
}