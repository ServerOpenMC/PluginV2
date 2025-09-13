package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.actions.CityTransferAction;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command("admcity")
@CommandPermission("omc.admins.commands.admincity")
public class AdminCityCommands {
    @Subcommand("deleteCity")
    @CommandPermission("omc.admins.commands.admincity.deleteCity")
    void deleteCity(Player player, @Named("name") String name) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityManager.deleteCity(city);
        MessagesManager.sendMessage(player, Component.text("La ville a été supprimée"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    private static final int PER_PAGE = 10;

    @Subcommand("list")
    @CommandPermission("omc.admins.commands.admincity.list")
    void list(Player player) {
        List<City> all = new ArrayList<>(CityManager.getCities());

        all.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        int page = 1;

        int total = all.size();
        int maxPage = (int) Math.ceil(total / (double) PER_PAGE);
        if (page > maxPage) page = maxPage;

        int start = (page - 1) * PER_PAGE;
        int end = Math.min(start + PER_PAGE, total);
        List<City> sub = all.subList(start, end);
        MessagesManager.sendMessage(
                player,
                Component.text("—— Villes (page " + page + " / " + maxPage + ") ——")
                        .color(NamedTextColor.GOLD),
                Prefix.STAFF,
                MessageType.SUCCESS,
                false
        );

        sub.forEach(city -> {
            UUID cityUUID = city.getUniqueId();
            String name = city.getName();

            Component line = Component.text("- ")
                    .append(Component.text(cityUUID.toString()).color(NamedTextColor.GRAY))
                    .append(Component.text(" • "))
                    .append(Component.text(name).color(NamedTextColor.WHITE))
                    .append(Component.text(" [copier]")
                            .color(NamedTextColor.GREEN)
                            .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                                    Component.text("Clique pour copier l’UUID"))
                            )
                            .clickEvent(ClickEvent.copyToClipboard(cityUUID.toString()))
                    );

            player.sendMessage(line);
        });

        Component nav = Component.empty()
                .append(page > 1
                        ? Component.text("« Prev").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/admcity list " + (page - 1)))
                        : Component.text("       "))
                .append(Component.text("    "))
                .append(page < maxPage
                        ? Component.text("Next »").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/admcity list " + (page + 1)))
                        : Component.text("      "));

        player.sendMessage(nav);
    }

    @Subcommand("info")
    @CommandPermission("omc.admins.commands.admincity.info")
    @AutoComplete("<name>")
    void info(Player player, @Named("name") String name) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Cette ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        new CityListDetailsMenu(player, city).open();
    }

    @Subcommand("rename")
    @CommandPermission("omc.admins.commands.admincity.rename")
    void rename(Player player, @Named("name") String name, @Named("nouveau nom") String newName) {
        City newNameCity = CityManager.getCityByName(newName);
        if (newNameCity != null) {
            MessagesManager.sendMessage(player, Component.text("Une ville a déjà le nom que vous voulez mettre"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        city.rename(newName);

        MessagesManager.sendMessage(player, Component.text("La ville a été renommée"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(Player player, @Named("name") String name, @Named("nouveau propriétaire") Player newOwner) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityTransferAction.transfer(player, city, newOwner);
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(Player player, @Named("name") String name, @Named("balance") double newBalance) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.setBalance(newBalance);
        MessagesManager.sendMessage(player, Component.text("Le solde a été modifié"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(Player player, String name) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player, Component.text("Le solde de la ville est de "+ city.getBalance()+ EconomyManager.getEconomyIcon()), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("addPlayer")
    @CommandPermission("omc.admins.commands.admincity.addplayer")
    void add(Player player, @Named("name") String name, Player newMember) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getPlayerCity(newMember.getUniqueId()) != null) {
            MessagesManager.sendMessage(player, Component.text("Le joueur est déjà dans une ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.addPlayer(newMember.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Le joueur a été ajouté"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.admincity.remove")
    void remove(Player player, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Le joueur n'est pas dans une ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(member.getUniqueId(), CityPermission.OWNER)) {
            MessagesManager.sendMessage(player, Component.text("Le joueur est le propriétaire de la ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.removePlayer(member.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("Le joueur a été retiré"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getPlayer")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(Player player, Player member) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Le joueur n'est pas dans une ville"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player, Component.text("Le joueur est dans la ville " + city.getName() + " (" + city.getUniqueId() + ")"), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("claim bypass")
    @CommandPermission("omc.admins.commands.admincity.claim.bypass")
    public void bypass(Player player) {
        UUID uuid = player.getUniqueId();
        boolean canBypass = ProtectionsManager.canBypassPlayer.contains(uuid);

        if (canBypass) {
            ProtectionsManager.canBypassPlayer.remove(uuid);
            MessagesManager.sendMessage(player, Component.text("Vous avez désactivé le bypass des claims"), Prefix.STAFF, MessageType.SUCCESS, false);
        } else {
            ProtectionsManager.canBypassPlayer.add(uuid);
            MessagesManager.sendMessage(player, Component.text("Vous avez activé le bypass des claims"), Prefix.STAFF, MessageType.SUCCESS, false);

        }
    }

    @Subcommand("freeclaim add")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.add")
    public void freeClaimAdd(@Named("player") Player player, @Named("claim") int claim) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.updateFreeClaims(claim);
    }

    @Subcommand("freeclaim remove")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimRemove(@Named("player") Player player, @Named("claim") int claim) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.updateFreeClaims(-claim);
    }

    @Subcommand("freeclaim delete")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimDelete(@Named("player") Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(player, Component.text("La ville n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        city.updateFreeClaims(-city.getFreeClaims());
    }
}
