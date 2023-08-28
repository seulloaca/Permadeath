package tech.sebazcrc.permadeath.util.interfaces;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public interface NMSHandler {

    Class<?> getNMSClass(String name);

    Object convertBukkitToNMS(EntityType ogType);

    Entity spawnNMSEntity(String className, EntityType type, org.bukkit.Location location, CreatureSpawnEvent.SpawnReason reason);

    Entity spawnNMSCustomEntity(String classPath, EntityType type, org.bukkit.Location location, CreatureSpawnEvent.SpawnReason reason);

    Entity spawnCustomGhast(org.bukkit.Location location, CreatureSpawnEvent.SpawnReason reason, boolean isEnder);

    void addMushrooms();
}