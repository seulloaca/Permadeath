package tech.sebazcrc.permadeath.nms.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.nms.v1_16_R3.entity.CustomGhast;
import tech.sebazcrc.permadeath.util.NMS;
import tech.sebazcrc.permadeath.util.interfaces.NMSHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server.v1_16_R3." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EntityTypes<?> convertBukkitToNMS(EntityType type) {
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
        if (type == EntityType.ZOMBIFIED_PIGLIN) {
            return EntityTypes.ZOMBIFIED_PIGLIN;
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
        if (type == EntityType.STRIDER) {
            return EntityTypes.STRIDER;
        }
        if (type == EntityType.PIGLIN) {
            return EntityTypes.PIGLIN;
        }
        if (type == EntityType.ZOGLIN) {
            return EntityTypes.ZOGLIN;
        }
        if (type == EntityType.HOGLIN) {
            return EntityTypes.HOGLIN;
        }
        if (type == EntityType.PIGLIN_BRUTE) {
            return EntityTypes.PIGLIN_BRUTE;
        }
        return null;
    }

    @Override
    public Entity spawnNMSEntity(String name, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        if (
                (type != EntityType.BAT && type != EntityType.COD && type != EntityType.SALMON && type != EntityType.SQUID && type != EntityType.PUFFERFISH && type != EntityType.TROPICAL_FISH)
                        || (reason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG || Math.random() <= 0.02004008016)) {

            EntityTypes<?> nms = convertBukkitToNMS(type);
            return nms.spawnCreature(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(), null, null, null, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), EnumMobSpawn.NATURAL, false, false, reason).getBukkitEntity();
        }

        return null;
    }

    @Override
    public Entity spawnNMSCustomEntity(String classPath, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        World nmsWorld = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        net.minecraft.server.v1_16_R3.Entity nmsEntity = null;

        try {
            Class<?> c = Class.forName(NMS.search("entity." + classPath));
            nmsEntity = (net.minecraft.server.v1_16_R3.Entity) c.getConstructor(Location.class).newInstance(location);
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException |
                 InvocationTargetException ignored) {
        }
        if (nmsEntity == null) return null;

        nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        nmsWorld.addEntity(nmsEntity, reason);

        return nmsEntity.getBukkitEntity();
    }

    @Override
    public Entity spawnCustomGhast(Location location, CreatureSpawnEvent.SpawnReason reason, boolean isEnder) {

        World nmsW = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        CustomGhast nmsEntity = new CustomGhast(EntityTypes.GHAST, nmsW);

        nmsEntity.setPosition(location.getX(), location.getY(), location.getZ());
        nmsW.addEntity(nmsEntity, reason);

        if (isEnder) {
            nmsEntity.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(100.0D);
            nmsEntity.setHealth(100.0F);
            nmsEntity.setCustomName(CraftChatMessage.fromStringOrNull("§6Ender Ghast"));
            nmsEntity.getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "ender_ghast"), PersistentDataType.BYTE, (byte) 1);
        }

        return nmsEntity.getBukkitEntity();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addMushrooms() {
        /**
         * Esto hay que cambiarlo XD
         */
    }
}
