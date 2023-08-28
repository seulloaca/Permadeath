package tech.sebazcrc.permadeath.nms.v1_16_R3.block;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.TileEntityMobSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import tech.sebazcrc.permadeath.util.interfaces.InfernalNetheriteBlock;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;

import java.util.Objects;

public class InfernalNetheriteBlockImpl implements InfernalNetheriteBlock {
    @Override
    public void placeCustomBlock(Location pos) {
        Block o = pos.getBlock();
        o.setType(Material.SPAWNER);

        BlockPosition bp = new BlockPosition(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        TileEntityMobSpawner spawner = (TileEntityMobSpawner) ((CraftWorld) pos.getWorld()).getHandle()
                .getTileEntity(bp);

        NBTTagCompound tileData = spawner.b();
        NBTTagCompound spawnData = new NBTTagCompound();
        NBTTagList armor = new NBTTagList();
        NBTTagCompound head = new NBTTagCompound();
        NBTTagCompound tags = new NBTTagCompound();

        tileData.setBoolean("InfernalNetherite", true);

        tags.setInt("Unbreakable", 1);

        head.setString("id", "minecraft:structure_block");
        head.setByte("Count", (byte) 1);
        head.set("tag", tags);

        armor.add(new NBTTagCompound());
        armor.add(new NBTTagCompound());
        armor.add(new NBTTagCompound());
        armor.add(head);

        spawnData.set("ArmorItems", armor);
        spawnData.setString("id", "minecraft:armor_stand");
        spawnData.setByte("Marker", (byte) 1);
        spawnData.setInt("Invisible", 1);

        tileData.setShort("SpawnRange", (short) 0);
        tileData.setShort("SpawnCount", (short) 0);
        tileData.setShort("RequiredPlayerRange", (short) 0);
        tileData.setShort("MaxNearbyEntities", (short) 0);
        tileData.set("SpawnData", spawnData);

        spawner.load(spawner.getBlock(), tileData);

        pos.getWorld().playSound(pos, Sound.BLOCK_STONE_BREAK, 1, 1);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent e) {
        if (isInfernalNetherite(e.getBlock().getLocation())) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 0.5, 0), PermadeathItems.craftInfernalNetheriteIngot());
            e.setExpToDrop(0);
        }
    }

    @Override
    public boolean isInfernalNetherite(Location pos) {
        BlockPosition bp = new BlockPosition(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        TileEntityMobSpawner spawner = (TileEntityMobSpawner) ((CraftWorld) Objects.requireNonNull(pos.getWorld())).getHandle().getTileEntity(bp);

        assert spawner != null;
        NBTTagCompound tileData = spawner.b();
        boolean dataFound = tileData != null && tileData.hasKey("InfernalNetherite") && tileData.getBoolean("InfernalNetherite");

        if (!dataFound) {
            if (pos.getBlock().getState() instanceof CreatureSpawner) {
                CreatureSpawner c = (CreatureSpawner) pos.getBlock().getState();
                if (c.getSpawnedType() == EntityType.ARMOR_STAND) {
                    dataFound = true;
                }
            }
        }

        return dataFound;
    }
}
