package tech.sebazcrc.permadeath.api;

import net.minecraft.world.entity.EntityLiving;
import tech.sebazcrc.permadeath.util.manager.Data.DateManager;

public class PermadeathAPI {
    public static long getDay() {
        return DateManager.getInstance().getDay();
    }
}
