package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("homeworld")
@Description("Permet de définir les mondes où les homes sont interdits")
@CommandPermission("omc.admins.commands.home.world")
public class HomeWorld {

    private final DisabledWorldHome disabledWorldHome;
    public HomeWorld(DisabledWorldHome disabledWorldHome) {
        this.disabledWorldHome = disabledWorldHome;
    }

    @Subcommand("add")
    @Description("Set the world where homes are disabled")
    @CommandPermission("omc.admins.commands.home.world.add")
    @AutoComplete("@homeWorldsAdd *")
    public void setHomeDisabledWorld(Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            MessagesManager.sendMessage(player, Component.text("§cCe monde n'existe pas."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(disabledWorldHome.isDisabledWorld(world)) {
            MessagesManager.sendMessage(player, Component.text("§cLe monde §e" + world.getName() + " §cest déjà dans la liste des mondes où les homes sont désactivés."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        disabledWorldHome.addDisabledWorld(world, player);
        MessagesManager.sendMessage(player, Component.text("§aLe monde §e" + world.getName() + " §aa été ajouté à la liste des mondes où les homes sont §cdésactivés."), Prefix.HOME, MessageType.SUCCESS, true);
    }

    @Subcommand("remove")
    @Description("Remove the world where homes are disabled")
    @CommandPermission("omc.admins.commands.home.world")
    @AutoComplete("@homeWorldsRemove *")
    public void removeWorld(Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            MessagesManager.sendMessage(player, Component.text("§cLe monde §e" + worldName + " §cn'existe pas."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(disabledWorldHome.isDisabledWorld(world)) {
            disabledWorldHome.removeDisabledWorld(world);
            MessagesManager.sendMessage(player, Component.text("§aLe monde §e" + world.getName() + " §aa été retiré de la liste des mondes où les homes sont §cdésactivés."), Prefix.HOME, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(player, Component.text("§cLe monde §e " + world.getName() + " §cn'est pas dans la liste des mondes où les homes sont désactivés."), Prefix.HOME, MessageType.ERROR, true);
        }
    }

    @Subcommand("list")
    @Description("List the worlds where homes are disabled")
    @CommandPermission("omc.admins.commands.home.world")
    public void listWorlds(Player player) {
        if(disabledWorldHome.getDisabledWorlds().isEmpty()) {
            MessagesManager.sendMessage(player, Component.text("§cAucun monde n'est dans la liste des mondes où les homes sont désactivés."), Prefix.HOME, MessageType.ERROR, true);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§eListe des mondes où les homes sont désactivés :"), Prefix.HOME, MessageType.INFO, true);
        disabledWorldHome.getDisabledWorlds().forEach(worldName1 -> player.sendMessage("    §8- §e" + worldName1 + " §8(" + disabledWorldHome.getDisabledWorldInfo(worldName1) + "§8)"));
    }

}
