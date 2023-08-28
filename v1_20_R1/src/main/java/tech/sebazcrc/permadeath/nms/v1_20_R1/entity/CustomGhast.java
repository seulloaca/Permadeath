package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

public class CustomGhast extends Ghast {

    public CustomGhast(EntityType<? extends Ghast> type, Level world) {
        super(type, world);
    }
}