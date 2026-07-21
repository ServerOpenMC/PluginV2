package fr.openmc.core.features.displays;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.bootstrap.features.types.NotInUnitTest;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.hooks.ProtocolLibHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TabList extends Feature implements NotInUnitTest, LoadIfEnable<ProtocolLibHook> {
    private static ProtocolManager protocolManager = null;

    @Override
    public void init() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if (!DreamUtils.isInDreamWorld(event.getPlayer())) return;

                EnumSet<?> actions = packet.getSpecificModifier(EnumSet.class).read(0);
                if (actions.isEmpty()) return;

                Class<? extends Enum> enumClass = actions.iterator().next().getClass();

                boolean shouldFilter = actions.contains(Enum.valueOf(enumClass, "ADD_PLAYER"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LATENCY"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LISTED"));
                if (!shouldFilter) return;

                List<Object> entries = packet.getSpecificModifier(List.class).read(0);

                UUID viewerId = event.getPlayer().getUniqueId();

                List<Object> filtered = entries.stream()
                        .filter(entry -> {
                            try {
                                UUID profileId = (UUID) entry.getClass().getMethod("profileId").invoke(entry);
                                return profileId.equals(viewerId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        })
                        .toList();

                packet.getModifier().withType(List.class).write(0, filtered);
            }
        });
    }

    public static void updateHeaderFooter(Player player, Component header, Component footer) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.connection.send(new ClientboundTabListPacket(
                PaperAdventure.asVanilla(header), PaperAdventure.asVanilla(footer)));
    }

    public static void updateTabList(Player player) {
        int visibleOnlinePlayers = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.canSee(p)) {
                visibleOnlinePlayers++;
            }
        }

        boolean isInDream = DreamUtils.isInDream(player);
        String logo;
        if (ItemsAdderHook.isEnable()) {
            logo = FontImageWrapper.replaceFontImages(isInDream ? ":dream_openmc:" : ":openmc:");
        } else {
            logo = "OPEN MC";
        }

        Component header = !isInDream
                ? TranslationManager.translation(
                        "feature.displays.tablist.header.default",
                        Component.text(logo),
                        Component.text(visibleOnlinePlayers).color(NamedTextColor.GOLD),
                        Component.text(Bukkit.getMaxPlayers()).color(NamedTextColor.YELLOW)
                )
                : TranslationManager.translation(
                        "feature.displays.tablist.header.dream",
                        Component.text(logo)
                );
        Component footer = isInDream
                ? TranslationManager.translation("feature.displays.tablist.footer.dream")
                : TranslationManager.translation("feature.displays.tablist.footer.default");

        updateHeaderFooter(player, header, footer);
    }

}
