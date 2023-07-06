package com.cudo.pixelviewer.bo.service;

import java.util.Map;

public interface AdminSettingService {

    Map<String, Object> getAdminSettingList();

    Map<String, Object> putAdminSetting(Map<String, Object> param);


    Map<String, Object> getDisplayInfoList();

    Map<String, Object> getDisplayInfo(String displayId);

    Map<String, Object> postDisplayInfo(Map<String, Object> param);

    Map<String, Object> putDisplayInfo(Map<String, Object> param);

    Map<String, Object> deleteDisplayInfo(Map<String, Object> param);

}