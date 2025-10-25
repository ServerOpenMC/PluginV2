package fr.openmc.core.features.hdv;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class HDVListing {
    private final UUID seller;
    private final ItemStack item;
    private final double price;
    private final LocalDateTime createdAt;

    public HDVListing(UUID seller, ItemStack item, double price) {
        this.seller = seller;
        this.item = item.clone();
        this.price = price;
        this.createdAt = LocalDateTime.now();
    }
}
