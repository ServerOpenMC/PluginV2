package fr.openmc.core.features.corporation;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.MethodState;
import fr.openmc.core.features.corporation.data.MerchantData;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.Queue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CompanyManager {

    private final List<Company> companies = new ArrayList<>();
    private final Queue<UUID, Company> pendingApplications = new Queue<>(100);

    public void createCompany(String name, CompanyOwner owner, EconomyManager economyManager, ShopBlocksManager shopBlocksManager) {
        companies.add(new Company(name, owner, economyManager, shopBlocksManager));
    }

    public void applyToCompany(UUID player, Company company) {
        pendingApplications.add(player, company);
    }

    public void acceptApplication(UUID player, Company company) {
        company.addMerchant(player, new MerchantData());
        pendingApplications.remove(player);
    }

    public boolean hasPendingApplicationFor(UUID player, Company company) {
        return pendingApplications.get(player) == company;
    }

    public void denyApplication(UUID player) {
        pendingApplications.remove(player);
    }

    public List<UUID> getPendingApplications(Company company) {
        List<UUID> players = new ArrayList<>();
        for (UUID player : pendingApplications.getQueue().keySet()) {
            if (hasPendingApplicationFor(player, company)) {
                players.add(player);
            }
        }
        return players;
    }

    public boolean liquidateCompany(Company company) {
        if (!company.getMerchants().isEmpty()) {
            fireAllMerchants(company);
        }
        if (company.getBalance() > 0) {
            return false;
        }
        if (!company.getShops().isEmpty()) {
            return false;
        }
        companies.remove(company);
        return true;
    }

    public void fireAllMerchants(Company company) {
        for (UUID uuid : company.getMerchants().keySet()) {
            company.fireMerchant(uuid);
        }
    }

    public MethodState leaveCompany(UUID player) {
        Company company = getCompany(player);
        if (company.isOwner(player)) {
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
        MerchantData data = company.getMerchant(player);
        company.removeMerchant(player);
        if (company.getAllMembers().isEmpty()) {
            if (!liquidateCompany(company)) {
                company.addMerchant(player, data);
                return MethodState.WARNING;
            }
        }
        return MethodState.SUCCESS;
    }

    public Company getCompany(String name) {
        for (Company company : companies) {
            if (company.getName().equals(name)) {
                return company;
            }
        }
        return null;
    }

    public Shop getAnyShop(UUID shopUUID) {
        for (Company company : companies) {
            Shop shop = company.getShop(shopUUID);
            if (shop != null) {
                return shop;
            }
        }
        return null;
    }

    public Company getCompany(UUID player) {
        for (Company company : companies) {
            if (company.getMerchants().containsKey(player)) {
                return company;
            }
            CompanyOwner owner = company.getOwner();
            if (owner.isPlayer()) {
                if (owner.getPlayer().equals(player)) {
                    return company;
                }
            }
            if (owner.isTeam()) {
                if (owner.getCity().getMembers().contains(player)) {
                    return company;
                }
            }
        }
        return null;
    }

    public Company getCompany(City city) {
        for (Company company : companies) {
            if (company.getOwner().getCity() != null && company.getOwner().getCity().equals(city)) {
                return company;
            }
        }
        return null;
    }

    public boolean isInCompany(UUID player) {
        return getCompany(player) != null;
    }

    public boolean isMerchantOfCompany(UUID player, Company company) {
        return company.getMerchants().containsKey(player);
    }

    public boolean companyExists(String name) {
        return getCompany(name) != null;
    }

}
