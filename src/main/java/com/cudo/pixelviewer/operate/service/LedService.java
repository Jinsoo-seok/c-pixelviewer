package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.config.ParamException;

import java.util.Map;

public interface LedService {
    Map<String, Object> setBrightness(float brightness);
    Map<String, Object> setInputSource(String source) throws ParamException;
    Map<String, Object> loadPreset(String presetNumber);
    Map<String, Object> getLedStatus();
    Map<String, Object> getLedPreset();
}
