package fr.openmc.core.disabled.corporation.data;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MerchantData {

    private final List<ItemStack> depositedItems = new ArrayList<>();
    private double moneyWon = 0;

    /**
     * @return the amount of items the merchant has deposit
     */
    public int getAllDepositedItemsAmount() {
        int amount = 0;
        for (ItemStack item : depositedItems) {
            amount += item.getAmount();
        }
        return amount;
    }

    /**
     * add an item to the merchant data
     *
     * @param item the item to add
     */
    public void depositItem(ItemStack item) {
        depositedItems.add(item);
    }

    /**
     * add money to the money won of the merchant
     *
     * @param money the money to add
     */
    public void addMoneyWon(double money) {
        moneyWon += money;
    }

}
