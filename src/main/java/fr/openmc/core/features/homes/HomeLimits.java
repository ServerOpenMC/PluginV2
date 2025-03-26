package fr.openmc.core.features.homes;

import lombok.Getter;

public enum HomeLimits {

    LIMIT_0(1, 0, 0),
    LIMIT_1(3, 5000, 10),
    LIMIT_2(5, 10000, 20),
    LIMIT_3(7, 15000, 30),
    LIMIT_4(10, 20000, 40),
    LIMIT_5(13, 25000, 50),
    LIMIT_6(15, 30000, 60),
    LIMIT_7(17, 35000, 70),
    LIMIT_8(20, 40000, 80),
    LIMIT_9(23, 45000, 90),
    LIMIT_10(25, 50000, 100),
    LIMIT_11(27, 55000, 110),
    LIMIT_12(30, 60000, 120),
    ;

    @Getter int limit;
    @Getter int price;
    @Getter int ayweniteCost;

    HomeLimits(int limit, int price, int ayweniteCost) {
        this.limit = limit;
        this.price = price;
        this.ayweniteCost = ayweniteCost;
    }

}
