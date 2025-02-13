package fr.openmc.core.features.homes;

import lombok.Getter;

public enum HomeLimits {

    LIMIT_0(1, 0),
    LIMIT_1(3, 5000),
    LIMIT_2(5, 10000),
    LIMIT_3(7, 15000),
    LIMIT_4(10, 20000),
    LIMIT_5(13, 25000),
    LIMIT_6(15, 30000),
    LIMIT_7(17, 35000),
    LIMIT_8(20, 40000),
    LIMIT_9(23, 45000),
    LIMIT_10(25, 50000),
    LIMIT_11(27, 55000),
    LIMIT_12(30, 60000),
    ;

    @Getter int limit;
    @Getter int price;

    HomeLimits(int limit, int price) {
        this.limit = limit;
        this.price = price;
    }

}
