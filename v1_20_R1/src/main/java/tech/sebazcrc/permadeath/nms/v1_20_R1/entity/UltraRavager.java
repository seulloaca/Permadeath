package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ravager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

public class UltraRavager extends Ravager {

    public UltraRavager(Location loc) {
        super(EntityType.RAVAGER, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        this.setPersistenceRequired(false);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }
}