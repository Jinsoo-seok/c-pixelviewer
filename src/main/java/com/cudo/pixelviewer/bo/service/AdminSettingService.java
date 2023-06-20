package com.cudo.pixelviewer.bo.service;

import java.util.Map;

public interface AdminSettingService {

    Map<String, Object> getAdminSettingList();

    Map<String, Object> patchLayerTopMost(Map<String, Object> param);

    Map<String, Object> patchTempHumi(Map<String, Object> param);

    Map<String, Object> patchControlType(Map<String, Object> param);

    Map<String, Object> patchLedPresetEnable(Map<String, Object> param);

    Map<String, Object> patchLedPresetCount(Map<String, Object> param);

    Map<String, Object> patchLedInputEnable(Map<String, Object> param);

}