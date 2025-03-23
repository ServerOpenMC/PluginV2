package fr.openmc.core.features.corporation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.features.corporation.commands.CompanyCommand;
import fr.openmc.core.features.corporation.commands.ShopCommand;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.corporation.listener.ShopListener;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.Queue;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class CompanyManager {

    @Getter static CompanyManager instance;

    // Liste de toutes les entreprises créées
    private final List<Company> companies = new ArrayList<>();

    // File d'attente des candidatures en attente, avec une limite de 100
    private final Queue<UUID, Company> pendingApplications = new Queue<>(100);

    public CompanyManager () {
        instance = this;

        CommandsManager.getHandler().register(
                new CompanyCommand(),
                new ShopCommand()
        );

        OMCPlugin.registerEvents(
                new ShopListener()
        );
    }

//    public static void init_db(Connection conn) throws SQLException {
//        conn.prepareStatement("CREATE TABLE IF NOT EXISTS company (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, name VARCHAR();").executeUpdate();
//    }
//
//    public static HashMap<String, Integer> getAllFreeClaims() {
//        HashMap<String, Integer> freeClaims = new HashMap<>();
//
//        String query = "SELECT city_uuid, claim FROM free_claim";
//        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query);
//             ResultSet rs = statement.executeQuery()) {
//
//            while (rs.next()) {
//                String cityUuid = rs.getString("city_uuid");
//                int claim = rs.getInt("claim");
//                freeClaims.put(cityUuid, claim);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return freeClaims;
//    }
//
//    public static List<Mascot> getAllMascots() {
//        List<Mascot> mascots = new ArrayList<>();
//
//        String query = "SELECT city_uuid, mascot_uuid, level, immunity, immunity_time, alive FROM mascots";
//        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query);
//             ResultSet rs = statement.executeQuery()) {
//
//            while (rs.next()) {
//                String cityUuid = rs.getString("city_uuid");
//                String mascotUuid = rs.getString("mascot_uuid");
//                int level = rs.getInt("level");
//                boolean immunity = rs.getBoolean("immunity");
//                long immunity_time = rs.getLong("immunity_time");
//                boolean alive = rs.getBoolean("alive");
//                mascots.add(new Mascot(cityUuid, mascotUuid, level, immunity, immunity_time, alive)); // Ajouter à la liste
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return mascots;
//    }
//
//    public static void saveFreeClaims(HashMap<String, Integer> freeClaims){
//        String query = "INSERT INTO free_claim (city_uuid, claim) VALUES (?, ?) ON DUPLICATE KEY UPDATE claim = ?";
//        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
//            for (Map.Entry<String, Integer> entry : freeClaims.entrySet()) {
//                statement.setString(1, entry.getKey());
//                statement.setInt(2, entry.getValue());
//                statement.setInt(3, entry.getValue());
//                statement.addBatch();
//            }
//            statement.executeBatch();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static void saveMascots(List<Mascot> mascots) {
//        String query = "INSERT INTO mascots (city_uuid, mascot_uuid, level, immunity, immunity_time, alive) " +
//                "VALUES (?, ?, ?, ?, ?, ?) " +
//                "ON DUPLICATE KEY UPDATE mascot_uuid = ?, level = ?, immunity = ?, immunity_time = ?, alive = ?";
//
//        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
//            for (Mascot mascot : mascots) {
//
//                statement.setString(1, mascot.getCityUuid());
//                statement.setString(2, mascot.getMascotUuid());
//                statement.setInt(3, mascot.getLevel());
//                statement.setBoolean(4, mascot.isImmunity());
//                statement.setLong(5, mascot.getImmunity_time());
//                statement.setBoolean(6, mascot.isAlive());
//
//                statement.setString(7, mascot.getMascotUuid());
//                statement.setInt(8, mascot.getLevel());
//                statement.setBoolean(9, mascot.isImmunity());
//                statement.setLong(10, mascot.getImmunity_time());
//                statement.setBoolean(11, mascot.isAlive());
//
//                statement.addBatch();
//            }
//
//            statement.executeBatch();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // Crée une nouvelle entreprise et l'ajoute à la liste des entreprises existantes
    public void createCompany(String name, CompanyOwner owner) {
        if (name.length()>24) {
            MessagesManager.sendMessage(Bukkit.getPlayer(owner.getPlayer()),Component.text("Le nom de votre entreprise est trop long il ne doit contenir que 24 caractères"), Prefix.ENTREPRISE, MessageType.INFO, false);
            return;
        }
        companies.add(new Company(name, owner));
    }

    // Un joueur postule pour rejoindre une entreprise
    public void applyToCompany(UUID player, Company company) {
        pendingApplications.add(player, company);
    }

    // Accepte une candidature et ajoute le joueur en tant que marchand
    public void acceptApplication(UUID player, Company company) {
        company.addMerchant(player, new MerchantData());
        pendingApplications.remove(player);
    }

    // Vérifie si un joueur a une candidature en attente pour une entreprise donnée
    public boolean hasPendingApplicationFor(UUID player, Company company) {
        return pendingApplications.get(player) == company;
    }

    // Refuse une candidature en attente
    public void denyApplication(UUID player) {
        pendingApplications.remove(player);
    }

    // Retourne la liste des joueurs ayant une candidature en attente pour une entreprise donnée
    public List<UUID> getPendingApplications(Company company) {
        List<UUID> players = new ArrayList<>();
        for (UUID player : pendingApplications.getQueue().keySet()) {
            if (hasPendingApplicationFor(player, company)) {
                players.add(player);
            }
        }
        return players;
    }

    // Liquidation d'une entreprise (suppression si conditions remplies)
    public boolean liquidateCompany(Company company) {
        // L'entreprise ne peut pas être liquidée si elle a encore des marchands
        if (!company.getMerchants().isEmpty()) {
            fireAllMerchants(company);
        }
        // L'entreprise ne peut pas être liquidée si elle a encore des fonds
        if (company.getBalance() > 0) {
            return false;
        }
        // L'entreprise ne peut pas être liquidée si elle possède encore des magasins
        if (!company.getShops().isEmpty()) {
            return false;
        }

        // Suppression de l'entreprise
        companies.remove(company);
        return true;
    }

    // Renvoyer tous les marchands d'une entreprise
    public void fireAllMerchants(Company company) {
        for (UUID uuid : company.getMerchants().keySet()) {
            company.fireMerchant(uuid);
        }
    }

    // Permet à un joueur de quitter une entreprise (différents cas gérés)
    public MethodState leaveCompany(UUID player) {
        Company company = getCompany(player);

        if (company.isOwner(player)) {
            // Si le joueur est propriétaire et qu'il n'y a pas d'autres marchands
            if (company.getMerchants().isEmpty()) {
                if (company.isUniqueOwner(player)) {
                    if (!liquidateCompany(company)) {
                        return MethodState.WARNING;
                    }
                    return MethodState.SUCCESS;
                }
                return MethodState.SPECIAL;
            }
            return MethodState.FAILURE;
        }

        // Si ce n'est pas le propriétaire qui quitte, on supprime le marchand
        MerchantData data = company.getMerchant(player);
        company.removeMerchant(player);

        // Si plus aucun membre n'est présent après le départ, l'entreprise est liquidée
        if (company.getAllMembers().isEmpty()) {
            if (!liquidateCompany(company)) {
                company.addMerchant(player, data); // Annulation si liquidation impossible
                return MethodState.WARNING;
            }
        }
        return MethodState.SUCCESS;
    }

    // Trouve une entreprise par son nom
    public Company getCompany(String name) {
        for (Company company : companies) {
            if (company.getName().equals(name)) {
                return company;
            }
        }
        return null;
    }

    // Trouve un magasin par son UUID, quel que soit son propriétaire
    public Shop getAnyShop(UUID shopUUID) {
        for (Company company : companies) {
            Shop shop = company.getShop(shopUUID);
            if (shop != null) {
                return shop;
            }
        }
        return null;
    }

    // Trouve l'entreprise d'un joueur (en tant que marchand ou propriétaire)
    public Company getCompany(UUID player) {
        for (Company company : companies) {
            if (company.getMerchants().containsKey(player)) {
                return company;
            }
            CompanyOwner owner = company.getOwner();
            if (owner.isPlayer() && owner.getPlayer().equals(player)) {
                return company;
            }
            if (owner.isTeam() && owner.getCity().getMembers().contains(player)) {
                return company;
            }
        }
        return null;
    }

    // Trouve l'entreprise associée à une ville donnée
    public Company getCompany(City city) {
        for (Company company : companies) {
            if (company.getOwner().getCity() != null && company.getOwner().getCity().equals(city)) {
                return company;
            }
        }
        return null;
    }

    // Vérifie si un joueur est dans une entreprise
    public boolean isInCompany(UUID player) {
        return getCompany(player) != null;
    }

    // Vérifie si un joueur est un marchand dans une entreprise donnée
    public boolean isMerchantOfCompany(UUID player, Company company) {
        return company.getMerchants().containsKey(player);
    }

    // Vérifie si une entreprise existe par son nom
    public boolean companyExists(String name) {
        return getCompany(name) != null;
    }
}