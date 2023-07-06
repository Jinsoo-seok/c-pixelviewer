package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface LedService {
    Map<String, Object> setBrightness(Double brightness);
    Map<String, Object> setInputSource(String source);
    Map<String, Object> loadPreset(Integer presetNumber);
    Map<String, Object> getLedStatus();
    Map<String, Object> getLedPreset();
}
