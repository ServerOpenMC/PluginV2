package fr.openmc.core.features.corporation.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.corporation.menu.company.CompanyBaltopMenu;
import fr.openmc.core.features.corporation.menu.company.CompanyMenu;
import fr.openmc.core.features.corporation.menu.company.CompanySearchMenu;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"company", "entreprise", "enterprise"})
@Description("Gestion des entreprises")
@CommandPermission("ayw.command.company")
public class CompanyCommand {

    private final CompanyManager manager = CompanyManager.getInstance();
    private final PlayerShopManager playerShopManager = PlayerShopManager.getInstance();

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
        player.sendMessage("Usage : /company <help | baltop | balance | create | teamCreate | menu | search | apply | deny | accept | withdraw | deposit | setcut | leave | fire | owner | liquidate | perms>");
    }

    @Subcommand("help")
    @Description("explique comment marche une entreprise")
    public void help(Player player) {
        //TODO mettre un message pour expliquer
    }

    @Subcommand("apply")
    @Description("Postuler dans une entreprise")
    public void apply(Player player, @Named("name") String name) {
        if (!manager.companyExists(name)) {
            player.sendMessage("§cL'entreprise n'existe pas !");
            return;
        }
        if (manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous êtes déjà dans une entreprise !");
            return;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous ne pouvez pas postuler pour une entreprise si vous possédez un shop !");
            return;
        }
        Company company = manager.getCompany(name);
        manager.applyToCompany(player.getUniqueId(), company);
        player.sendMessage("§aVous avez postulé pour l'entreprise " + name + " !");
        company.broadCastOwner("§a" + player.getName() + " a postulé pour rejoindre l'entreprise !");
    }

    @Subcommand("accept")
    @Description("Accepter une candidature")
    public void accept(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.HIRINGER)) {
            player.sendMessage("§cVous n'avez pas la permission d'embaucher dans l'entreprise !");
            return;
        }
        if (!manager.hasPendingApplicationFor(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage("§cLe joueur n'a pas postulé pour votre entreprise !");
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        manager.acceptApplication(target.getUniqueId(), company);
        player.sendMessage("§aVous avez accepté la candidature de " + target.getName() + " !");
        target.sendMessage("§aVotre candidature pour l'entreprise <<" + company.getName() + ">> a été acceptée !");
    }

    @Subcommand("deny")
    @Description("Refuser une candidature")
    public void deny(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.HIRINGER)) {
            player.sendMessage("§cVous n'avez pas la permission d'embaucher dans l'entreprise !");
            return;
        }
        if (!manager.hasPendingApplicationFor(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage("§cLe joueur n'a pas postulé pour votre entreprise !");
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        manager.denyApplication(target.getUniqueId());
        player.sendMessage("§aVous avez refusé la candidature de " + target.getName() + " !");
        target.sendMessage("§cVotre candidature pour la entreprise <<" + company.getName() + ">> a été refusée !");
    }

    @Subcommand("search")
    @Description("Rechercher une entreprise")
    public void search(Player player) {
        CompanySearchMenu menu = new CompanySearchMenu(player);
        menu.open();
    }

    @Subcommand("liquidate")
    @Description("Liquider une entreprise")
    public void liquidate(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes dans aucune entreprise !");
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        if (!company.hasPermission(player.getUniqueId(), CorpPermission.LIQUIDATESHOP)) {
            player.sendMessage("§cVous n'avez pas l'autorisation de liquider dans l'entreprise !");
            return;
        }
        if (!manager.liquidateCompany(company)) {
            player.sendMessage("§cL'entreprise ne peut pas être liquidée car elle possède encore de l'argent ou des shops (merci de withdraw ou de supprimer vos shops)!");
            return;
        }
        player.sendMessage("§aL'entreprise a été liquidée avec succès !");
    }

    @Subcommand("leave")
    @Description("Quitter une entreprise")
    public void leave(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        MethodState leaveResult = manager.leaveCompany(player.getUniqueId());
        if (leaveResult == MethodState.FAILURE) {
            player.sendMessage("§cMerci de transférer l'ownership avant de quitter la team !");
            return;
        }
        if (leaveResult == MethodState.WARNING) {
            player.sendMessage("§cVous êtes le dernier a quitter et l'entreprise ne peut pas être liquidée car elle possède encore de l'argent ou des shops (merci de retirer l'argent ou de supprimer vos shops)!");
            return;
        }
        if (leaveResult == MethodState.SPECIAL) {
            player.sendMessage("§cLe propriétaire de la ville doit liquider ou quitter l'entreprise pour que vous puissiez ne plus en faire partie ! Ou vous pouvez quitter votre ville pour quitter l'entreprise !");
            return;
        }
        player.sendMessage("§aVous avez quitté l'entreprise !");
    }

    @Subcommand("fire")
    @Description("Renvoyer un membre de l'entreprise")
    public void fire(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.FIRE)) {
            player.sendMessage("§cVous n'avez l'autorisation de virer dans l'entreprise !");
            return;
        }
        if (!manager.isMerchantOfCompany(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage("§cCe marchand n'est pas trouvable dans l'entreprise !");
            return;
        }
        manager.getCompany(player.getUniqueId()).fireMerchant(target.getUniqueId());
        player.sendMessage("§aVous avez renvoyé " + target.getName() + " de l'entreprise !");
    }

    @Subcommand("baltop")
    @Description("Afficher le top des entreprises")
    public void baltop(Player player) {
        CompanyBaltopMenu menu = new CompanyBaltopMenu(player);
        menu.open();
    }

    @Subcommand("balance")
    @Description("Afficher le solde de l'entreprise")
    public void balance(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        player.sendMessage("§aSolde de l'entreprise : " + manager.getCompany(player.getUniqueId()).getBalance() + "€");
    }

    @Subcommand("setcut")
    @Description("Définir la part de l'entreprise lors d'une vente")
    public void setCut(Player player, @Named("cut") double cut) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.SETCUT)) {
            player.sendMessage("§cVous n'êtes pas le propriétaire haut-gradé de l'entreprise !");
            return;
        }
        if (cut < 0 || cut > 100) {
            player.sendMessage("§cLa part doit être comprise entre 0 et 100 !");
            return;
        }
        manager.getCompany(player.getUniqueId()).setCut(cut / 100);
        player.sendMessage("§aVous avez défini la part de l'entreprise à " + cut + "% !");
    }

    // seul le joueur est propriétaire
    @Subcommand("create")
    @Description("Créer une entreprise")
    public void createCompany(Player player, @Named("name") String name) {
        if (!check(player, name, false)) return;
        manager.createCompany(name, new CompanyOwner(player.getUniqueId()), false);
        player.sendMessage("§aL'entreprise " + name + " a été créée avec succès !");
    }

    // les membres de la ville sont propriétaires
    @Subcommand("cityCreate")
    @Description("Créer une entreprise de ville")
    public void createTeamCompany(Player player, @Named("name") String name) {
        if (!check(player, name, true)) return;
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.INFO, false);
            return;
        }
        if (player.getUniqueId() != city.getPlayerWith(CPermission.OWNER)) {
            player.sendMessage("§cVous ne pouvez pas créer d'entreprise au nom de votre ville sans en être l'owner");
            return;
        }
        manager.createCompany(name, new CompanyOwner(CityManager.getPlayerCity(player.getUniqueId())), true);
        player.sendMessage("§aL'entreprise " + name + " a été créée avec succès !");
    }

    @Subcommand("menu")
    @Description("Ouvrir le menu de l'entreprise")
    public void openMenu(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        CompanyMenu menu = new CompanyMenu(player, manager.getCompany(player.getUniqueId()), false);
        menu.open();
    }

    @Subcommand("withdraw")
    @Description("Retirer de l'argent de l'entreprise")
    public void withdraw(Player player, @Named("amount") double amount) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.WITHDRAW)) {
            player.sendMessage("§cVous n'avez pas la permission de retirer de l'argent dans l'entreprise");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).withdraw(amount, player, "Retrait")) {
            player.sendMessage("§cVous n'avez pas assez d'argent dans la banque d'entreprise !");
            return;
        }
        player.sendMessage("§aVous avez retiré " + amount + "€ de l'entreprise !");
    }

    @Subcommand("deposit")
    @Description("Déposer de l'argent dans l'entreprise")
    public void deposit(Player player, @Named("amount") double amount) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.DEPOSIT)){
            player.sendMessage("§cVous n'avez pas la permission d'ajouter de l'argent dans l'entreprise");
            return;
        }

        if (!manager.getCompany(player.getUniqueId()).deposit(amount, player, "Dépôt")) {
            player.sendMessage("§cVous n'avez pas assez d'argent sur vous !");
            return;
        }
        player.sendMessage("§aVous avez déposé " + amount + "€ dans l'entreprise !");
    }

    @Subcommand("owner")
    @Description("Transférer la propriété de l'entreprise")
    public void transferOwner(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous n'êtes pas dans une entreprise !");
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isUniqueOwner(player.getUniqueId())) {
            player.sendMessage("§cVous n'etes pas le propriétaire haut gradé de l'entreprise !");
            return;
        }
        if (!manager.isMerchantOfCompany(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            player.sendMessage("§cLe joueur ne fait pas partie de l'entreprise !");
            return;
        }
        manager.getCompany(player.getUniqueId()).setOwner(target.getUniqueId());
        player.sendMessage("§aVous avez transféré la propriété de l'entreprise à " + target.getName());
    }

    // add permissions
    @Subcommand({"permission SUPERIOR give", "perms SUPERIOR give"})
    @Description("Donner la permission SUPERIOR au joueur")
    void giveSuperior(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.SUPERIOR)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.SUPERIOR);
        sender.sendMessage("Permission " + CorpPermission.SUPERIOR.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.SUPERIOR.name());
    }

    @Subcommand({"permission SETCUT give", "perms SETCUT give"})
    @Description("Donner la permission setCut au joueur")
    void giveSetCut(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.SETCUT)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.SETCUT);
        sender.sendMessage("Permission " + CorpPermission.SETCUT.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.SETCUT.name());
    }

    @Subcommand({"permission INVITE give", "perms INVITE give"})
    @Description("Donner la permission Invite au joueur")
    void giveInvite(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.INVITE)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.INVITE);
        sender.sendMessage("Permission " + CorpPermission.INVITE.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.INVITE.name());
    }

    @Subcommand({"permission FIRE give", "perms FIRE give"})
    @Description("Donner la permission Fire au joueur")
    void giveFire(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.FIRE)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.FIRE);
        sender.sendMessage("Permission " + CorpPermission.FIRE.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.FIRE.name());
    }

    @Subcommand({"permission SUPPLY give", "perms SUPPLY give"})
    @Description("Donner la permission Supply au joueur")
    void giveSupply(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.SUPPLY)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.SUPPLY);
        sender.sendMessage("Permission " + CorpPermission.SUPPLY.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.SUPPLY.name());
    }

    @Subcommand({"permission LIQUIDATESHOP give", "perms LIQUIDATESHOP give"})
    @Description("Donner la permission Liquider un shop au joueur")
    void giveLiquidateShop(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.LIQUIDATESHOP)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.LIQUIDATESHOP);
        sender.sendMessage("Permission " + CorpPermission.LIQUIDATESHOP.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.LIQUIDATESHOP.name());
    }

    @Subcommand({"permission CREATESHOP give", "perms CREATESHOP give"})
    @Description("Donner la permission Créer un shop au joueur")
    void giveCreateShop(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.CREATESHOP)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.CREATESHOP);
        sender.sendMessage("Permission " + CorpPermission.CREATESHOP.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.CREATESHOP.name());
    }

    @Subcommand({"permission DELETESHOP give", "perms DELETESHOP give"})
    @Description("Donner la permission Supprimer un shop au joueur")
    void giveDeleteShop(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.DELETESHOP)){
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.DELETESHOP);
        sender.sendMessage("Permission " + CorpPermission.DELETESHOP.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.DELETESHOP.name());
    }

    @Subcommand({"permission HIRINGER give", "perms HIRINGER give"})
    @Description("Donner la permission HIRINGER au joueur")
    void giveHiringer(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.HIRINGER)) {
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.HIRINGER);
        sender.sendMessage("Permission " + CorpPermission.HIRINGER.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.HIRINGER.name() + ".");
    }

    @Subcommand({"permission DEPOSIT give", "perms DEPOSIT give"})
    @Description("Donner la permission DEPOSIT au joueur")
    void giveDeposit(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.DEPOSIT)) {
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.DEPOSIT);
        sender.sendMessage("Permission " + CorpPermission.DEPOSIT.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.DEPOSIT.name() + ".");
    }

    @Subcommand({"permission WITHDRAW give", "perms WITHDRAW give"})
    @Description("Donner la permission WITHDRAW au joueur")
    void giveWithdraw(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.WITHDRAW)) {
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.WITHDRAW);
        sender.sendMessage("Permission " + CorpPermission.WITHDRAW.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.WITHDRAW.name() + ".");
    }

    @Subcommand({"permission SELLER give", "perms SELLER give"})
    @Description("Donner la permission SELLER au joueur")
    void giveSeller(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !");
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.SELLER)) {
            sender.sendMessage("Ce joueur a déjà cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.SELLER);
        sender.sendMessage("Permission " + CorpPermission.SELLER.name() + " ajoutée au joueur.");
        target.sendMessage("Vous avez reçu la permission " + CorpPermission.SELLER.name() + ".");
    }


    //remove permissions
    // only the owner or the owners can remove that permission
    @Subcommand({"permission SUPERIOR remove", "perms SUPERIOR remove"})
    @Description("Retire la permission SUPERIOR au joueur")
    void removeSuperior(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())){
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.OWNER)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.SUPERIOR)){
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.SUPERIOR);
        sender.sendMessage("Permission " + CorpPermission.SUPERIOR.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.SUPERIOR.name());
    }

    @Subcommand({"permission SETCUT remove", "perms SETCUT remove"})
    @Description("Retirer la permission setCut au joueur")
    void removeSetCut(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.SETCUT)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.SETCUT);
        sender.sendMessage("Permission " + CorpPermission.SETCUT.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.SETCUT.name());
    }

    @Subcommand({"permission INVITE remove", "perms INVITE remove"})
    @Description("Retirer la permission Invite au joueur")
    void removeInvite(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.INVITE)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.INVITE);
        sender.sendMessage("Permission " + CorpPermission.INVITE.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.INVITE.name());
    }

    @Subcommand({"permission FIRE remove", "perms FIRE remove"})
    @Description("Retirer la permission Fire au joueur")
    void removeFire(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.FIRE)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.FIRE);
        sender.sendMessage("Permission " + CorpPermission.FIRE.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.FIRE.name());
    }

    @Subcommand({"permission SUPPLY remove", "perms SUPPLY remove"})
    @Description("Retirer la permission Supply au joueur")
    void removeSupply(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.SUPPLY)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.SUPPLY);
        sender.sendMessage("Permission " + CorpPermission.SUPPLY.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.SUPPLY.name());
    }

    @Subcommand({"permission LIQUIDATESHOP remove", "perms LIQUIDATESHOP remove"})
    @Description("Retirer la permission Liquider un shop au joueur")
    void removeLiquidateShop(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.LIQUIDATESHOP)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.LIQUIDATESHOP);
        sender.sendMessage("Permission " + CorpPermission.LIQUIDATESHOP.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.LIQUIDATESHOP.name());
    }

    @Subcommand({"permission CREATESHOP remove", "perms CREATESHOP remove"})
    @Description("Retirer la permission Créer un shop au joueur")
    void removeCreateShop(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.CREATESHOP)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.CREATESHOP);
        sender.sendMessage("Permission " + CorpPermission.CREATESHOP.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.CREATESHOP.name());
    }

    @Subcommand({"permission DELETESHOP remove", "perms DELETESHOP remove"})
    @Description("Retirer la permission Supprimer un shop au joueur")
    void removeDeleteShop(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.DELETESHOP)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.DELETESHOP);
        sender.sendMessage("Permission " + CorpPermission.DELETESHOP.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.DELETESHOP.name());
    }

    @Subcommand({"permission HIRINGER remove", "perms HIRINGER remove"})
    @Description("Retirer la permission HIRINGER au joueur")
    void removeHiringer(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.HIRINGER)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.HIRINGER);
        sender.sendMessage("Permission " + CorpPermission.HIRINGER.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.HIRINGER.name() + ".");
    }

    @Subcommand({"permission DEPOSIT remove", "perms DEPOSIT remove"})
    @Description("Retirer la permission DEPOSIT au joueur")
    void removeDeposit(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.DEPOSIT)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.DEPOSIT);
        sender.sendMessage("Permission " + CorpPermission.DEPOSIT.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.DEPOSIT.name() + ".");
    }

    @Subcommand({"permission WITHDRAW remove", "perms WITHDRAW remove"})
    @Description("Retirer la permission WITHDRAW au joueur")
    void removeWithdraw(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.WITHDRAW)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.WITHDRAW);
        sender.sendMessage("Permission " + CorpPermission.WITHDRAW.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.WITHDRAW.name() + ".");
    }

    @Subcommand({"permission SELLER remove", "perms SELLER remove"})
    @Description("Retirer la permission SELLER au joueur")
    void removeSeller(@Named("target") Player target, Player sender) {
        Company company = manager.getCompany(target.getUniqueId());
        if (company != manager.getCompany(sender.getUniqueId())) {
            sender.sendMessage("Ce joueur n'est pas dans votre entreprise.");
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            sender.sendMessage("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !");
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.SELLER)) {
            sender.sendMessage("Ce joueur n'a pas cette permission.");
            return;
        }
        company.removePermission(target.getUniqueId(), CorpPermission.SELLER);
        sender.sendMessage("Permission " + CorpPermission.SELLER.name() + " retirée au joueur.");
        target.sendMessage("Vous avez perdu la permission " + CorpPermission.SELLER.name() + ".");
    }

    private boolean check(Player player, String name, boolean teamCreate) {
        if (manager.isInCompany(player.getUniqueId())) {
            player.sendMessage("§cVous êtes déjà dans une entreprise !");
            return false;
        }
        if (name.length() < 3 || name.length() > 16){
            player.sendMessage("§cLe nom de l'entreprise doit faire entre 3 et 16 caractères !");
            return false;
        }
        if (manager.companyExists(name)) {
            player.sendMessage("§cUne entreprise avec ce nom existe déjà !");
            return false;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            player.sendMessage("§cVous ne pouvez pas créer d'entreprise si vous possédez un shop !");
            return false;
        }
        if (teamCreate) {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city!=null) {
                for (UUID cityMember : city.getMembers()) {
                    if (playerShopManager.hasShop(cityMember)) {
                        player.sendMessage("§cVous ne pouvez pas créer d'entreprise si un membre de votre ville possède un shop !");
                        return false;
                    }
                    if (manager.isInCompany(cityMember)) {
                        if (Bukkit.getPlayer(cityMember)==null){
                            player.sendMessage("§cUn membre de la ville est déjà dans une entreprise ! Ce membre est déconnecté");
                        } else {
                            player.sendMessage("§cUn membre de la ville est déjà dans une entreprise ! Ce membre est : " + Bukkit.getPlayer(cityMember).getName());
                        }
                        return false;
                    }
                }
            } else {
                player.sendMessage("§cVous ne pouvez pas créer d'entreprise car vous n'avez pas de ville !");
                return false;
            }
        }
        return true;
    }

}
