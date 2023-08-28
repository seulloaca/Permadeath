package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMinecart;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.interfaces.DeathModule;

import java.util.Optional;

public class DeathModuleImpl implements DeathModule {
    private static final int MODULE_HARM_EFFECT_LEVEL = 3;

    @Override
    public void spawn(Location where) {
        ServerLevel world = ((CraftWorld)where.getWorld()).getHandle();

        SpawnerMinecart bukkitSpawnerMinecart = where.getWorld().spawn(where, SpawnerMinecart.class);
        MinecartSpawner nmsSpawnerMinecart = (MinecartSpawner) ((CraftMinecart) bukkitSpawnerMinecart).getHandle();
        BaseSpawner nmsSpawner = nmsSpawnerMinecart.getSpawner();

        nmsSpawner.maxSpawnDelay = 150;
        nmsSpawner.spawnDelay = 0;
        nmsSpawner.spawnRange = 5;
        nmsSpawner.minSpawnDelay = 60;
        nmsSpawner.requiredPlayerRange = 32;
        nmsSpawner.spawnCount = 4;

        nmsSpawner.setEntityId(EntityType.POTION, world, RandomSource.create(), new BlockPos(where.getBlockX(), where.getBlockY(), where.getBlockZ()));
        nmsSpawner.nextSpawnData.getEntityToSpawn().putString("id", "minecraft:potion");

        CompoundTag potion = new CompoundTag();
        potion.putString("id", "minecraft:splash_potion");
        potion.putByte("Count", (byte) 1);

        CompoundTag effectCompound = new CompoundTag();
        effectCompound.putInt("Id", (byte) 7);
        effectCompound.putShort("Amplifier", (byte) 3);
        effectCompound.putInt("Duration", (byte) 1);

        ListTag effectList = new ListTag();
        effectList.add(effectCompound);

        CompoundTag tag = new CompoundTag();
        tag.put("CustomPotionEffects", effectList);

        potion.put("tag", tag);

        nmsSpawner.nextSpawnData.getEntityToSpawn().put("Item", potion);

        Bukkit.broadcastMessage(nmsSpawner.save(new CompoundTag()).getAsString());

        bukkitSpawnerMinecart.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "module_minecart"), PersistentDataType.BYTE, (byte)1);

        CaveSpider spider = where.getWorld().spawn(where, CaveSpider.class);
        Shulker shulker = where.getWorld().spawn(where, Shulker.class);
        shulker.setColor(DyeColor.RED);
        shulker.addPassenger(bukkitSpawnerMinecart);
        spider.addPassenger(shulker);
    }
}
