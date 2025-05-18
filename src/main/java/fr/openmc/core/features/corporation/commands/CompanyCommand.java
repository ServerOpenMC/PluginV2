package fr.openmc.core.features.corporation.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corporation.*;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.company.CompanyOwner;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.PlayerShopManager;
import fr.openmc.core.features.corporation.menu.company.CompanyBaltopMenu;
import fr.openmc.core.features.corporation.menu.company.CompanyMenu;
import fr.openmc.core.features.corporation.menu.company.CompanySearchMenu;
import fr.openmc.core.features.corporation.MethodState;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"company", "companies",  "entreprise", "enterprises"})
@Description("Gestion des entreprises")
@CommandPermission("omc.commands.company")
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

    @Subcommand("help")
    @Description("Explique comment marche une entreprise")
    @Cooldown(30)
    public void help(Player player) {
        MessagesManager.sendMessage(player, Component.text("""
            §6§lListe des commandes entreprise :
            
            §e▪ /company baltop§7 - Affiche les meilleures entreprises
            §e▪ /company balance§7 - Montre l'argent de votre entreprise
            §e▪ /company create§7 - Crée une nouvelle entreprise
            §e▪ /company menu§7 - Ouvre le menu de gestion de votre entreprise
            §e▪ /company search§7 - Recherche une entreprise
            §e▪ /company apply§7 - Postule pour une entreprise
            §e▪ /company deny§7 - Refuse une candidature
            §e▪ /company accept§7 - Accepte une candidature
            §e▪ /company withdraw§7 - Retire de l'argent de l'entreprise
            §e▪ /company deposit§7 - Dépose de l'argent dans l'entreprise
            §e▪ /company setcut§7 - Définit la part de revenu pour l'entreprise
            §e▪ /company leave§7 - Quitte l'entreprise
            §e▪ /company fire§7 - Vire un employé
            §e▪ /company owner§7 - Transfère la propriété de l'entreprise
            §e▪ /company liquidate§7 - Supprime l'entreprise
            §e▪ /company perms§7 - Gère les permissions des employés
            """),
                Prefix.ENTREPRISE, MessageType.INFO, false);
    }

    @Subcommand("apply")
    @Description("Postuler dans une entreprise")
    public void apply(Player player, @Named("name") String name) {
        if (!manager.companyExists(name)) {
            MessagesManager.sendMessage(player, Component.text("§cL'entreprise n'existe pas !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous êtes déjà dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas postuler pour une entreprise si vous possédez un shop !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        Company company = manager.getCompany(name);
        manager.applyToCompany(player.getUniqueId(), company);
        MessagesManager.sendMessage(player, Component.text("§aVous avez postulé pour l'entreprise " + name + " !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
        company.broadCastOwner("§a" + player.getName() + " a postulé pour rejoindre l'entreprise !");
    }

    @Subcommand("accept")
    @Description("Accepter une candidature")
    public void accept(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.HIRINGER)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission d'embaucher dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.hasPendingApplicationFor(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            MessagesManager.sendMessage(player, Component.text("§cLe joueur n'a pas postulé pour votre entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        manager.acceptApplication(target.getUniqueId(), company);
        MessagesManager.sendMessage(player, Component.text("§aVous avez accepté la candidature de " + target.getName() + " !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target, Component.text("§aVotre candidature pour l'entreprise §6§l" + company.getName() + "§r a été acceptée !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    @Subcommand("deny")
    @Description("Refuser une candidature")
    public void deny(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.HIRINGER)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission d'embaucher dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.hasPendingApplicationFor(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            MessagesManager.sendMessage(player, Component.text("§cLe joueur n'a pas postulé pour votre entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        manager.denyApplication(target.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("§aVous avez refusé la candidature de " + target.getName() + " !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target, Component.text("§cVotre candidature pour la entreprise §6§l" + company.getName() + "§r a été refusée !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
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
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes dans aucune entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        Company company = manager.getCompany(player.getUniqueId());
        if (!company.hasPermission(player.getUniqueId(), CorpPermission.LIQUIDATESHOP)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas l'autorisation de liquider dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.liquidateCompany(company)) {
            MessagesManager.sendMessage(player, Component.text("§cL'entreprise ne peut pas être liquidée car elle possède encore de l'argent ou des shops (merci de withdraw ou de supprimer vos shops)!"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§aL'entreprise a été liquidée avec succès !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    @Subcommand("leave")
    @Description("Quitter une entreprise")
    public void leave(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        MethodState leaveResult = manager.leaveCompany(player.getUniqueId());
        if (leaveResult == MethodState.FAILURE) {
            MessagesManager.sendMessage(player, Component.text("§cMerci de transférer l'ownership avant de quitter la team !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (leaveResult == MethodState.WARNING) {
            MessagesManager.sendMessage(player, Component.text("§cVous êtes le dernier a quitter et l'entreprise ne peut pas être liquidée car elle possède encore de l'argent ou des shops (merci de retirer l'argent ou de supprimer vos shops)!"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (leaveResult == MethodState.SPECIAL) {
            MessagesManager.sendMessage(player, Component.text("§cLe propriétaire de la ville doit liquider ou quitter l'entreprise pour que vous puissiez ne plus en faire partie ! Ou vous pouvez quitter votre ville pour quitter l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§aVous avez quitté l'entreprise !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    @Subcommand("fire")
    @Description("Renvoyer un membre de l'entreprise")
    public void fire(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.FIRE)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez l'autorisation de virer dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.isMerchantOfCompany(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            MessagesManager.sendMessage(player, Component.text("§cCe marchand n'est pas trouvable dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        manager.getCompany(player.getUniqueId()).fireMerchant(target.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("§aVous avez renvoyé " + target.getName() + " de l'entreprise !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
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
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§aSolde de l'entreprise : " + manager.getCompany(player.getUniqueId()).getBalance() + EconomyManager.getEconomyIcon()), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    // définit la part de l'entreprise sur tous ses shops
    @Subcommand("setcut")
    @Description("Définir la part de l'entreprise lors d'une vente")
    public void setCut(Player player, @Named("cut") double cut) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.SETCUT)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas le propriétaire haut-gradé de l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (cut < 0 || cut > 100) {
            MessagesManager.sendMessage(player, Component.text("§cLa part doit être comprise entre 0 et 100 !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        manager.getCompany(player.getUniqueId()).setCut(cut / 100);
        MessagesManager.sendMessage(player, Component.text("§aVous avez défini la part de l'entreprise à " + cut + "% !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    // seul le joueur est propriétaire
    @Subcommand("create")
    @Description("Créer une entreprise")
    public void createCompany(Player player, @Named("name") String name) {
        if (!check(player, name, false)) return;
        manager.createCompany(name, new CompanyOwner(player.getUniqueId()), false, null);
        MessagesManager.sendMessage(player, Component.text("§aL'entreprise " + name + " a été créée avec succès !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    // laissez en commentaire, le système d'entreprise de ville doit être refait

    // les membres de la ville sont propriétaires
//    @Subcommand("cityCreate")
//    @Description("Créer une entreprise de ville")
//    public void createTeamCompany(Player player, @Named("name") String name) {
//        if (!check(player, name, true)) return;
//        City city = CityManager.getPlayerCity(player.getUniqueId());
//        if (city==null){
//            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.INFO, false);
//            return;
//        }
//        if (player.getUniqueId() != city.getPlayerWith(CPermission.OWNER)) {
//            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas créer d'entreprise au nom de votre ville sans en être l'owner"), Prefix.ENTREPRISE, MessageType.INFO, false);
//            return;
//        }
//        manager.createCompany(name, new CompanyOwner(CityManager.getPlayerCity(player.getUniqueId())), true, null);
//        MessagesManager.sendMessage(player, Component.text("§aL'entreprise " + name + " a été créée avec succès !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
//    }

    @Subcommand("menu")
    @Description("Ouvrir le menu de l'entreprise")
    public void openMenu(Player player) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        CompanyMenu menu = new CompanyMenu(player, manager.getCompany(player.getUniqueId()), false);
        menu.open();
    }

    @Subcommand("withdraw")
    @Description("Retirer de l'argent de l'entreprise")
    public void withdraw(Player player, @Named("amount") double amount) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.WITHDRAW)) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission de retirer de l'argent dans l'entreprise"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).withdraw(amount, player, "Retrait")) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez d'argent dans la banque d'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§aVous avez retiré " + amount + "" + EconomyManager.getEconomyIcon() + " de l'entreprise !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    @Subcommand("deposit")
    @Description("Déposer de l'argent dans l'entreprise")
    public void deposit(Player player, @Named("amount") double amount) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).hasPermission(player.getUniqueId(), CorpPermission.DEPOSIT)){
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission d'ajouter de l'argent dans l'entreprise"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }

        if (!manager.getCompany(player.getUniqueId()).deposit(amount, player, "Dépôt")) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez d'argent sur vous !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        MessagesManager.sendMessage(player, Component.text("§aVous avez déposé " + amount + "" + EconomyManager.getEconomyIcon() + " dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    @Subcommand("owner")
    @Description("Transférer la propriété de l'entreprise")
    public void transferOwner(Player player, @Named("target") Player target) {
        if (!manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.getCompany(player.getUniqueId()).isUniqueOwner(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'etes pas le propriétaire haut gradé de l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!manager.isMerchantOfCompany(target.getUniqueId(), manager.getCompany(player.getUniqueId()))) {
            MessagesManager.sendMessage(player, Component.text("§cLe joueur ne fait pas partie de l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        manager.getCompany(player.getUniqueId()).setOwner(target.getUniqueId());
        MessagesManager.sendMessage(player, Component.text("§aVous avez transféré la propriété de l'entreprise à " + target.getName()), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    // add permissions
    @Subcommand({"permission give", "perms give"})
    @Description("Donner les permissions aux joueurs")
    @AutoComplete("@company_perms")
    void giveSuperior(Player sender,@Named("company_perms") String perms, @Named("target") Player target) {
        Company company = manager.getCompany(target.getUniqueId());
        if (!manager.isInCompany(sender.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (company != manager.getCompany(sender.getUniqueId())){
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans votre entreprise."), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            MessagesManager.sendMessage(sender, Component.text("Vous n'avez pas la permission d'ajouter des permissions dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (company.hasPermission(target.getUniqueId(), CorpPermission.valueOf(perms))){
            MessagesManager.sendMessage(sender, Component.text("Ce joueur a déjà cette permission."), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.valueOf(perms));
        MessagesManager.sendMessage(sender, Component.text("Permission §6§l" + CorpPermission.valueOf(perms).name() + "§r ajoutée au joueur."), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target, Component.text("Vous avez reçu la permission §6§l" + CorpPermission.valueOf(perms).name()), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }


    //remove permissions
    @Subcommand({"permission remove", "perms remove"})
    @Description("Retire les permissions aux joueurs")
    @AutoComplete("@company_perms")
    void removeSuperior(Player sender,@Named("company_perms") String perms, @Named("target") Player target) {
        Company company = manager.getCompany(target.getUniqueId());
        if (!manager.isInCompany(sender.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("§cVous n'êtes pas dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (company != manager.getCompany(sender.getUniqueId())){
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans votre entreprise."), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!company.hasPermission(sender.getUniqueId(), CorpPermission.SUPERIOR)) {
            MessagesManager.sendMessage(sender, Component.text("Vous n'avez pas la permission de retirer des permissions dans l'entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        if (!company.hasPermission(target.getUniqueId(), CorpPermission.valueOf(perms))){
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'a pas cette permission."), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        company.addPermission(target.getUniqueId(), CorpPermission.valueOf(perms));
        MessagesManager.sendMessage(sender, Component.text("Permission §6§l" + CorpPermission.valueOf(perms).name() + "§r retirée au joueur."), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target, Component.text("Vous avez perdu la permission §6§l" + CorpPermission.valueOf(perms).name()), Prefix.ENTREPRISE, MessageType.SUCCESS, false);
    }

    private boolean check(Player player, String name, boolean teamCreate) {
        if (manager.isInCompany(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous êtes déjà dans une entreprise !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return false;
        }
        if (name.length() < 3 || name.length() > 16){
            MessagesManager.sendMessage(player, Component.text("§cLe nom de l'entreprise doit faire entre 3 et 16 caractères !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return false;
        }
        if (manager.companyExists(name)) {
            MessagesManager.sendMessage(player, Component.text("§cUne entreprise avec ce nom existe déjà !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return false;
        }
        if (playerShopManager.hasShop(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas créer d'entreprise si vous possédez un shop !"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return false;
        }
        if (teamCreate) {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city!=null) {
                for (UUID cityMember : city.getMembers()) {
                    if (playerShopManager.hasShop(cityMember)) {
                        MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas créer d'entreprise si un membre de votre ville possède un shop !"), Prefix.ENTREPRISE, MessageType.INFO, false);
                        return false;
                    }
                    if (manager.isInCompany(cityMember)) {
                        if (Bukkit.getPlayer(cityMember)==null){
                            MessagesManager.sendMessage(player, Component.text("§cUn membre de la ville est déjà dans une entreprise ! Ce membre est déconnecté"), Prefix.ENTREPRISE, MessageType.INFO, false);
                        } else {
                            MessagesManager.sendMessage(player, Component.text("§cUn membre de la ville est déjà dans une entreprise ! Ce membre est : " + Bukkit.getPlayer(cityMember).getName()), Prefix.ENTREPRISE, MessageType.INFO, false);
                        }
                        return false;
                    }
                }
            } else {
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas créer d'entreprise car vous n'avez pas de ville !"), Prefix.ENTREPRISE, MessageType.INFO, false);
                return false;
            }
        }
        return true;
    }

}
