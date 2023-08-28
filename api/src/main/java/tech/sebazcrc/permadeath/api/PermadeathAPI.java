package tech.sebazcrc.permadeath.api;

import tech.sebazcrc.permadeath.data.DateManager;

public class PermadeathAPI {
    public static long getDay() {
        return DateManager.getInstance().getDay();
    }
}
