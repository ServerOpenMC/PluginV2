package fr.openmc.core.utils.nms.toast;

import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public record CustomToastData(ItemStack icon, Component name, Component description, AdvancementType type) {
    public CustomToastData(ItemStack icon, Component name, AdvancementType type) {
        this(icon, name, Component.empty(), type);
    }

    public void send(Player player) {
        ToastUtils.sendCustomToast(player, this);
    }

    public void send(Collection<Player> receivers) {
        for (Player receiver : receivers) {
            send(receiver);
        }
    }
}
