package com.cudo.pixelviewer.component.scheduler;

public enum ScheduleCode {
    POWER_PREFIX("POWER_PREFIX", "power"),
    LED_PREFIX("LED_PREFIX", "led"),
    LIGHT_PREFIX("LIGHT_PREFIX", "light"),

    DATA_MAP_KEY("key", "key"),

    POWER_ON("key", "powerOn"),
    POWER_OFF("key", "powerOff"),

    LIGHT("key", "light"),

    LED_PLAY("key", "ledPlay");


    private String code;
    private String value;

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
