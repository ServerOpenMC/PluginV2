package fr.openmc.core.features.singularity.sub.world.gravity;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.singularity.sub.world.utils.SingularityWorldUtils;
import fr.openmc.core.utils.bukkit.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GravityListener implements Listener {

    private final float BASE_OVERWORLD = 0.08f;
    private final float BASE_SINGULARITY_WORLD = 0f;
    private final float BASE_ON_SNEAK = 0.01f;
    private final float BASE_ON_JUMP = -0.01f;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (SingularityWorldUtils.isInSingularityWorld(player))
            EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_SINGULARITY_WORLD);
        if (!SingularityWorldUtils.isInSingularityWorld(player))
            EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_OVERWORLD);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (SingularityWorldUtils.isInSingularityWorld(player))
            EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_OVERWORLD);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (SingularityWorldUtils.isInSingularityWorld(player))
            EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_OVERWORLD);

    }

    @EventHandler
    public void onSinguEntrered(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!SingularityWorldUtils.isSingularityWorld(event.getTo())) return;
        if (SingularityWorldUtils.isSingularityWorld(event.getFrom())) return;

        EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_OVERWORLD);
    }

    @EventHandler
    public void onSinguLeave(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!SingularityWorldUtils.isSingularityWorld(event.getFrom())) return;
        if (SingularityWorldUtils.isSingularityWorld(event.getTo())) return;

        EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_OVERWORLD);
    }

    @EventHandler
    public void onPlayerSendInput(PlayerInputEvent event) {
        if (!SingularityWorldUtils.isInSingularityWorld(event.getPlayer())) return;

        Player player = event.getPlayer();

        float baseToApply;

        if (event.getInput().isJump()) {
            baseToApply = BASE_ON_JUMP;
        } else if (event.getInput().isSneak()) {
            baseToApply = BASE_ON_SNEAK;
        } else return;

        EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, baseToApply);

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            EntityUtils.setAttributeIfPresent(player, Attribute.GRAVITY, BASE_SINGULARITY_WORLD);
        }, 20L);
    }
}
