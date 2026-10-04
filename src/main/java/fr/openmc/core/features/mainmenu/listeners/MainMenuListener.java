package fr.openmc.core.features.mainmenu.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mainmenu.MainMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.advancements.*;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStackTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainMenuListener implements Listener {

    private static final String PIPELINE_NAME = "omc_main_menu_injector";
    private static final Identifier MENU_TAB_ID = Identifier.fromNamespaceAndPath("openmc_mainmenu", "mainmenu");

    private static final Set<UUID> isModeAdvancementActived = new HashSet<>();
    private static final Map<UUID, ClientboundUpdateAdvancementsPacket> mainMenuOpened = new ConcurrentHashMap<>();

    private final ClientboundUpdateAdvancementsPacket tabMainMenuPacket;

    public MainMenuListener(OMCPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.tabMainMenuPacket = createMenuOnlyPacket();
    }

    /**
     * Créé un packet de progrès vides avec écrit "Chargement..." en attendant que le menu principal soit affiché.
     *
     * @return Un packet {@link ClientboundUpdateAdvancementsPacket} avec les progrès vides.
     */
    private static ClientboundUpdateAdvancementsPacket createMenuOnlyPacket() {
        AdvancementHolder holder = createMenuTabHolder();
        ClientboundUpdateAdvancementsPacket.PositionedAdvancement positioned =
                new ClientboundUpdateAdvancementsPacket.PositionedAdvancement(holder, 0, 0);
        return new ClientboundUpdateAdvancementsPacket(
                true,
                List.of(positioned),
                Set.of(),
                Map.of(),
                false
        );
    }

    private static AdvancementHolder createMenuTabHolder() {
        DisplayInfo displayInfo = new DisplayInfo(
                ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(getMenuIcon())),
                PaperAdventure.asVanilla(TranslationManager.translation("feature.mainmenu.advancements.loading")),
                Component.empty(),
                Optional.of(new ClientAsset.ResourceTexture(
                        Identifier.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png")
                )),
                AdvancementType.GOAL,
                false, false, false
        );
        Advancement advancement = new Advancement(
                Optional.empty(),
                Optional.of(displayInfo),
                AdvancementRewards.EMPTY,
                Map.of(),
                new AdvancementRequirements(List.of()),
                false
        );
        return new AdvancementHolder(MENU_TAB_ID, advancement);
    }

    private static org.bukkit.inventory.ItemStack getMenuIcon() {
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

        if (channel.pipeline().get(PIPELINE_NAME) != null) return;

        channel.pipeline().addBefore("packet_handler", PIPELINE_NAME, new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof ServerboundSeenAdvancementsPacket packet) {
                    if (packet.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB
                            && packet.getTab() != null
                            && packet.getTab().getNamespace().equals("openmc_mainmenu")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                MainMenu.openMainMenu(player);
                            }
                        }.runTask(OMCPlugin.getInstance());
                    } else if (packet.getAction() == ServerboundSeenAdvancementsPacket.Action.CLOSED_SCREEN
                            && isModeAdvancementActived.remove(playerUUID)) {
                        connection.connection.send(tabMainMenuPacket);
                    }
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof ClientboundUpdateAdvancementsPacket packet) {
                    if (packet == tabMainMenuPacket) {
                        super.write(ctx, packet, promise);
                        return;
                    }

                    if (packet.shouldReset()) {
                        mainMenuOpened.put(playerUUID, packet);

                        if (isModeAdvancementActived.contains(playerUUID)) {
                            // * Mode succes normal
                            super.write(ctx, packet, promise);
                        } else {
                            // * Mode MainMenu OMC
                            super.write(ctx, tabMainMenuPacket, promise);
                        }
                        return;
                    }
                }
                super.write(ctx, msg, promise);
            }
        });
    }

    public static void setModeAdvancement(Player player) {
        UUID uuid = player.getUniqueId();
        isModeAdvancementActived.add(uuid);

        ClientboundUpdateAdvancementsPacket realTree = mainMenuOpened.get(uuid);
        if (realTree != null) {
            ((CraftPlayer) player).getHandle().connection.send(realTree);
        }
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        isModeAdvancementActived.remove(uuid);
        mainMenuOpened.remove(uuid);
    }
}