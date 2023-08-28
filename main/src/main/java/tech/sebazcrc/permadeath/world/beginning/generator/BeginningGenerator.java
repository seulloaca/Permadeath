package tech.sebazcrc.permadeath.world.beginning.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.world.WorldEditPortal;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class BeginningGenerator extends ChunkGenerator {
    private static final int HEIGHT = 100;
    private static final boolean SMALL_ISLANDS_ENABLED = Main.getInstance().isSmallIslandsEnabled();
    private final SplittableRandom random = new SplittableRandom();

    @Override
    public ChunkData generateChunkData(World world, Random cRandom, int chunkX, int chunkZ, BiomeGrid biomes) {
        SimplexOctaveGenerator lowGenerator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);

        ChunkData chunk = createChunkData(world);
        lowGenerator.setScale(0.02D);

        for (int X = 0; X < 16; X++)
            for (int Z = 0; Z < 16; Z++) {
                int noise = (int) (lowGenerator.noise(chunkX * 16 + X, chunkZ * 16 + Z,
                        0.5D, 0.5D) * 15);

                if (noise <= 0) {
                    if (Main.worldEditFound && SMALL_ISLANDS_ENABLED && X == 8 && Z == 8)
                        if (random.nextInt(20) == 0) {
                            int finalX = X;
                            int finalZ = Z;

                            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> WorldEditPortal.generateIsland(world,
                                    chunkX * 16 + finalX, chunkZ * 16 + finalZ, HEIGHT, random), 20);
                        }
                    continue;
                }

                //100000
                int chance = Main.getInstance().getConfig().getInt("Toggles.TheBeginning.YticGenerateChance");
                if (chance > 1000000 || chance < 1) {
                    chance = 100000;
                }
                if (Main.worldEditFound && random.nextInt(chance) == 0) {
                    int finalX = X;
                    int finalZ = Z;

                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> WorldEditPortal.generateYtic(world,
                            chunkX * 16 + finalX, chunkZ * 16 + finalZ, HEIGHT), 20);
                }

                for (int i = 0; i < noise / 3; i++)
                    chunk.setBlock(X, i + HEIGHT, Z, Material.PURPUR_BLOCK);

                for (int i = 0; i < noise; i++)
                    chunk.setBlock(X, HEIGHT - i - 1, Z, Material.PURPUR_BLOCK);
            }
        return chunk;
    }

    @NotNull
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.singletonList(new TreePopulator());
    }
}
