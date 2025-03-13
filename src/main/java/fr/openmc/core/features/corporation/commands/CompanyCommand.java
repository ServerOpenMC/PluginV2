package fr.openmc.core.features.corporation.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.corporation.menu.company.CompanyBaltopMenu;
import fr.openmc.core.features.corporation.menu.company.CompanyMenu;
import fr.openmc.core.features.corporation.menu.company.CompanySearchMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"company", "entreprise", "enterprise"})
@Description("Gestion des entreprises")
@CommandPermission("ayw.command.company")
public class CompanyCommand {

    private final CompanyManager manager;
    private final EconomyManager economyManager;
    private final ShopBlocksManager shopBlocksManager;
    private final PlayerShopManager playerShopManager;

    public CompanyCommand(CompanyManager manager, EconomyManager economyManager, ShopBlocksManager shopBlocksManager, PlayerShopManager playerShopManager) {
        this.manager = manager;
        this.economyManager = economyManager;
        this.shopBlocksManager = shopBlocksManager;
        this.playerShopManager = playerShopManager;
    }

    @DefaultFor("~")
    public void onCommand(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            search(player);
            return;
        }
        CompanyMenu menu = new CompanyMenu(player, manager.getCompany(player.getUniqueId()), false);
        menu.open();
    }

    @Subcommand("usage")
    @Description("Afficher l'utilisation des commandes d'une entreprise")
    public void usage(Player player) {
        player.sendMessage("Usage : /company <baltop | balance | create | teamCreate | menu | search | apply | deny | accept | withdraw | deposit | setcut | leave | fire | owner | liquidate>");
    }

    @Subcommand("apply")
    @Description("Postuler dans une entreprise")
    public void apply(Player player, @Named("name") String name) {
        if (!manager.companyExists(name)) {
            player.sendMessage(ChatColor.RED + "L'entreprise n'existe pas !");
            return;
        }
        if (manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans une entreprise !");
            return;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas postuler pour une entreprise si vous possédez un shop !");
            return;
        }
        Company company = manager.getCompany(name);
        manager.applyToCompany(player.getUniqueId(), company);
        player.sendMessage(ChatColor.GREEN + "Vous avez postulé pour l'entreprise " + name + " !");
        company.broadCastOwner(ChatColor.GREEN + player.getName() + " a postulé pour rejoindre l'entreprise !");
    }

    @Subcommand("accept")
    @Description("Accepter une candidature")
    public void accept(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas un des propriétaires de l'entreprise !");
            return;
        }
        if (!manager.hasPendingApplicationFor(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage(ChatColor.RED + "Le joueur n'a pas postulé pour votre entreprise !");
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        manager.acceptApplication(target.getUniqueId(), company);
        player.sendMessage(ChatColor.GREEN + "Vous avez accepté la candidature de " + target.getName() + " !");
        target.sendMessage(ChatColor.GREEN + "Votre candidature pour l'entreprise <<" + company.getName() + ">> a été acceptée !");
    }

    @Subcommand("deny")
    @Description("Refuser une candidature")
    public void deny(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas un des propriétaires de l'entreprise !");
            return;
        }
        if (!manager.hasPendingApplicationFor(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage(ChatColor.RED + "Le joueur n'a pas postulé pour votre entreprise !");
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        manager.denyApplication(target.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Vous avez refusé la candidature de " + target.getName() + " !");
        target.sendMessage(ChatColor.RED + "Votre candidature pour la entreprise <<" + company.getName() + ">> a été refusée !");
    }

    @Subcommand("search")
    @Description("Rechercher une entreprise")
    public void search(Player player) {
        CompanySearchMenu menu = new CompanySearchMenu(player, manager);
        menu.open();
    }

    @Subcommand("liquidate")
    @Description("Liquider une entreprise")
    public void liquidate(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes dans aucune entreprise !");
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        if (!company.isUniqueOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas le propriétaire haut-gradé de l'entreprise !");
            return;
        }
        if (!manager.liquidateCompany(company)) {
            player.sendMessage(ChatColor.RED + "L'entreprise ne peut pas être liquidée car elle possède encore de l'argent ou des shops (merci de withdraw ou de supprimer vos shops)!");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "L'entreprise a été liquidée avec succès !");
    }

    @Subcommand("leave")
    @Description("Quitter une entreprise")
    public void leave(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        MethodState leaveResult = manager.leaveCompany(player.getUniqueId());
        if (leaveResult == MethodState.FAILURE) {
            player.sendMessage(ChatColor.RED + "Merci de transférer l'ownership avant de quitter la team !");
            return;
        }
        if (leaveResult == MethodState.WARNING) {
            player.sendMessage(ChatColor.RED + "Vous êtes le dernier a quitter et l'entreprise ne peut pas être liquidée car elle possède encore de l'argent ou des shops (merci de retirer l'argent ou de supprimer vos shops)!");
            return;
        }
        if (leaveResult == MethodState.SPECIAL) {
            player.sendMessage(ChatColor.RED + "Le propriétaire de la team doit liquider ou quitter l'entreprise pour que vous puissiez ne plus en faire partie ! Ou vous pouvez quitter votre team pour quitter l'entreprise !");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Vous avez quitté l'entreprise !");
    }

    @Subcommand("fire")
    @Description("Renvoyer un membre de l'entreprise")
    public void fire(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas un des propriétaires de l'entreprise !");
            return;
        }
        if (!manager.isMerchantOfCompany(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage(ChatColor.RED + "Ce marchand n'est pas trouvable dans l'entreprise !");
            return;
        }
        manager.getCompany(player.getUniqueId()).fireMerchant(target.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Vous avez renvoyé " + target.getName() + " de l'entreprise !");
    }

    @Subcommand("baltop")
    @Description("Afficher le top des entreprises")
    public void baltop(Player player) {
        CompanyBaltopMenu menu = new CompanyBaltopMenu(player, manager);
        menu.open();
    }

    @Subcommand("balance")
    @Description("Afficher le solde de l'entreprise")
    public void balance(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Solde de l'entreprise : " + manager.getCompany(player.getUniqueId()).getBalance() + "€");
    }

    @Subcommand("setcut")
    @Description("Définir la part de l'entreprise lors d'une vente")
    public void setCut(Player player, @Named("cut") double cut) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isUniqueOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas le propriétaire haut-gradé de l'entreprise !");
            return;
        }
        if (cut < 0 || cut > 100) {
            player.sendMessage(ChatColor.RED + "La part doit être comprise entre 0 et 100 !");
            return;
        }
        manager.getCompany(player.getUniqueId()).setCut(cut / 100);
        player.sendMessage(ChatColor.GREEN + "Vous avez défini la part de l'entreprise à " + cut + "% !");
    }

    @Subcommand("create")
    @Description("Créer une entreprise")
    public void createCompany(Player player, @Named("name") String name) {
        if (!check(player, name, false)) return;
        manager.createCompany(name, new CompanyOwner(player.getUniqueId()), economyManager, shopBlocksManager);
        player.sendMessage(ChatColor.GREEN + "L'entreprise " + name + " a été créée avec succès !");
    }

    @Subcommand("teamCreate")
    @Description("Créer une entreprise de team")
    public void createTeamCompany(Player player, @Named("name") String name) {
        if (!check(player, name, true)) return;
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.INFO, false);
            return;
        }
        if (player.getUniqueId() != city.getPlayerWith(CPermission.OWNER)) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas créer d'entreprise au nom de votre ville sans en être l'owner");
            return;
        }
        manager.createCompany(name, new CompanyOwner(CityManager.getPlayerCity(player.getUniqueId())), economyManager, shopBlocksManager);
        player.sendMessage(ChatColor.GREEN + "L'entreprise " + name + " a été créée avec succès !");
    }

    @Subcommand("menu")
    @Description("Ouvrir le menu de l'entreprise")
    public void openMenu(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        CompanyMenu menu = new CompanyMenu(player, manager.getCompany(player.getUniqueId()), false);
        menu.open();
    }

    @Subcommand("withdraw")
    @Description("Retirer de l'argent de l'entreprise")
    public void withdraw(Player player, @Named("amount") double amount) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous ne faites pas partie des propriétaires de l'entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).withdraw(amount, player, "Retrait", economyManager)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent dans la banque d'entreprise !");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Vous avez retiré " + amount + "€ de l'entreprise !");
    }

    @Subcommand("deposit")
    @Description("Déposer de l'argent dans l'entreprise")
    public void deposit(Player player, @Named("amount") double amount) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).deposit(amount, player, "Dépôt", economyManager)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent sur vous !");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Vous avez déposé " + amount + "€ dans l'entreprise !");
    }

    @Subcommand("owner")
    @Description("Transférer la propriété de l'entreprise")
    public void transferOwner(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isUniqueOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous n'etes pas le propriétaire haut gradé de l'entreprise !");
            return;
        }
        if (!manager.isMerchantOfCompany(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage(ChatColor.RED + "Le joueur ne fait pas partie de l'entreprise !");
            return;
        }
        manager.getCompany(player.getUniqueId()).setOwner(target.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Vous avez transféré la propriété de l'entreprise à " + target.getName());
    }

    private boolean check(Player player, String name, boolean teamCreate) {
        if (manager.isInCompany(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans une entreprise !");
            return false;
        }
        if (name.length() < 3 || name.length() > 16){
            player.sendMessage(ChatColor.RED + "Le nom de l'entreprise doit faire entre 3 et 16 caractères !");
            return false;
        }
        if (manager.companyExists(name)) {
            player.sendMessage(ChatColor.RED + "Une entreprise avec ce nom existe déjà !");
            return false;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas créer d'entreprise si vous possédez un shop !");
            return false;
        }
        if (teamCreate) {
            if (CityManager.getPlayerCity(player.getUniqueId())!=null) {
                City city = CityManager.getPlayerCity(player.getUniqueId());
                for (UUID cityMember : city.getMembers()) {
                    if (playerShopManager.hasShop(cityMember)) {
                        player.sendMessage(ChatColor.RED + "Vous ne pouvez pas créer d'entreprise si un membre de votre team possède un shop !");
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
