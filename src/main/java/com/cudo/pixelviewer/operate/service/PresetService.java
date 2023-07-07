package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface PresetService {

    Map<String, Object> getPresetList();

    Map<String, Object> getPreset(String screenId);

    Map<String, Object> postPreset(Map<String, Object> param);

    Map<String, Object> deletePreset(Map<String, Object> param);

    Map<String, Object> patchPresetName(Map<String, Object> param);

    Map<String, Object> putPreset(Map<String, Object> param);

    Map<String, Object> patchPresetRun(Map<String, Object> param);

    Map<String, Object> patchPresetControl(Map<String, Object> param);

}