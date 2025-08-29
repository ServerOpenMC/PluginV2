package fr.openmc.core.features.city;

import lombok.Getter;

@Getter
public enum CityType {
    WAR("Guerre", "§c"),
    PEACE("Paix", "§a"),

    ;

    private String displayName;
    private String color;

    CityType(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getName() {
        return color + displayName + "§7";
    }
}
