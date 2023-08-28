package tech.sebazcrc.permadeath.nms.v1_16_R3.entity;

import net.minecraft.server.v1_16_R3.EntityMinecartMobSpawner;
import net.minecraft.server.v1_16_R3.MobSpawnerAbstract;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMinecart;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.interfaces.DeathModule;

import java.lang.reflect.Field;

public class DeathModuleImpl implements DeathModule {
    @Override
    public void spawn(Location where) {

        CaveSpider spider = where.getWorld().spawn(where, CaveSpider.class);
        Shulker shulker = where.getWorld().spawn(where, Shulker.class);
        shulker.setColor(DyeColor.RED);

        SpawnerMinecart spawnerMinecart = where.getWorld().spawn(where, SpawnerMinecart.class);
        EntityMinecartMobSpawner nms = (EntityMinecartMobSpawner) ((CraftMinecart)spawnerMinecart).getHandle();

        try {
            final Field spawner = nms.getClass().getDeclaredField("b");
            spawner.setAccessible(true);

            MobSpawnerAbstract ms = (MobSpawnerAbstract) spawner.get(nms);

            ms.maxSpawnDelay = 150;
            ms.spawnDelay = 0;
            ms.spawnRange = 5;
            ms.minSpawnDelay = 60;
            ms.requiredPlayerRange = 32;
            ms.spawnCount = 4;

            ms.spawnData.getEntity().setString("id", "minecraft:potion");

            NBTTagCompound potion = new NBTTagCompound();
            potion.setString("id", "minecraft:splash_potion");
            potion.setByte("Count", (byte) 1);

            ms.spawnData.getEntity().set("Potion", potion);

            NBTTagCompound effects = new NBTTagCompound();
            effects.setByte("Id", (byte) 7);
            effects.setByte("Amplifier", (byte) 3);
            effects.setInt("Duration", 1);

            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("Potion", "minecraft:harming");
            tag.set("CustomPotionEffects", effects);
            ms.spawnData.getEntity().getCompound("Potion").set("tag", tag);

            spawner.set(nms, ms);

        } catch (Exception x) {
            x.printStackTrace();
        }

        spawnerMinecart.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "module_minecart"), PersistentDataType.BYTE, (byte)1);

        shulker.addPassenger(spawnerMinecart);
        spider.addPassenger(shulker);
    }
}
