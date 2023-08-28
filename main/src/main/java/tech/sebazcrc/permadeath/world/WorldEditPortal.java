package tech.sebazcrc.permadeath.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import tech.sebazcrc.permadeath.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class WorldEditPortal {
    public static void generatePortal(boolean overworld, Location to) {

        if (!Main.getInstance().getBeData().generatedOverWorldBeginningPortal() && overworld) {

            int x = Main.getInstance().getConfig().getInt("TheBeginning.X-Limit");
            int z = Main.getInstance().getConfig().getInt("TheBeginning.Z-Limit");

            int ranX = new Random().nextInt(x);
            int ranZ = new Random().nextInt(z);

            if (new Random().nextBoolean()) {
                ranX = ranX * -1;
            }
            if (new Random().nextBoolean()) {

                ranZ = ranZ * -1;
            }
            Location loc = new Location(Main.getInstance().world, ranX, 0, ranZ);

            int highestBlockAt = Main.getInstance().world.getHighestBlockAt(loc).getY();
            if (highestBlockAt == -1) {
                highestBlockAt = 50;
            }

            highestBlockAt = highestBlockAt + 15;
            loc.setY(highestBlockAt);
            pasteSchematic(loc, new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
            Main.getInstance().getBeData().setOverWorldPortal(loc);
        }

        if (!Main.getInstance().getBeData().generatedBeginningPortal() && !overworld) {
            Bukkit.getWorld("pdc_the_beginning").loadChunk(to.getChunk());

            pasteSchematic(to, new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
            Main.getInstance().getBeData().setBeginningPortal(to);
        }
    }

    public static void pasteSchematic(Location loc, File schematic) {
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,
                    -1)) {
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                        .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).ignoreAirBlocks(true).build();
                try {
                    Operations.complete(operation);
                    editSession.flushSession();
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
