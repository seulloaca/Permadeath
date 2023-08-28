package tech.sebazcrc.permadeath.util;

import lombok.Getter;
import org.bukkit.Location;
import tech.sebazcrc.permadeath.util.interfaces.DeathModule;
import tech.sebazcrc.permadeath.util.interfaces.InfernalNetheriteBlock;
import tech.sebazcrc.permadeath.util.interfaces.NMSAccessor;
import tech.sebazcrc.permadeath.util.interfaces.NMSHandler;

import java.lang.reflect.InvocationTargetException;

public class NMS {
    @Getter
    private static NMSAccessor accessor;
    @Getter
    private static NMSHandler handler;
    @Getter
    private static InfernalNetheriteBlock netheriteBlock;

    private static Class<?> deathModuleClass;
    static {
        try {
            deathModuleClass = Class.forName(search("entity.DeathModuleImpl"));
        } catch (Exception ignored) {}
    }

    public static void loadNMSAccessor() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        accessor = (NMSAccessor) Class.forName(search("NMSAccessorImpl")).getConstructor().newInstance();
    }

    public static void loadNMSHandler() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        handler = (NMSHandler) Class.forName(search("NMSHandlerImpl")).getConstructor().newInstance();
    }

    public static void loadInfernalNetheriteBlock() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        netheriteBlock = (InfernalNetheriteBlock) Class.forName(search("block.InfernalNetheriteBlockImpl")).getConstructor().newInstance();
    }

    public static String search(String classPath) {
        return search(VersionManager.getRev(), classPath);
    }

    public static String search(String rev, String classPath) {
        return String.format("tech.sebazcrc.permadeath.nms.v%s.%s", rev, classPath);
    }

    public static void spawnDeathModule(Location location) {
        try {
            DeathModule module = (DeathModule) deathModuleClass.getConstructor().newInstance();
            module.spawn(location);
        } catch (Exception x) {
        }
    }
}
