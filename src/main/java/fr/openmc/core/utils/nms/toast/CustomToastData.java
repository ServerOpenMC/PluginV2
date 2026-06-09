package fr.openmc.core.utils.nms.toast;

import net.minecraft.advancements.AdvancementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public record CustomToastData(ItemStack icon, String translationKey, Object[] translationsArgs, AdvancementType type) {
    public CustomToastData(ItemStack icon, String translationKey, AdvancementType type) {
        this(icon, translationKey, null, type);
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
