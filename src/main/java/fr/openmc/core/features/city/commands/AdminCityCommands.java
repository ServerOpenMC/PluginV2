package fr.openmc.core.features.city.commands;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.actions.CityTransferAction;
import fr.openmc.core.features.city.commands.autocomplete.CityNameAutoComplete;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.bedrock.CharRemplacementUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }

        CityManager.deleteCity(city);
        player.message().send(TranslationManager.translation("feature.city.admin.commands.delete.success"), Prefix.STAFF);
    }

    private static final int PER_PAGE = 10;

    @Subcommand("list")
    @CommandPermission("omc.admins.commands.admincity.list")
    void list(OMCPlayer player) {
        List<City> all = new ArrayList<>(CityManager.getCities());

        all.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        int page = 1;

        int total = all.size();
        int maxPage = (int) Math.ceil(total / (double) PER_PAGE);
        if (page > maxPage) page = maxPage;

        int start = (page - 1) * PER_PAGE;
        int end = Math.min(start + PER_PAGE, total);
        List<City> sub = all.subList(start, end);
        player.message().send(
                TranslationManager.translation("feature.city.admin.commands.list.header", Component.text(page), Component.text(maxPage)).color(NamedTextColor.GOLD),
                Prefix.STAFF
        );

        sub.forEach(city -> {
            UUID cityUUID = city.getUniqueId();
            String name = city.getName();

            Component line = Component.text("- ")
                    .append(Component.text(cityUUID.toString()).color(NamedTextColor.GRAY))
                    .append(Component.text(" " + CharRemplacementUtils.getPointChar(player) + " "))
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
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            player.message().sendError(TranslationManager.translation("feature.city.admin.commands.info.not_found"), Prefix.STAFF);
            return;
        }

        new CityListDetailsMenu(player, city).open();
    }

    @Subcommand("rename")
    @CommandPermission("omc.admins.commands.admincity.rename")
    void rename(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("nouveau nom") String newName
    ) {
        City newNameCity = CityManager.getCityByName(newName);
        if (newNameCity != null) {
            player.message().sendError(TranslationManager.translation("feature.city.admin.commands.rename.name_already_used"), Prefix.STAFF);
            return;
        }

        City city = CityManager.getCityByName(name);
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }
        city.rename(newName);

        player.message().send(TranslationManager.translation("feature.city.admin.commands.rename.success"), Prefix.STAFF);
    }

    @Subcommand("setOwner")
    @CommandPermission("omc.admins.commands.admincity.setOwner")
    void setOwner(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("nouveau propriétaire") @SuggestWith(OnlinePlayerAutoComplete.class) OMCOfflinePlayer newOwner) {
        City city = CityManager.getCityByName(name);

        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }

        CityTransferAction.transfer(player, city, newOwner);
    }

    @Subcommand("setBalance")
    @CommandPermission("omc.admins.commands.admincity.setBalance")
    void setBalance(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("balance") double newBalance
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }

        city.setBalance(newBalance);
        player.message().send(TranslationManager.translation("feature.city.admin.commands.balance.set_success"), Prefix.STAFF);
    }

    @Subcommand("getBalance")
    @CommandPermission("omc.admins.commands.admincity.getBalance")
    void getBalance(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            player.message().send(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }

        player.message().send(TranslationManager.translation(
                "feature.city.admin.commands.balance.get_success",
                Component.text(city.getBalance() + EconomyManager.getEconomyIcon())
        ), Prefix.STAFF);
    }

    @Subcommand("addPlayer")
    @CommandPermission("omc.admins.commands.admincity.addplayer")
    void add(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) OMCOfflinePlayer newMember) {
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
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(OnlinePlayerAutoComplete.class) OMCOfflinePlayer member
    ) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            player.message().sendError(TranslationManager.translation("feature.city.admin.commands.remove_player.not_in_city"), Prefix.STAFF);
            return;
        }

        if (city.hasPermission(member.getUniqueId(), CityPermission.OWNER)) {
            player.message().sendError(TranslationManager.translation("feature.city.admin.commands.remove_player.is_owner"), Prefix.STAFF);
            return;
        }

        city.removePlayer(member.getUniqueId());
        player.message().send(TranslationManager.translation("feature.city.admin.commands.remove_player.success"), Prefix.STAFF);
    }

    @Subcommand("getcity")
    @CommandPermission("omc.admins.commands.admincity.getPlayer")
    void getPlayer(
            OMCPlayer player,
            @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) OMCOfflinePlayer member
    ) {
        City city = CityManager.getPlayerCity(member.getUniqueId());
        if (city == null) {
            player.message().sendError(TranslationManager.translation("feature.city.admin.commands.getcity.not_in_city"), Prefix.STAFF);
            return;
        }

        player.message().send(TranslationManager.translation(
                "feature.city.admin.commands.getcity.success",
                Component.text(city.getName()),
                Component.text(city.getUniqueId().toString())
        ), Prefix.STAFF);
    }

    @Subcommand("claim bypass")
    @CommandPermission("omc.admins.commands.admincity.claim.bypass")
    public void bypass(OMCPlayer player) {
        UUID uuid = player.getUniqueId();
        boolean canBypass = ProtectionsManager.canBypassPlayer.contains(uuid);

        if (canBypass) {
            ProtectionsManager.canBypassPlayer.remove(uuid);
            player.message().send(TranslationManager.translation("feature.city.admin.commands.claim_bypass.disabled"), Prefix.STAFF);
        } else {
            ProtectionsManager.canBypassPlayer.add(uuid);
            player.message().send(TranslationManager.translation("feature.city.admin.commands.claim_bypass.enabled"), Prefix.STAFF);
        }
    }

    @Subcommand("freeclaim add")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.add")
    public void freeClaimAdd(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("claim") int claim) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }
        city.updateFreeClaims(claim);
    }

    @Subcommand("freeclaim remove")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimRemove(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name,
            @Named("claim") int claim
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }

        city.updateFreeClaims(-claim);
    }

    @Subcommand("freeclaim delete")
    @CommandPermission("omc.admins.commands.admincity.freeclaim.remove")
    public void freeClaimDelete(
            OMCPlayer player,
            @Named("nom de ville") @SuggestWith(CityNameAutoComplete.class) String name
    ) {
        City city = CityManager.getCityByName(name);
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.not_found"), Prefix.STAFF);
            return;
        }

        city.updateFreeClaims(-city.getFreeClaims());
    }
}
