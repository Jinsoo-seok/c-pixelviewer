package com.cudo.pixelviewer.setting.service;

import java.util.Map;

public interface SettingService {

//    Map<String, Object> serviceGetValue (Map<String, Object> param);
    Map<String, Object> serviceGetValue ();

    Map<String, Object> putSettingImageDefaultPlaytime(Map<String, Object> param);

}
