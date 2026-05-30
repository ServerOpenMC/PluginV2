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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class CustomAmbient {
    // ** UUID playerUUID -> String idAmbient
    public static final Map<UUID, String> ACTIVE_AMBIENTS = new HashMap<>();

    public abstract String getId();
    public abstract DimensionTypesInjector.DimensionTypeBuilder getDimensionType();

    /**
     * Choix de la transition de dimension lorsque le joueur change d'ambience
     * @return La key de la dimension
     */
    public abstract ResourceKey<Level> getTransitionDimension();

    /**
     * Converti notre DimensionTypeBuild en un injecteur de datapack
     * et qui mettera les dimension_type sous le namepsace omc_ambient
     * @return Un datapack injector
     */
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
                nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()).dimension().equals(Level.OVERWORLD)
                        ? this.getTransitionDimension()
                        : Level.OVERWORLD
        );

        ACTIVE_AMBIENTS.put(player.getUniqueId(), this.getId());
    }

    /**
     * Retire l'ambience du Joueur
     * @param player le joueur ciblé
     */
    public void reset(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        // * On envoie le packet respawn qui remets tout a la normale
        PlayerRespawnNMS.sendPacket(
                nmsPlayer,
                nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()),
                nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()).dimension().equals(Level.OVERWORLD)
                        ? this.getTransitionDimension()
                        : Level.OVERWORLD
        );

        ACTIVE_AMBIENTS.remove(player.getUniqueId());
    }

    /**
     * Crée les informations de spawn du joueur en fonction de l'ambience ciblé
     * En gros on cherche le dimension_type enregistré dans le registre, et on le mets dans les infos de spawn du joueur
     * @param nmsPlayer le joueur ciblé
     * @return les informations de spawn
     */
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
}
