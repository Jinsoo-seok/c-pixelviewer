package com.cudo.pixelviewer.setting.service;

import java.util.Map;

public interface SettingService {

    Map<String, Object> serviceGetValue ();

    Map<String, Object> patchSettingImageDefaultPlaytime(Map<String, Object> param);

}
