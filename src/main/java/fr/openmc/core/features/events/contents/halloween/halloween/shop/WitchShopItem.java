package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import fr.openmc.core.features.events.contents.halloween.halloween.dimension.advancement.Advancements;
import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

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
        this.advancements = advancements;
        this.item = item;
        this.name = name;
        this.shopMoney = ShopMoney.MONSTER_CANDY;
        this.price = price;
    }

    public Component getName() {
        // TODO get current advancement
        return Component.text("aaaaaaaaaa").decoration(TextDecoration.OBFUSCATED, false);
    }
}
