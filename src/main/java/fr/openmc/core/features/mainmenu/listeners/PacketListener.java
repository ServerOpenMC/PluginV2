package fr.openmc.core.features.mainmenu.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.MainMenu;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PacketListener implements Listener {

    private final OMCPlugin plugin;
    private final ClientboundUpdateAdvancementsPacket advancementPacket;

    // #ProtocolLib sucks
    public PacketListener(OMCPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        advancementPacket = createAdvancementPacket();
    }

    private static ClientboundUpdateAdvancementsPacket createAdvancementPacket() {
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
                        }.runTask(plugin);
                    }
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof ClientboundUpdateAdvancementsPacket) {
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
}
