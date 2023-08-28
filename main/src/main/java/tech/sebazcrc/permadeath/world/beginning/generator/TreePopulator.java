package tech.sebazcrc.permadeath.world.beginning.generator;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TreePopulator extends BlockPopulator {
    private static Set<Coordinates> chunks = ConcurrentHashMap.newKeySet();
    private static Set<Coordinates> unpopulatedChunks = ConcurrentHashMap.newKeySet();

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Coordinates chunkCoordinates = new Coordinates(chunkX, chunkZ);

        if (!chunks.contains(chunkCoordinates)) {
            chunks.add(chunkCoordinates);
            unpopulatedChunks.add(chunkCoordinates);
        }

        for (Coordinates unpopulatedChunk : unpopulatedChunks) {
            if (chunks.contains(unpopulatedChunk.left())
                    && chunks.contains(unpopulatedChunk.right())
                    && chunks.contains(unpopulatedChunk.above())
                    && chunks.contains(unpopulatedChunk.below())
                    && chunks.contains(unpopulatedChunk.upperLeft())
                    && chunks.contains(unpopulatedChunk.upperRight())
                    && chunks.contains(unpopulatedChunk.lowerLeft())
                    && chunks.contains(unpopulatedChunk.lowerRight())) {
                actuallyPopulate(world, random, world.getChunkAt(unpopulatedChunk.x, unpopulatedChunk.z));
                unpopulatedChunks.remove(unpopulatedChunk);
            }
        }
    }

    private void actuallyPopulate(World world, Random random, Chunk chunk) {

        int x = random.nextInt(16);
        int z = random.nextInt(16);
        int y = world.getMaxHeight() - 1;

        while (y > 0 && chunk.getBlock(x, y, z).getType() == Material.AIR) {
            --y;
        }

        if (y > 0 && y < 255) {

            if (y >= 100 && y < 105) {

                world.generateTree(chunk.getBlock(x, y + 1, z).getLocation(), TreeType.CHORUS_PLANT, new BlockChangeDelegate() {
                    @Override
                    public boolean setBlockData(int i, int i1, int i2, @NotNull BlockData blockData) {
                        if (blockData.getMaterial() == Material.CHORUS_FLOWER) {
                            world.getBlockAt(i, i1, i2).setType(Material.SEA_LANTERN);
                        } else if (blockData.getMaterial() == Material.CHORUS_PLANT) {
                            world.getBlockAt(i, i1, i2).setType(Material.END_STONE_BRICK_WALL);
                        }
                        return true;
                    }

                    @Override
                    public @NotNull
                    BlockData getBlockData(int i, int i1, int i2) {
                        return null;
                    }

                    @Override
                    public int getHeight() {
                        return 255;
                    }

                    @Override
                    public boolean isEmpty(int i, int i1, int i2) {
                        return false;
                    }
                });
            }
        }
    }

    private class Coordinates {
        public final int x;
        public final int z;

        public Coordinates(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public Coordinates left() {
            return new Coordinates(x - 1, z);
        }

        public Coordinates right() {
            return new Coordinates(x + 1, z);
        }

        public Coordinates above() {
            return new Coordinates(x, z - 1);
        }

        public Coordinates below() {
            return new Coordinates(x, z + 1);
        }

        public Coordinates upperLeft() {
            return new Coordinates(x - 1, z - 1);
        }

        public Coordinates upperRight() {
            return new Coordinates(x + 1, z - 1);
        }

        public Coordinates lowerLeft() {
            return new Coordinates(x - 1, z + 1);
        }

        public Coordinates lowerRight() {
            return new Coordinates(x + 1, z + 1);
        }

        @Override
        public int hashCode() {
            return (x + z) * (x + z + 1) / 2 + x;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Coordinates other = (Coordinates) obj;
            if (x != other.x)
                return false;
            if (z != other.z)
                return false;
            return true;
        }
    }
}
