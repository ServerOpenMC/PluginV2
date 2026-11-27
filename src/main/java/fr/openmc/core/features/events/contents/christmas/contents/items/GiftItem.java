package fr.openmc.core.features.events.contents.christmas.contents.items;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.UsableItem;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class GiftItem extends CustomItem implements UsableItem {
    private final GiftType type;

    public GiftItem(GiftType type) {
        super(type.getId());
        this.type = type;
    }

    @Override
    public @NotNull ItemStack getVanilla() {
        return ItemStack.of(Material.PAPER);
    }

    @Override
    public void onClickOnEntity(Player p, PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player p2)) return;

        ItemStack itemHand = p.getEquipment().getItemInMainHand();
        if (p.hasCooldown(itemHand)) return;
        p.setCooldown(itemHand, 2 * 20);

        LootReward reward = type.getGiftTable().rollLoots();
        ItemUtils.removeItemsFromPlayerInventory(p, this.getBest(), 1);

        for (CustomLoot loot : reward.loots()) {
            loot.run(p);
            loot.run(p2);
        }

        OMCPlayer player = OMCPlayer.of(p);
        OMCPlayer player2 = OMCPlayer.of(p2);

        Component lootComponent = reward.buildComponent();
        player.message().sendInfo(
                TranslationManager.translation("feature.events.christmas.gift.gift_gived",
                        this.getBest().displayName(),
                        player2.getNameWithHead(),
                        lootComponent
                ),
                Prefix.CHRISTMAS,
                false
        );
        player2.message().sendInfo(
                TranslationManager.translation("feature.events.christmas.gift.player_gave",
                        player.getNameWithHead(),
                        this.getBest().displayName(),
                        lootComponent
                ),
                Prefix.CHRISTMAS,
                false
        );

        switch (type) {
            case COMMON, RARE -> {
                player.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.twinkle"), Sound.Source.AMBIENT, 1, 1.3f));
                player2.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.twinkle"), Sound.Source.AMBIENT, 1, 1.3f));

                ParticleUtils.spawnDispersingParticles(player2.getLocation(), Particle.FIREWORK, 40, 40, 0.1, null);
            }
            case LEGENDARY -> {
                Location loc = player2.getLocation().add(0, 1, 0);

                player.playSound(Sound.sound(Key.key("minecraft:ui.toast.challenge_complete"), Sound.Source.AMBIENT, 1, 1f));
                player2.playSound(Sound.sound(Key.key("minecraft:ui.toast.challenge_complete"), Sound.Source.AMBIENT, 1, 1f));
                player2.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.launch"), Sound.Source.AMBIENT, 1, 0.8f));

                ParticleUtils.spawnConvergingParticles(loc, 25);
                ParticleUtils.spawnCloudParticlesToAll(
                        loc.clone().add(0, 2, 0), Particle.END_ROD, 30, 1.5, 2.5,
                        loc.getNearbyEntitiesByType(Player.class, 40)
                );

                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                    player.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.blast"), Sound.Source.AMBIENT, 1, 0.9f));
                    player2.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.blast"), Sound.Source.AMBIENT, 1, 0.9f));
                    player2.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.AMBIENT, 1, 1.2f));

                    ParticleUtils.spawnDispersingParticles(loc, Particle.TOTEM_OF_UNDYING, 80, 40, 0.5, null);
                    ParticleUtils.spawnDispersingParticles(loc, Particle.FIREWORK, 60, 40, 0.15, null);
                    ParticleUtils.spawnCubeParticles(loc, Particle.END_ROD, 1.5, 1.5, 1.5, 40, 40, null);
                    ParticleUtils.spawnDispersingParticles(
                            loc, Particle.DUST, 40, 40, 0.1,
                            new Particle.DustOptions(Color.fromRGB(255, 200, 0), 1.5f)
                    );
                }, 20L);
            }

            case ONIRISME -> {
                Location loc = player2.getLocation().add(0, 1, 0);
                Color violet = Color.fromRGB(150, 60, 255);
                Color cyan = Color.fromRGB(80, 255, 230);

                player.playSound(Sound.sound(Key.key("minecraft:block.beacon.activate"), Sound.Source.AMBIENT, 1, 0.7f));
                player2.playSound(Sound.sound(Key.key("minecraft:block.beacon.activate"), Sound.Source.AMBIENT, 1, 0.7f));
                player2.playSound(Sound.sound(Key.key("minecraft:block.end_portal.spawn"), Sound.Source.AMBIENT, 0.6f, 1.5f));

                ParticleUtils.spawnConvergingParticles(loc, 40);
                ParticleUtils.spawnCloudParticlesToAll(
                        loc.clone().add(0, 3, 0), Particle.PORTAL, 60, 2.5, 3,
                        loc.getNearbyEntitiesByType(Player.class, 40)
                );

                new BukkitRunnable() {
                    int tick = 0;

                    @Override
                    public void run() {
                        if (tick >= 5) {
                            cancel();
                            return;
                        }

                        float pitch = 0.8f + tick * 0.2f;
                        player.playSound(Sound.sound(Key.key("minecraft:block.amethyst_block.resonate"), Sound.Source.AMBIENT, 1, pitch));
                        player2.playSound(Sound.sound(Key.key("minecraft:block.amethyst_block.resonate"), Sound.Source.AMBIENT, 1, pitch));

                        ParticleUtils.spawnDispersingParticles(
                                loc, Particle.DUST_COLOR_TRANSITION, 30, 40, 0.1,
                                new Particle.DustTransition(violet, cyan, 1.6f)
                        );
                        ParticleUtils.spawnCubeParticles(loc, Particle.REVERSE_PORTAL, 2, 2, 2, 25, 40, null);
                        ParticleUtils.spawnDispersingParticles(loc, Particle.END_ROD, 15, 40, 0.05, null);

                        tick++;
                    }
                }.runTaskTimer(OMCPlugin.getInstance(), 10L, 6L);

                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                    player.playSound(Sound.sound(Key.key("minecraft:entity.ender_dragon.growl"), Sound.Source.AMBIENT, 0.5f, 1.5f));
                    player2.playSound(Sound.sound(Key.key("minecraft:entity.ender_dragon.growl"), Sound.Source.AMBIENT, 0.5f, 1.5f));
                    player2.playSound(Sound.sound(Key.key("minecraft:ui.toast.challenge_complete"), Sound.Source.AMBIENT, 1, 0.6f));

                    ParticleUtils.spawnDispersingParticles(loc, Particle.DRAGON_BREATH, 80, 40, 0.15, null);
                    ParticleUtils.spawnDispersingParticles(loc, Particle.TOTEM_OF_UNDYING, 80, 40, 0.6, null);
                    ParticleUtils.spawnDispersingParticles(loc, Particle.SOUL_FIRE_FLAME, 40, 40, 0.1, null);
                    ParticleUtils.spawnDispersingParticles(loc, Particle.FLASH, 1, 40, 0, null);
                }, 40L);
            }
        }
    }
}
