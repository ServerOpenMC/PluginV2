package fr.openmc.core.commands.debug;

import fr.openmc.core.utils.nms.ToastUtils;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("toast")
@CommandPermission("omc.admins.commands.toast")
public class ToastCommand {
    @Subcommand("test")
    @CommandPermission("omc.admins.commands.toast.test")
    public void test(Player player) {
        ToastUtils.sendCustomToast(player, Material.TEST_INSTANCE_BLOCK, "translation.key.fuck", AdvancementType.CHALLENGE);
    }
}
