package tech.sebazcrc.permadeath.nms.v1_15_R1;

import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.nms.v1_15_R1.entity.CustomGhast;
import tech.sebazcrc.permadeath.util.NMS;
import tech.sebazcrc.permadeath.util.interfaces.NMSHandler;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server.v1_15_R1." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EntityTypes convertBukkitToNMS(EntityType type) {
        if (type == EntityType.IRON_GOLEM) {
            return EntityTypes.IRON_GOLEM;
        }
        if (type == EntityType.SNOWMAN) {
            return EntityTypes.SNOW_GOLEM;
        }
        if (type == EntityType.WITHER) {
            return EntityTypes.WITHER;
        }

        if (type == EntityType.CHICKEN) {

            return EntityTypes.CHICKEN;
        }

        if (type == EntityType.COW) {

            return EntityTypes.COW;
        }
        if (type == EntityType.MUSHROOM_COW) {

            return EntityTypes.MOOSHROOM;
        }
        if (type == EntityType.PIG) {
            return EntityTypes.PIG;
        }
        if (type == EntityType.PIG_ZOMBIE) {
            return EntityTypes.ZOMBIE_PIGMAN;
        }
        if (type == EntityType.SHEEP) {

            return EntityTypes.SHEEP;
        }
        if (type == EntityType.SQUID) {

            return EntityTypes.SQUID;
        }
        if (type == EntityType.VILLAGER) {

            return EntityTypes.VILLAGER;
        }
        if (type == EntityType.WANDERING_TRADER) {

            return EntityTypes.WANDERING_TRADER;
        }
        if (type == EntityType.BAT) {

            return EntityTypes.BAT;
        }
        if (type == EntityType.OCELOT) {

            return EntityTypes.OCELOT;
        }
        if (type == EntityType.CAT) {

            return EntityTypes.CAT;
        }

        if (type == EntityType.DONKEY) {

            return EntityTypes.DONKEY;
        }

        if (type == EntityType.HORSE) {

            return EntityTypes.HORSE;
        }

        if (type == EntityType.MULE) {

            return EntityTypes.MULE;
        }

        if (type == EntityType.SKELETON_HORSE) {

            return EntityTypes.SKELETON_HORSE;
        }

        if (type == EntityType.ZOMBIE_HORSE) {

            return EntityTypes.ZOMBIE_HORSE;
        }

        if (type == EntityType.WOLF) {

            return EntityTypes.WOLF;
        }

        if (type == EntityType.FOX) {

            return EntityTypes.FOX;
        }

        if (type == EntityType.RABBIT) {

            return EntityTypes.RABBIT;
        }

        if (type == EntityType.PARROT) {

            return EntityTypes.PARROT;
        }

        if (type == EntityType.TURTLE) {

            return EntityTypes.TURTLE;
        }

        if (type == EntityType.COD) {

            return EntityTypes.COD;
        }

        if (type == EntityType.SALMON) {

            return EntityTypes.SALMON;
        }

        if (type == EntityType.PUFFERFISH) {

            return EntityTypes.PUFFERFISH;
        }

        if (type == EntityType.TROPICAL_FISH) {

            return EntityTypes.TROPICAL_FISH;
        }

        if (type == EntityType.ZOMBIE) {

            return EntityTypes.ZOMBIE;
        }

        if (type == EntityType.ENDERMAN) {

            return EntityTypes.ENDERMAN;
        }

        if (type == EntityType.DOLPHIN) {

            return EntityTypes.DOLPHIN;
        }

        if (type == EntityType.BEE) {
            return EntityTypes.BEE;
        }
        if (type == EntityType.SPIDER) {

            return EntityTypes.SPIDER;
        }
        if (type == EntityType.CAVE_SPIDER) {

            return EntityTypes.CAVE_SPIDER;
        }
        if (type == EntityType.POLAR_BEAR) {

            return EntityTypes.POLAR_BEAR;
        }
        if (type == EntityType.LLAMA) {

            return EntityTypes.LLAMA;
        }
        if (type == EntityType.PANDA) {

            return EntityTypes.PANDA;
        }
        if (type == EntityType.BLAZE) {

            return EntityTypes.BLAZE;
        }
        if (type == EntityType.CREEPER) {

            return EntityTypes.CREEPER;
        }
        if (type == EntityType.GHAST) {

            return EntityTypes.GHAST;
        }
        if (type == EntityType.MAGMA_CUBE) {

            return EntityTypes.MAGMA_CUBE;
        }
        if (type == EntityType.SILVERFISH) {

            return EntityTypes.SILVERFISH;
        }
        if (type == EntityType.SKELETON) {

            return EntityTypes.SKELETON;
        }
        if (type == EntityType.SLIME) {

            return EntityTypes.SLIME;
        }
        if (type == EntityType.ZOMBIE_VILLAGER) {

            return EntityTypes.ZOMBIE_VILLAGER;
        }
        if (type == EntityType.DROWNED) {

            return EntityTypes.DROWNED;
        }
        if (type == EntityType.WITHER_SKELETON) {

            return EntityTypes.WITHER_SKELETON;
        }
        if (type == EntityType.VINDICATOR) {

            return EntityTypes.VINDICATOR;
        }
        if (type == EntityType.EVOKER) {

            return EntityTypes.EVOKER;
        }

        if (type == EntityType.PILLAGER) {

            return EntityTypes.PILLAGER;
        }

        if (type == EntityType.RAVAGER) {

            return EntityTypes.RAVAGER;
        }

        if (type == EntityType.WITCH) {

            return EntityTypes.WITCH;
        }

        if (type == EntityType.VEX) {

            return EntityTypes.VEX;
        }

        if (type == EntityType.ENDERMITE) {

            return EntityTypes.ENDERMITE;
        }

        if (type == EntityType.GUARDIAN) {

            return EntityTypes.GUARDIAN;
        }

        if (type == EntityType.ELDER_GUARDIAN) {

            return EntityTypes.ELDER_GUARDIAN;
        }

        if (type == EntityType.SHULKER) {

            return EntityTypes.SHULKER;
        }

        if (type == EntityType.HUSK) {

            return EntityTypes.HUSK;
        }

        if (type == EntityType.STRAY) {

            return EntityTypes.STRAY;
        }

        if (type == EntityType.PHANTOM) {

            return EntityTypes.PHANTOM;
        }
        return null;
    }

    @Override
    public org.bukkit.entity.Entity spawnNMSEntity(String name, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        if (
                (type != EntityType.BAT && type != EntityType.COD && type != EntityType.SALMON && type != EntityType.SQUID && type != EntityType.PUFFERFISH && type != EntityType.TROPICAL_FISH)
                        || (reason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG || Math.random() <= 0.02004008016)) {
            try {
                World nmsWorld = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
                net.minecraft.server.v1_15_R1.Entity nmsEntity = (net.minecraft.server.v1_15_R1.Entity) getNMSClass("Entity" + name).getConstructor(getNMSClass("EntityTypes"), getNMSClass("World")).newInstance(convertBukkitToNMS(type), nmsWorld);
                nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                nmsWorld.addEntity(nmsEntity, reason);

                return nmsEntity.getBukkitEntity();

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                     InstantiationException ignored) {
            }
        }

        return null;
    }

    @Override
    public org.bukkit.entity.Entity spawnNMSCustomEntity(String classPath, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        World nmsWorld = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        net.minecraft.server.v1_15_R1.Entity nmsEntity = null;

        try {
            Class<?> c = Class.forName(NMS.search("entity." + classPath));
            nmsEntity = (net.minecraft.server.v1_15_R1.Entity) c.getConstructor(Location.class).newInstance(location);

        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException |
                 InvocationTargetException ignored) {
        }
        if (nmsEntity == null) return null;

        nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        nmsWorld.addEntity(nmsEntity, reason);

        return nmsEntity.getBukkitEntity();
    }

    @Override
    public org.bukkit.entity.Entity spawnCustomGhast(Location location, CreatureSpawnEvent.SpawnReason reason, boolean isEnder) {

        World nmsW = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        CustomGhast nmsEntity = new CustomGhast(EntityTypes.GHAST, nmsW);

        nmsEntity.setPosition(location.getX(), location.getY(), location.getZ());
        nmsW.addEntity(nmsEntity, reason);

        if (isEnder) {

            nmsEntity.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(100.0D);
            LivingEntity e2 = (LivingEntity) nmsEntity.getBukkitEntity();
            e2.setHealth(100.0D);
            e2.setCustomName("§6Ender Ghast");
            e2.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "ender_ghast"), PersistentDataType.BYTE, (byte) 1);
            e2.setCustomNameVisible(false);
        }

        return nmsEntity.getBukkitEntity();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addMushrooms() {
        /**
         * Esto hay que cambiarlo XD
         */
        Class<BiomeBase> c = BiomeBase.class;

        try {
            BiomeMushrooms m = BiomeMushrooms.class.getConstructor().newInstance();

            final Field f = c.getDeclaredField("v");
            f.setAccessible(true);

            final Map<EnumCreatureType, List<BiomeBase.BiomeMeta>> v = (Map<EnumCreatureType, List<BiomeBase.BiomeMeta>>) f.get(m);

            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
            v.get(EnumCreatureType.MONSTER).add(new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));

            f.set(m, v);
        } catch (Exception e) {}
    }
}
