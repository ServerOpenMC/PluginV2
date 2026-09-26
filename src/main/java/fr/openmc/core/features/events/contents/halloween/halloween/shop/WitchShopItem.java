package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import fr.openmc.core.features.events.contents.halloween.halloween.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.halloween.dimension.advancement.Advancements;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class WitchShopItem {

    @Getter public final Advancements advancements;
    @Getter public final CustomItem item;
    public final Component name;
    @Getter public final ShopMoney shopMoney;
    @Getter public final int price;


    public WitchShopItem(Advancements advancements, CustomItem item, Component name, ShopMoney moneyItem, int price) {
        this.advancements = advancements;
        this.item = item;
        this.name = name;
        this.shopMoney = moneyItem;
        this.price = price;
    }
    public WitchShopItem(Advancements advancements, CustomItem item, Component name, int price) {
        this(advancements, item, name, ShopMoney.MONSTER_CANDY, price);
    }

    public WitchShopItem(Advancements advancements, CustomItem item, int price) {
        this(advancements, item, item.getBest().displayName(), price);
    }

    public Component getName() {
        if (HalloweenManager.hasUnlock(advancements))
            return name;
        return Component.text("§knotunlockyet");
    }
}
