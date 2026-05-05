package fr.openmc.core.features.city.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.actions.CityTransferAction;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command("admcity")
@CommandPermission("omc.admins.commands.admincity")
public class AdminCityCommands {
    @Subcommand("deleteCity")
    @CommandPermission("omc.admins.commands.admincity.deleteCity")
    void deleteCity(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityManager.deleteCity(city);
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.delete.success"), Prefix.STAFF, MessageType.SUCCESS, false);
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
                TranslationManager.translation("feature.city.admin.commands.list.header", Component.text(page), Component.text(maxPage)).color(NamedTextColor.GOLD),
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
                    .append(TranslationManager.translation("feature.city.admin.commands.list.copy_label")
                            .color(NamedTextColor.GREEN)
                            .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                                    TranslationManager.translation("feature.city.admin.commands.list.copy_uuid_hover"))
                            )
                            .clickEvent(ClickEvent.copyToClipboard(cityUUID.toString()))
                    );

            player.sendMessage(line);
        });

        Component nav = Component.empty()
                .append(page > 1
                        ? TranslationManager.translation("feature.city.admin.commands.list.nav.prev").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/admcity list " + (page - 1)))
                        : Component.text("       "))
                .append(Component.text("    "))
                .append(page < maxPage
                        ? TranslationManager.translation("feature.city.admin.commands.list.nav.next").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/admcity list " + (page + 1)))
                        : Component.text("      "));

        player.sendMessage(nav);
    }

    @Subcommand("info")
    @CommandPermission("omc.admins.commands.admincity.info")
    void info(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.info.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        new CityListDetailsMenu(player, city).open();
    }

    @Subcommand("rename")
    @CommandPermission("omc.admins.commands.admincity.rename")
    void rename(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("nouveau nom") String newName
    ) {
        City newNameCity = CityManager.getCityByName(newName);
        if (newNameCity != null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.rename.name_already_used"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        city.rename(newName);

        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.rename.success"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("nouveau propriétaire") @SuggestWith(OnlinePlayerAutoComplete.class) Player newOwner) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        CityTransferAction.transfer(player, city, newOwner);
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("balance") double newBalance
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.setBalance(newBalance);
	    MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.balance.set_success"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.city.admin.commands.balance.get_success",
                Component.text(city.getBalance() + EconomyManager.getEconomyIcon())
        ), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("addPlayer")
    @CommandPermission("omc.admins.commands.admincity.addplayer")
    void add(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player newMember) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getPlayerCity(newMember.getUniqueId()) != null) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.add_player.already_in_city"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.addPlayer(newMember.getUniqueId());
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.add_player.success"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.admincity.remove")
    void remove(
            Player player,
            @Named("nom de ville") @SuggestWith(OnlinePlayerAutoComplete.class) Player member
    ) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.remove_player.not_in_city"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(member.getUniqueId(), CityPermission.OWNER)) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.remove_player.is_owner"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.removePlayer(member.getUniqueId());
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.remove_player.success"), Prefix.STAFF, MessageType.SUCCESS, false);
    }

    @Subcommand("getcity")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(
            Player player,
            @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player member
    ) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.getcity.not_in_city"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.city.admin.commands.getcity.success",
                Component.text(city.getName()),
                Component.text(city.getUniqueId().toString())
        ), Prefix.STAFF, MessageType.INFO, false);
    }

    @Subcommand("claim bypass")
    @CommandPermission("omc.admins.commands.admincity.claim.bypass")
    public void bypass(Player player) {
        UUID uuid = player.getUniqueId();
        boolean canBypass = ProtectionsManager.canBypassPlayer.contains(uuid);

        if (canBypass) {
            ProtectionsManager.canBypassPlayer.remove(uuid);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.claim_bypass.disabled"), Prefix.STAFF, MessageType.SUCCESS, false);
        } else {
            ProtectionsManager.canBypassPlayer.add(uuid);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.admin.commands.claim_bypass.enabled"), Prefix.STAFF, MessageType.SUCCESS, false);

        }
    }

    @Subcommand("freeclaim add")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.add")
    public void freeClaimAdd(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("claim") int claim) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }
        city.updateFreeClaims(claim);
    }

    @Subcommand("freeclaim remove")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimRemove(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("claim") int claim
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.updateFreeClaims(-claim);
    }

    @Subcommand("freeclaim delete")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimDelete(
            Player player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.not_found"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.updateFreeClaims(-city.getFreeClaims());
    }
}
