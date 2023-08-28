package tech.sebazcrc.permadeath.nms.v1_20_R1.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
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

        BlockPos bp = new BlockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        SpawnerBlockEntity spawner = (SpawnerBlockEntity) ((CraftWorld) pos.getWorld()).getHandle().getBlockEntity(bp);

        CompoundTag tileData = spawner.getUpdateTag();

        CompoundTag spawnData = new CompoundTag();
        CompoundTag entity = new CompoundTag();
        ListTag armor = new ListTag();
        CompoundTag head = new CompoundTag();
        CompoundTag headTags = new CompoundTag();

        tileData.putBoolean("InfernalNetherite", true);

        entity.putString("id", "minecraft:armor_stand");
        entity.putByte("Marker", (byte) 1);
        entity.putInt("Invisible", 1);

        // ARMOR
        head.putString("id", "minecraft:structure_block");
        head.putByte("Count", (byte) 1);

        headTags.putInt("Unbreakable", 1);
        head.put("tag", headTags);

        armor.add(new CompoundTag());
        armor.add(new CompoundTag());
        armor.add(new CompoundTag());
        armor.add(head);
        entity.put("ArmorItems", armor);

        tileData.putShort("SpawnRange", (short) 0);
        tileData.putShort("SpawnCount", (short) 0);
        tileData.putShort("RequiredPlayerRange", (short) 0);
        tileData.putShort("MaxNearbyEntities", (short) 0);
        spawnData.put("entity", entity);

        tileData.put("SpawnData", spawnData);

        spawner.load(tileData);

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
        BlockPos bp = new BlockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        SpawnerBlockEntity spawner = (SpawnerBlockEntity) ((CraftWorld) Objects.requireNonNull(pos.getWorld())).getHandle().getBlockEntity(bp);

        assert spawner != null;
        CompoundTag tileData = spawner.getUpdateTag();
        boolean dataFound = tileData != null && tileData.contains("InfernalNetherite") && tileData.getBoolean("InfernalNetherite");

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
