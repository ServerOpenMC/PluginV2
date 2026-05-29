package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.utils.nms.PlayerRespawnNMS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class CustomAmbient {
    public abstract String getId();
    public abstract DimensionTypesInjector.DimensionTypeBuilder getDimensionType();

    public DatapackInjector toDimensionTypeInjector() {
        return new DimensionTypesInjector("omc_ambient").add(getId(), getDimensionType());
    }

    /**
     * Applique l'ambience sur un Joueur
     * @param player Le joueur concerné
     */
    public void apply(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        // * On envoie le packet respawn qui applique l'ambience
        PlayerRespawnNMS.sendPacket(
                nmsPlayer,
                getPlayerAmbientSpawnInfo(nmsPlayer),
                getPivotDimension(nmsPlayer)
        );
    }

    /**
     * Retire l'ambience du Joueur
     * @param player le joueur ciblé
     */
    public static void reset(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        // * On envoie le packet respawn qui remets tout a la normale
        PlayerRespawnNMS.sendPacket(
                nmsPlayer,
                nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()),
                getPivotDimension(nmsPlayer)
        );
    }

    private CommonPlayerSpawnInfo getPlayerAmbientSpawnInfo(ServerPlayer nmsPlayer) {
        ServerLevel nmsWorld = nmsPlayer.level();
        CommonPlayerSpawnInfo spawnInfo = nmsPlayer.createCommonSpawnInfo(nmsPlayer.level());

        ResourceKey<DimensionType> key = ResourceKey.create(
                Registries.DIMENSION_TYPE,
                Identifier.fromNamespaceAndPath("omc_ambient", this.getId())
        );

        Registry<DimensionType> dimRegistry =
                nmsWorld.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE);

        Holder<DimensionType> dimensionTypeHolder = dimRegistry.get(key).orElseThrow(() ->
                new IllegalStateException("DimensionType omc_ambient:"+ this.getId() +" introuvable")
        );

        return new CommonPlayerSpawnInfo(
                dimensionTypeHolder,
                spawnInfo.dimension(),
                spawnInfo.seed(),
                spawnInfo.gameType(),
                spawnInfo.previousGameType(),
                spawnInfo.isDebug(),
                spawnInfo.isFlat(),
                spawnInfo.lastDeathLocation(),
                spawnInfo.portalCooldown(),
                spawnInfo.seaLevel()
        );
    }

    private static ResourceKey<Level> getPivotDimension(ServerPlayer nmsPlayer) {
            return nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()).dimension()
                    .equals(Level.OVERWORLD) ? Level.END : Level.NETHER;
    }
}
