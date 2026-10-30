package fr.openmc.core.features.events.contents.halloween.festival.shop;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floors;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class WitchShopItem {

    @Getter public final Floors advancements;
    @Getter public final CustomItem item;
    public final Component name;
    @Getter public final ShopMoney shopMoney;
    @Getter public final int price;


    public WitchShopItem(Floors advancements, CustomItem item, Component name, ShopMoney moneyItem, int price) {
        this.advancements = advancements;
        this.item = item;
        this.name = name;
        this.shopMoney = moneyItem;
        this.price = price;
    }
    public WitchShopItem(Floors advancements, CustomItem item, Component name, int price) {
        this(advancements, item, name, ShopMoney.MONSTER_CANDY, price);
    }

    public WitchShopItem(Floors advancements, CustomItem item, ShopMoney moneyItem, int price) {
        this(advancements, item, item.getBest().effectiveName().asHoverEvent().value(), moneyItem, price);
    }

    public WitchShopItem(Floors advancements, CustomItem item, int price) {
        this(advancements, item, item.getBest().effectiveName().asHoverEvent().value(), price);
    }

    public Component getName() {
        if (TerrorHouseManager.enabledFloors.contains(advancements))
            return name;
        return Component.text("§knotunlockyet");
    }
}
