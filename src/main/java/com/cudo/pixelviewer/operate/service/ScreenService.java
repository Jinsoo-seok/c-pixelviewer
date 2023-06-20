package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface ScreenService {

    Map<String, Object> getScreenList();

    Map<String, Object> getScreen(String screenId);

    Map<String, Object> postScreen(Map<String, Object> param);

    Map<String, Object> deleteScreen(Map<String, Object> param);

    Map<String, Object> patchScreenName(Map<String, Object> param);

    Map<String, Object> putScreenSet(Map<String, Object> param);

}
