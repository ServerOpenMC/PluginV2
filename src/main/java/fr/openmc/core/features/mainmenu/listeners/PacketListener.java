package fr.openmc.core.features.mainmenu.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.MainMenu;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import net.minecraft.advancements.*;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PacketListener implements Listener {

    private final ClientboundUpdateAdvancementsPacket advancementPacket;
    @Getter
    private static final Map<UUID, ClientboundUpdateAdvancementsPacket> advancementPackets = new HashMap<>();
    @Getter
    private static final List<UUID> enabledAdvancements = new ArrayList<>();

    public PacketListener(OMCPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        advancementPacket = createEmptyAdvancementPacket();
    }

    /**
     * Créé un packet de progrès vides avec écrit "Chargement..." en attendant que le menu principal soit affiché.
     *
     * @return Un packet {@link ClientboundUpdateAdvancementsPacket} avec les progrès vides.
     */
    private static ClientboundUpdateAdvancementsPacket createEmptyAdvancementPacket() {
        // Rien de très important ici, on crée justes les instances nécessaires pour le packet avec le minimum requis.
        DisplayInfo displayInfo = new DisplayInfo(
                ItemStack.fromBukkitCopy(getInvisibleItem()),
                Component.literal("Chargement..."),
                Component.empty(),
                Optional.of(new ClientAsset(ResourceLocation.fromNamespaceAndPath("minecraft", "gui/tab_header_background"))), // Texture transparente par défaut
                AdvancementType.GOAL,
                false,
                false,
                false
        );
        Advancement advancement = new Advancement(
                Optional.empty(),
                Optional.of(displayInfo),
                new AdvancementRewards(0, new ArrayList<>(), new ArrayList<>(), Optional.empty()),
                new HashMap<>(),
                new AdvancementRequirements(new ArrayList<>()),
                false
        );
        AdvancementHolder advancementHolder = new AdvancementHolder(ResourceLocation.fromNamespaceAndPath("openmc", "advancement"), advancement);
        return new ClientboundUpdateAdvancementsPacket(
                true,
                Collections.singletonList(advancementHolder),
                Set.of(),
                new HashMap<>(),
                false
        );
    }

    private static org.bukkit.inventory.ItemStack getInvisibleItem() {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setItemModel(NamespacedKey.minecraft("air"));
        itemMeta.setHideTooltip(true);
        item.setItemMeta(itemMeta);
        return item;
    }

    public void inject(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        Channel channel = connection.connection.channel;
        UUID playerUUID = player.getUniqueId();

        if (channel.pipeline().get("packet_listener") != null) return;

        channel.pipeline().addBefore("packet_handler", "packet_listener", new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof ServerboundSeenAdvancementsPacket packet) {
                    if (packet.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB && packet.getTab() != null && packet.getTab().getNamespace().equals("openmc")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                MainMenu.openMainMenu(player);
                            }
                        }.runTask(OMCPlugin.getInstance());
                    } else if (packet.getAction() == ServerboundSeenAdvancementsPacket.Action.CLOSED_SCREEN && enabledAdvancements.contains(playerUUID)) {
                        connection.connection.send(advancementPacket);
                    }
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof ClientboundUpdateAdvancementsPacket packet && !enabledAdvancements.contains(playerUUID)) {
                    packet = new ClientboundUpdateAdvancementsPacket(true, packet.getAdded(), packet.getRemoved(), packet.getProgress(), true);
                    advancementPackets.put(playerUUID, packet);
                    super.write(ctx, advancementPacket, promise);
                    return;
                }
                super.write(ctx, msg, promise);
            }
        });
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        advancementPackets.remove(event.getPlayer().getUniqueId());
        enabledAdvancements.remove(event.getPlayer().getUniqueId());
    }
}
