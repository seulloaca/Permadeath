package tech.sebazcrc.permadeath.util;

public enum MinecraftVersion {
    v1_15_R1,
    v1_16_R3,
    v1_20_R1;

    private static final String REVISION_PATTERN = "_R\\d";

    public boolean isAboveOrEqual(MinecraftVersion compare) {
        return ordinal() >= compare.ordinal();
    }

    public boolean isSubVersionOf(MinecraftVersion version) {
        return version.name().split("_")[1].equalsIgnoreCase(name().split("_")[1]);
    }

    public String getFormattedName() {
        return this.name().replaceAll(REVISION_PATTERN, "").replace("_", ".");
    }
}
