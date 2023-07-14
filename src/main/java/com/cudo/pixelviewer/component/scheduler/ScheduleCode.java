package com.cudo.pixelviewer.component.scheduler;

public enum ScheduleCode {
    POWER_PREFIX("POWER_PREFIX", "power"),
    LED_PREFIX("LED_PREFIX", "led"),
    LIGHT_PREFIX("LIGHT_PREFIX", "light"),

    DATA_MAP_KEY("key", "key"),

    POWER_ON("key", "powerOn"),
    POWER_OFF("key", "powerOff"),

    LIGHT("key", "light"),

    LED_PLAY_LIST_START("key", "ledPlayListStart"),
    LED_PLAY_LIST_END("key", "ledPlayListEnd");


    private final String code;
    private final String value;

    ScheduleCode(String code, String value){
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
