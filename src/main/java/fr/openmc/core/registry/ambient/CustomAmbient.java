package fr.openmc.core.registry.ambient;

import com.google.gson.JsonObject;
import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import fr.openmc.core.utils.MathUtils;
import fr.openmc.core.utils.nms.PlayerBiomeNMS;
import fr.openmc.core.utils.nms.PlayerRespawnNMS;
import fr.openmc.core.utils.nms.PlayerSetTimeNMS;
import fr.openmc.core.utils.nms.PlayerWeatherNMS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class CustomAmbient {
    // ** UUID playerUUID -> String idAmbient
    public static final Map<UUID, String> ACTIVE_AMBIENTS = new HashMap<>();

    public Holder<DimensionType> CACHED_DIMENSION_TYPE = null;

    public abstract String getId();
    public abstract AmbientBuilder getAmbientBuilder();

    /**
     * Choix de la transition de dimension lorsque le joueur change d'ambience
     * @return La key de la dimension
     */
    public abstract ResourceKey<Level> getTransitionDimension();

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
                getTransitionDimensionForPlayer(nmsPlayer)
        );

        if (this.getAmbientBuilder().utilizeBiome()) {
            PlayerBiomeNMS.replaceBiomes(nmsPlayer, this.getId(), this::toBiomeVariantKey);
        }

        if (this.getAmbientBuilder().getTimeFixed() != null) {
            PlayerSetTimeNMS.sendPacketSetTime(player, this.getAmbientBuilder().getTimeFixed());
        }

        if (this.getAmbientBuilder().getWeatherFixed() != null) {
            PlayerWeatherNMS.setWeather(player, this.getAmbientBuilder().getWeatherFixed());
        }

        ACTIVE_AMBIENTS.put(player.getUniqueId(), this.getId());
    }

    /**
     * Applique l'ambience sur des joueurs
     * @param receivers Les joueurs concernés
     */
    public void apply(Collection<Player> receivers) {
        for (Player receiver : receivers) {
            apply(receiver);
        }
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
                getTransitionDimensionForPlayer(nmsPlayer)
        );

        ACTIVE_AMBIENTS.remove(player.getUniqueId());
    }

    /**
     * Retire l'ambience des joueurs
     * @param receivers le joueur ciblé74
     */
    public void reset(Collection<Player> receivers) {
        for (Player receiver : receivers) {
            reset(receiver);
        }
    }

    /**
     * Genere la clé de la variente du biome
     * @param initialBiomeKey clé du biome initial a changer
     * @return identifiant de la variante du biome (namespace:initialBiomePath_ambientId)
     */
    public Identifier toBiomeVariantKey(Identifier initialBiomeKey) {
        return Identifier.fromNamespaceAndPath(CustomAmbientRegistry.NAMESPACE, initialBiomeKey.getPath() + "_" + this.getId());
    }

    /**
     * Genere une variante d'un biome en fonction d'une ambience (namespace:id)
     * Retourne un injecteur de biome qui prendra une variante de celui ci
     * (couleur de l'herbe initial si pas override par l'ambience, idem pour les autres)
     * @param initialBiome le biome initial à cloner
     * @param ambientId l'id de l'ambience
     * @return l'injecteur du fichier json
     */
    public BiomesInjector toBiomeVariant(Biome initialBiome, Identifier ambientId) {
        BiomeSpecialEffects initialEffects = initialBiome.getSpecialEffects();
        Biome.ClimateSettings climate = initialBiome.climateSettings;

        if (!this.getAmbientBuilder().utilizeBiome()) return null;

        JsonObject effects = this.getAmbientBuilder().getBiomeBuilder().getEffects();
        Optional<Integer> grassColor = hasEffects(effects, "grass_color") ?
                Optional.of(MathUtils.hexToInt(effects.get("grass_color").getAsString())) :
                initialEffects.grassColorOverride();
        Optional<Integer> foliageColor = hasEffects(effects, "foliage_color") ?
                Optional.of(MathUtils.hexToInt(effects.get("foliage_color").getAsString())) :
                initialEffects.foliageColorOverride();
        Integer waterColor = hasEffects(effects, "water_color") ?
                MathUtils.hexToInt(effects.get("water_color").getAsString()) :
                initialEffects.waterColor();
        Optional<Integer> dryFoliageColor = hasEffects(effects, "dry_foliage_color") ?
                Optional.of(MathUtils.hexToInt(effects.get("dry_foliage_color").getAsString())) :
                initialEffects.foliageColorOverride();
        String grassColorModifier = hasEffects(effects, "grass_color_modifier") ?
                effects.get("grass_color_modifier").getAsString() :
                initialEffects.grassColorModifier().getName();

        BiomeBuilder builder = new BiomeBuilder()
                .waterColor(waterColor)
                .grassColorModifier(grassColorModifier)
                .hasPrecipitation(climate.hasPrecipitation())
                .downfall(climate.downfall())
                .temperatures(climate.temperature())
                .temperatureModifier(climate.temperatureModifier().getName());

        grassColor.ifPresent(builder::grassColor);
        foliageColor.ifPresent(builder::foliageColor);
        dryFoliageColor.ifPresent(builder::dryFoliageColor);

        return new BiomesInjector(ambientId.getNamespace()).add(ambientId.getPath(), builder);
    }

    private boolean hasEffects(JsonObject effects, String envKey) {
        return effects.get(envKey) != null;
    }

    /**
     * Calcule la dimension de transition appropriée pour le joueur
     * Si le joueur est en OVERWORLD, on transitionne vers l'ambience
     * Sinon on revient à l'OVERWORLD
     * @param nmsPlayer le joueur ciblé
     * @return la key de dimension
     */
    private ResourceKey<Level> getTransitionDimensionForPlayer(ServerPlayer nmsPlayer) {
        return nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()).dimension().equals(Level.OVERWORLD)
                ? this.getTransitionDimension()
                : Level.OVERWORLD;
    }

    /**
     * Crée les informations de spawn du joueur en fonction de l'ambience ciblé
     * En gros on cherche le dimension_type enregistré dans le registre, et on le mets dans les infos de spawn du joueur
     * @param nmsPlayer le joueur ciblé
     * @return les informations de spawn
     */
    private CommonPlayerSpawnInfo getPlayerAmbientSpawnInfo(ServerPlayer nmsPlayer) {
        CommonPlayerSpawnInfo spawnInfo = nmsPlayer.createCommonSpawnInfo(nmsPlayer.level());

        return new CommonPlayerSpawnInfo(
                getDimensionType(),
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

    private Holder<DimensionType> getDimensionType() {
        if (CACHED_DIMENSION_TYPE != null)
            return CACHED_DIMENSION_TYPE;

        ResourceKey<DimensionType> key = ResourceKey.create(
                Registries.DIMENSION_TYPE,
                Identifier.fromNamespaceAndPath(CustomAmbientRegistry.NAMESPACE, this.getId())
        );

        Registry<DimensionType> dimRegistry =
                MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE);

        CACHED_DIMENSION_TYPE = dimRegistry.get(key).orElseThrow(() ->
                new IllegalStateException("DimensionType " + CustomAmbientRegistry.NAMESPACE + ":"+ this.getId() +" introuvable")
        );
        return CACHED_DIMENSION_TYPE;
    }
}