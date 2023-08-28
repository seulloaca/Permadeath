package tech.sebazcrc.permadeath.nms.v1_20_R1;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.nms.v1_20_R1.entity.CustomGhast;
import tech.sebazcrc.permadeath.util.NMS;
import tech.sebazcrc.permadeath.util.interfaces.NMSHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class NMSHandlerImpl implements NMSHandler {
    @Override
    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object convertBukkitToNMS(EntityType type) {
        if (type == EntityType.IRON_GOLEM) {
            return net.minecraft.world.entity.EntityType.IRON_GOLEM;
        }
        if (type == EntityType.SNOWMAN) {
            return net.minecraft.world.entity.EntityType.SNOW_GOLEM;
        }
        if (type == EntityType.WITHER) {
            return net.minecraft.world.entity.EntityType.WITHER;
        }
        if (type == EntityType.CHICKEN) {
            return net.minecraft.world.entity.EntityType.CHICKEN;
        }
        if (type == EntityType.COW) {
            return net.minecraft.world.entity.EntityType.COW;
        }
        if (type == EntityType.MUSHROOM_COW) {
            return net.minecraft.world.entity.EntityType.MOOSHROOM;
        }
        if (type == EntityType.PIG) {
            return net.minecraft.world.entity.EntityType.PIG;
        }
        if (type == EntityType.SHEEP) {
            return net.minecraft.world.entity.EntityType.SHEEP;
        }
        if (type == EntityType.SQUID) {
            return net.minecraft.world.entity.EntityType.SQUID;
        }
        if (type == EntityType.VILLAGER) {
            return net.minecraft.world.entity.EntityType.VILLAGER;
        }
        if (type == EntityType.WANDERING_TRADER) {
            return net.minecraft.world.entity.EntityType.WANDERING_TRADER;
        }
        if (type == EntityType.BAT) {
            return net.minecraft.world.entity.EntityType.BAT;
        }
        if (type == EntityType.OCELOT) {
            return net.minecraft.world.entity.EntityType.OCELOT;
        }
        if (type == EntityType.CAT) {
            return net.minecraft.world.entity.EntityType.CAT;
        }
        if (type == EntityType.DONKEY) {
            return net.minecraft.world.entity.EntityType.DONKEY;
        }
        if (type == EntityType.HORSE) {
            return net.minecraft.world.entity.EntityType.HORSE;
        }
        if (type == EntityType.MULE) {
            return net.minecraft.world.entity.EntityType.MULE;
        }
        if (type == EntityType.SKELETON_HORSE) {
            return net.minecraft.world.entity.EntityType.SKELETON_HORSE;
        }
        if (type == EntityType.ZOMBIE_HORSE) {
            return net.minecraft.world.entity.EntityType.ZOMBIE_HORSE;
        }
        if (type == EntityType.WOLF) {
            return net.minecraft.world.entity.EntityType.WOLF;
        }
        if (type == EntityType.FOX) {
            return net.minecraft.world.entity.EntityType.FOX;
        }
        if (type == EntityType.RABBIT) {
            return net.minecraft.world.entity.EntityType.RABBIT;
        }
        if (type == EntityType.PARROT) {
            return net.minecraft.world.entity.EntityType.PARROT;
        }
        if (type == EntityType.TURTLE) {
            return net.minecraft.world.entity.EntityType.TURTLE;
        }
        if (type == EntityType.COD) {
            return net.minecraft.world.entity.EntityType.COD;
        }
        if (type == EntityType.SALMON) {
            return net.minecraft.world.entity.EntityType.SALMON;
        }
        if (type == EntityType.PUFFERFISH) {
            return net.minecraft.world.entity.EntityType.PUFFERFISH;
        }
        if (type == EntityType.TROPICAL_FISH) {
            return net.minecraft.world.entity.EntityType.TROPICAL_FISH;
        }
        if (type == EntityType.ZOMBIE) {
            return net.minecraft.world.entity.EntityType.ZOMBIE;
        }
        if (type == EntityType.ENDERMAN) {
            return net.minecraft.world.entity.EntityType.ENDERMAN;
        }
        if (type == EntityType.DOLPHIN) {
            return net.minecraft.world.entity.EntityType.DOLPHIN;
        }
        if (type == EntityType.BEE) {
            return net.minecraft.world.entity.EntityType.BEE;
        }
        if (type == EntityType.SPIDER) {
            return net.minecraft.world.entity.EntityType.SPIDER;
        }
        if (type == EntityType.CAVE_SPIDER) {
            return net.minecraft.world.entity.EntityType.CAVE_SPIDER;
        }
        if (type == EntityType.POLAR_BEAR) {
            return net.minecraft.world.entity.EntityType.POLAR_BEAR;
        }
        if (type == EntityType.LLAMA) {
            return net.minecraft.world.entity.EntityType.LLAMA;
        }
        if (type == EntityType.PANDA) {
            return net.minecraft.world.entity.EntityType.PANDA;
        }
        if (type == EntityType.BLAZE) {
            return net.minecraft.world.entity.EntityType.BLAZE;
        }
        if (type == EntityType.CREEPER) {
            return net.minecraft.world.entity.EntityType.CREEPER;
        }
        if (type == EntityType.GHAST) {
            return net.minecraft.world.entity.EntityType.GHAST;
        }
        if (type == EntityType.MAGMA_CUBE) {
            return net.minecraft.world.entity.EntityType.MAGMA_CUBE;
        }
        if (type == EntityType.SILVERFISH) {
            return net.minecraft.world.entity.EntityType.SILVERFISH;
        }
        if (type == EntityType.SKELETON) {
            return net.minecraft.world.entity.EntityType.SKELETON;
        }
        if (type == EntityType.SLIME) {
            return net.minecraft.world.entity.EntityType.SLIME;
        }
        if (type == EntityType.ZOMBIE_VILLAGER) {
            return net.minecraft.world.entity.EntityType.ZOMBIE_VILLAGER;
        }
        if (type == EntityType.DROWNED) {
            return net.minecraft.world.entity.EntityType.DROWNED;
        }
        if (type == EntityType.WITHER_SKELETON) {
            return net.minecraft.world.entity.EntityType.WITHER_SKELETON;
        }
        if (type == EntityType.VINDICATOR) {
            return net.minecraft.world.entity.EntityType.VINDICATOR;
        }
        if (type == EntityType.EVOKER) {
            return net.minecraft.world.entity.EntityType.EVOKER;
        }
        if (type == EntityType.PILLAGER) {
            return net.minecraft.world.entity.EntityType.PILLAGER;
        }
        if (type == EntityType.RAVAGER) {
            return net.minecraft.world.entity.EntityType.RAVAGER;
        }
        if (type == EntityType.WITCH) {
            return net.minecraft.world.entity.EntityType.WITCH;
        }
        if (type == EntityType.VEX) {
            return net.minecraft.world.entity.EntityType.VEX;
        }
        if (type == EntityType.ENDERMITE) {
            return net.minecraft.world.entity.EntityType.ENDERMITE;
        }
        if (type == EntityType.GUARDIAN) {
            return net.minecraft.world.entity.EntityType.GUARDIAN;
        }
        if (type == EntityType.ELDER_GUARDIAN) {
            return net.minecraft.world.entity.EntityType.ELDER_GUARDIAN;
        }
        if (type == EntityType.SHULKER) {
            return net.minecraft.world.entity.EntityType.SHULKER;
        }
        if (type == EntityType.HUSK) {
            return net.minecraft.world.entity.EntityType.HUSK;
        }
        if (type == EntityType.STRAY) {
            return net.minecraft.world.entity.EntityType.STRAY;
        }
        if (type == EntityType.PHANTOM) {
            return net.minecraft.world.entity.EntityType.PHANTOM;
        }
        if (type == EntityType.HOGLIN) {
            return net.minecraft.world.entity.EntityType.HOGLIN;
        }
        if (type == EntityType.ZOGLIN) {
            return net.minecraft.world.entity.EntityType.ZOGLIN;
        }
        if (type == EntityType.PIGLIN) {
            return net.minecraft.world.entity.EntityType.PIGLIN;
        }
        if (type == EntityType.ZOMBIFIED_PIGLIN) {
            return net.minecraft.world.entity.EntityType.ZOMBIFIED_PIGLIN;
        }
        if (type == EntityType.STRIDER) {
            return net.minecraft.world.entity.EntityType.STRIDER;
        }
        if (type == EntityType.PIGLIN_BRUTE) {
            return net.minecraft.world.entity.EntityType.PIGLIN_BRUTE;
        }
        if (type == EntityType.AXOLOTL) {
            return net.minecraft.world.entity.EntityType.AXOLOTL;
        }
        if (type == EntityType.GLOW_SQUID) {
            return net.minecraft.world.entity.EntityType.GLOW_SQUID;
        }
        if (type == EntityType.GOAT) {
            return net.minecraft.world.entity.EntityType.GOAT;
        }
        if (type == EntityType.ALLAY) {
            return net.minecraft.world.entity.EntityType.ALLAY;
        }
        if (type == EntityType.FROG) {
            return net.minecraft.world.entity.EntityType.FROG;
        }
        if (type == EntityType.WARDEN) {
            return net.minecraft.world.entity.EntityType.WARDEN;
        }
        if (type == EntityType.CAMEL) {
            return net.minecraft.world.entity.EntityType.CAMEL;
        }
        if (type == EntityType.SNIFFER) {
            return net.minecraft.world.entity.EntityType.SNIFFER;
        }
        return null;
    }

    @Override
    public Entity spawnNMSEntity(String className, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        if ((type != EntityType.BAT && type != EntityType.COD && type != EntityType.SALMON && type != EntityType.SQUID && type != EntityType.PUFFERFISH && type != EntityType.TROPICAL_FISH)
                        || (reason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG || Math.random() <= 0.02004008016)) {
            net.minecraft.world.entity.EntityType<?> nms = (net.minecraft.world.entity.EntityType<?>) convertBukkitToNMS(type);
            return nms.spawn(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(), new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), MobSpawnType.NATURAL, reason).getBukkitEntity();
        }
        return null;
    }

    @Override
    public Entity spawnNMSCustomEntity(String classPath, EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason) {
        net.minecraft.world.entity.Entity nmsEntity = null;

        try {
            Class<?> c = Class.forName(NMS.search("entity." + classPath));
            nmsEntity = (net.minecraft.world.entity.Entity) c.getConstructor(Location.class).newInstance(location);

        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException |
                 InvocationTargetException ignored) {
        }
        if (nmsEntity == null) return null;

        nmsEntity.setPos(location.getX(), location.getY(), location.getZ());
        nmsEntity.setXRot(location.getPitch());
        nmsEntity.setYRot(location.getYaw());
        ((CraftWorld) location.getWorld()).addEntityToWorld(nmsEntity, reason);

        return nmsEntity.getBukkitEntity();
    }

    @Override
    public Entity spawnCustomGhast(Location location, CreatureSpawnEvent.SpawnReason reason, boolean isEnder) {
        ServerLevel nmsWorld = ((CraftWorld)location.getWorld()).getHandle();

        CustomGhast ghast = new CustomGhast(net.minecraft.world.entity.EntityType.GHAST, nmsWorld);
        ghast.setPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        nmsWorld.addFreshEntity(ghast);

        if (isEnder) {
            ghast.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
            ghast.setHealth(100.0F);
            ghast.setCustomName(CraftChatMessage.fromStringOrNull("§6Ender Ghast"));
            ghast.setCustomNameVisible(false);
            ghast.getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "ender_ghast"), PersistentDataType.BYTE, (byte) 1);
        }
        return ghast.getBukkitEntity();
    }

    @Override
    public void addMushrooms() {
        // TODO
    }
}
