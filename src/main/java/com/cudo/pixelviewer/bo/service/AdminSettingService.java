package com.cudo.pixelviewer.bo.service;

import java.util.Map;

public interface AdminSettingService {

    Map<String, Object> getAdminSettingList();

    Map<String, Object> putAdminSetting(Map<String, Object> param);


    Map<String, Object> patchLayerTopMost(Map<String, Object> param);

    Map<String, Object> patchTempHumi(Map<String, Object> param);

    Map<String, Object> patchControlType(Map<String, Object> param);

    Map<String, Object> patchLedPresetEnable(Map<String, Object> param);

    Map<String, Object> patchLedPresetCount(Map<String, Object> param);

    Map<String, Object> patchLedInputEnable(Map<String, Object> param);



    Map<String, Object> getDisplayInfoList();

    Map<String, Object> getDisplayInfo(String displayId);

    Map<String, Object> postDisplayInfo(Map<String, Object> param);

    Map<String, Object> putDisplayInfo(Map<String, Object> param);

    Map<String, Object> deleteDisplayInfo(Map<String, Object> param);

}