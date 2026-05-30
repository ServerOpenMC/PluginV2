package fr.openmc.core.features.homes.command;

import fr.openmc.core.features.homes.command.autocomplete.HomeWorldAddAutoComplete;
import fr.openmc.core.features.homes.command.autocomplete.HomeWorldRemoveAutoComplete;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("homeworld")
@Description("Permet de définir les mondes où les homes sont interdits")
@CommandPermission("omc.admins.commands.home.world")
public class HomeWorldCommand {

    @Subcommand("add")
    @Description("Set the world where homes are disabled")
    @CommandPermission("omc.admins.commands.home.world.add")
    public void setHomeDisabledWorld(
            Player player,
            @Named("homeWorldAdd") @SuggestWith(HomeWorldAddAutoComplete.class) String worldName
    ) {
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.world.not_found"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }

        if(DisabledWorldHome.isDisabledWorld(world)) {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.homes.world.already_disabled",
                            Component.text(world.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    MessageType.ERROR,
                    true
            );
            return;
        }

        DisabledWorldHome.addDisabledWorld(world, player);
        MessagesManager.sendMessage(
                player,
                TranslationManager.translation(
                        "feature.homes.world.added",
                        Component.text(world.getName()).color(NamedTextColor.YELLOW)
                ),
                Prefix.HOME,
                MessageType.SUCCESS,
                true
        );
    }

    @Subcommand("remove")
    @Description("Remove the world where homes are disabled")
    @CommandPermission("omc.admins.commands.home.world")
    public void removeWorld(Player player, @SuggestWith(HomeWorldRemoveAutoComplete.class) String worldName) {
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.homes.world.remove.not_found",
                            Component.text(worldName).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    MessageType.ERROR,
                    true
            );
            return;
        }

        if(DisabledWorldHome.isDisabledWorld(world)) {
            DisabledWorldHome.removeDisabledWorld(world);
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.homes.world.removed",
                            Component.text(world.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    MessageType.SUCCESS,
                    true
            );
        } else {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.homes.world.not_disabled",
                            Component.text(world.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    MessageType.ERROR,
                    true
            );
        }
    }

    @Subcommand("list")
    @Description("List the worlds where homes are disabled")
    @CommandPermission("omc.admins.commands.home.world")
    public void listWorlds(Player player) {
        if(DisabledWorldHome.getDisabledWorlds().isEmpty()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.world.list.empty"), Prefix.HOME, MessageType.ERROR, true);
            return;
        }
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.world.list.title"), Prefix.HOME, MessageType.INFO, true);
        DisabledWorldHome.getDisabledWorlds().forEach(worldName1 -> player.sendMessage(
                TranslationManager.translation(
                        "feature.homes.world.list.item",
                        Component.text(worldName1).color(NamedTextColor.YELLOW),
                        DisabledWorldHome.getDisabledWorldInfo(worldName1)
                )
        ));
    }

}
