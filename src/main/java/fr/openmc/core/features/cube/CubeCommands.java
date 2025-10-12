package fr.openmc.core.features.cube;

import fr.openmc.core.features.cube.multiblocks.MultiBlock;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("cube")
@CommandPermission("omc.admins.commands.cube")
public class CubeCommands {

    @Subcommand("startShock")
    @CommandPermission("omc.admins.commands.cube.shock")
    @AutoComplete("@cubes")
    public void startShock(Player player, String cubeLoc) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        cube.startMagneticShock();
        MessagesManager.sendMessage(player, Component.text("Choc éléctro-magnétique lancé"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("startBubble")
    @CommandPermission("omc.admins.commands.cube.bubble")
    @AutoComplete("@cubes")
    public void startCorruptedBubble(Player player, String cubeLoc) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        cube.startCorruptedBubble();
        MessagesManager.sendMessage(player, Component.text("Bulle corrompue lancé"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("reproduce")
    @CommandPermission("omc.admins.commands.cube.reproduce")
    @AutoComplete("@cubes")
    public void reproduceCube(Player player, String cubeLoc) {
        Cube cube = getInputCubes(player, cubeLoc);
        if (cube == null) return;

        cube.startReproduction();
        MessagesManager.sendMessage(player, Component.text("Reproduction du cube lancée !"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("reproduceForce")
    @CommandPermission("omc.admins.commands.cube.reproduce_force")
    @AutoComplete("@cubes")
    public void reproduceForceCube(Player player, String cubeLoc) {
        Cube cube = getInputCubes(player, cubeLoc);

        if (cube == null) return;

        if (cube.reproductionTask == null) {
            MessagesManager.sendMessage(player, Component.text("La reproduction n'est pas en cours, utilisez §6/cube reproduce"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        cube.reproductionTask.forceReproduction();
        
        MessagesManager.sendMessage(player, Component.text("Reproduction forcée du cube!"), Prefix.STAFF, MessageType.SUCCESS, false);

    }

    private Cube getInputCubes(Player player, String cubeLoc) {
        String[] split = cubeLoc.split(":");
        if (split.length != 2) {
            MessagesManager.sendMessage(player, Component.text("Format invalide !"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            MessagesManager.sendMessage(player, Component.text("Monde introuvable"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        String[] coords = split[1].split(",");
        if (coords.length != 3) {
            MessagesManager.sendMessage(player, Component.text("Coordonnées invalides"), Prefix.STAFF, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(player, Component.text("Aucun cube trouvé"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }

        if (mb instanceof Cube cube) {
            return cube;
        } else {
            MessagesManager.sendMessage(player, Component.text("Ce n'est pas un cube"), Prefix.STAFF, MessageType.ERROR, false);
            return null;
        }
    }
}