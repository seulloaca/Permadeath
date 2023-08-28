package tech.sebazcrc.permadeath.world.beginning.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import tech.sebazcrc.permadeath.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class BeginningGenerator extends ChunkGenerator {
    //private static final int HEIGHT = 210;
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
                    if (SMALL_ISLANDS_ENABLED && X == 8 && Z == 8)
                        if (random.nextInt(20) == 0) {
                            int finalX = X;
                            int finalZ = Z;

                            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> generateIsland(world,
                                    chunkX * 16 + finalX, chunkZ * 16 + finalZ, random), 20);
                        }
                    continue;
                }

                //100000

                int chance = Main.getInstance().getConfig().getInt("Toggles.TheBeginning.YticGenerateChance");

                if (chance > 1000000 || chance < 1) {

                    chance = 100000;
                }

                if (random.nextInt(chance) == 0) {
                    int finalX = X;
                    int finalZ = Z;

                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> generateYtic(world,
                            chunkX * 16 + finalX, chunkZ * 16 + finalZ), 20);
                }

                for (int i = 0; i < noise / 3; i++)
                    chunk.setBlock(X, i + HEIGHT, Z, Material.PURPUR_BLOCK);

                for (int i = 0; i < noise; i++)
                    chunk.setBlock(X, HEIGHT - i - 1, Z, Material.PURPUR_BLOCK);
            }
        return chunk;
    }

    private void generateIsland(World world, int x, int z, SplittableRandom random) {
        Clipboard clipboard;
        File file;

        switch (random.nextInt(6)) {
            case 0:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island1.schem");
                break;
            case 1:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island2.schem");
                break;
            case 2:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island3.schem");
                break;
            case 3:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island4.schem");
                break;
            default:
                file = new File(Main.getInstance().getDataFolder(), "schematics/island5.schem");
                break;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        assert format != null;

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, HEIGHT + 20, z))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private void generateYtic(World world, int x, int z) {
        Clipboard clipboard;
        File file = new File(Main.getInstance().getDataFolder(), "schematics/ytic.schem");

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

            Operation operation = clipboardHolder
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, HEIGHT + 34, z))
                    .ignoreAirBlocks(true)
                    .copyEntities(true)
                    .build();

            Operations.complete(operation);
            //editSession.replaceBlocks(
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.singletonList(new TreePopulator());
    }
}
