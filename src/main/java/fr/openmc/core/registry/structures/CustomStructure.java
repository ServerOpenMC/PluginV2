package fr.openmc.core.registry.structures;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class CustomStructure {

    @Getter
    String id;
    File file;

    public CustomStructure(String id, File file) {
        this.id = id;
        this.file = file;
    }

    public void placeStructure(Location origin, boolean mirrorX, boolean mirrorZ) {
        ServerLevel level = ((CraftWorld) origin.getWorld()).getHandle();

        try (InputStream in = new FileInputStream(file)) {

            CompoundTag tag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());

            StructureTemplate template = new StructureTemplate();

            template.load(
                    level.registryAccess().lookupOrThrow(Registries.BLOCK),
                    tag
            );

            StructurePlaceSettings settings = new StructurePlaceSettings();

            if (mirrorX) settings.setMirror(Mirror.FRONT_BACK);
            if (mirrorZ) settings.setMirror(Mirror.LEFT_RIGHT);

            settings.setRotation(Rotation.NONE);

            BlockPos pos = new BlockPos(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());

            template.placeInWorld(
                    level,
                    pos,
                    pos,
                    settings,
                    level.getRandom(),
                    2
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

