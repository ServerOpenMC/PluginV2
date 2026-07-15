package fr.openmc.core.utils.nms;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SkullNMS {
    public static ItemStack getPlayerSkullNMS(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);

        ResolvableProfile profile = ResolvableProfile.createResolved(nmsPlayer.getGameProfile());

        skull.set(DataComponents.PROFILE, profile);
        return skull;
    }
}
