package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.mecanism.blocksdrops.DreamBlocksDropsManager;
import fr.openmc.core.features.dream.mecanism.cloudfishing.CloudFishingManager;
import fr.openmc.core.features.dream.mecanism.cold.ColdManager;
import fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorManager;
import fr.openmc.core.features.dream.mecanism.sfx.clone.PlayerCloneNpc;
import fr.openmc.core.features.dream.mecanism.sfx.ghost.DreamGhostManager;
import fr.openmc.core.features.dream.mecanism.singularity.SingularityManager;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteNpcManager;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class DreamFeaturesRegistry extends SubRegistry<String, Feature> {
    public final DreamDimensionManager DREAM_DIMENSION = register(new DreamDimensionManager());
    public final GlaciteNpcManager GLACITE_NPC = register(new GlaciteNpcManager());
    public final PlayerCloneNpc PLAYER_CLONE_NPC = register(new PlayerCloneNpc());
    public final DreamBlocksManager DREAM_BLOCKS = register(new DreamBlocksManager());
    public final DreamBlocksDropsManager BLOCK_DROPS = register(new DreamBlocksDropsManager());
    public final CloudFishingManager CLOUD_FISHING = register(new CloudFishingManager());
    public final MetalDetectorManager METAL_DETECTOR = register(new MetalDetectorManager());
    public final ColdManager COLD = register(new ColdManager());
    public final SingularityManager SINGULARITY = register(new SingularityManager());
    public final DreamGhostManager DREAM_GHOST = register(new DreamGhostManager());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
