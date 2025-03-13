package fr.openmc.core.features.corporation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ShopOwner {

    private final Company company;
    private final UUID player;

    public ShopOwner(Company company) {
        this.company = company;
        this.player = null;
    }

    public ShopOwner(UUID owner) {
        this.company = null;
        this.player = owner;
    }

    public boolean isCompany() {
        return company != null;
    }

    public boolean isPlayer() {
        return player != null;
    }

}
