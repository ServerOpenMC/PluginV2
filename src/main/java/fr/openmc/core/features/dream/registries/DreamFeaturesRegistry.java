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
    public final Feature DREAM_DIMENSION = register(new DreamDimensionManager());
    public final Feature GLACITE_NPC = register(new GlaciteNpcManager(OMCRegistry.FEATURES.DREAM.get()));
    public final Feature PLAYER_CLONE_NPC = register(new PlayerCloneNpc());
    public final Feature DREAM_BLOCKS = register(new DreamBlocksManager());
    public final Feature BLOCK_DROPS = register(new DreamBlocksDropsManager());
    public final Feature CLOUD_FISHING = register(new CloudFishingManager());
    public final Feature METAL_DETECTOR = register(new MetalDetectorManager());
    public final Feature COLD = register(new ColdManager());
    public final Feature SINGULARITY = register(new SingularityManager());
    public final Feature DREAM_GHOST = register(new DreamGhostManager());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
