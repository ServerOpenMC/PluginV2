package fr.openmc.core.features.dream.mecanism.singularity;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class SingularityCraftListener implements Listener {
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
        if (dreamItem == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!dreamItem.getId().equals("omc_dream:singularity")) return;

        MailboxManager.sendItems(player, player, new ItemStack[] { dreamItem.getBest() });

        // * SFX
        World world = player.getWorld();
        world.strikeLightningEffect(player.getLocation());
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.OMINOUS_SPAWNING, 25, 15, 0.1,  null);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.FLASH, 5, 15, 0.05,  null);
        world.playSound(player.getLocation(), "minecraft:entity.wither.death", 1f, 0.1f);

        MessagesManager.broadcastMessage(Component.text(player.getName() + " a crafté une Singularité !"), Prefix.DREAM, MessageType.INFO);
    }
}
