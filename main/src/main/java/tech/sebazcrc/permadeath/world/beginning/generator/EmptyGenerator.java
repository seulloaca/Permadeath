package tech.sebazcrc.permadeath.world.beginning.generator;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import java.util.Random;

public class EmptyGenerator extends ChunkGenerator {
    @Override
    public ChunkData generateChunkData(World world, Random cRandom, int chunkX, int chunkZ, BiomeGrid biomes) {
        return createChunkData(world);
    }
}
