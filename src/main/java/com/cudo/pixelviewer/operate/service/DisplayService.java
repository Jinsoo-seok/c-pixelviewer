package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface DisplayService {

    Map<String, Object> getDisplayList(String screenId);

    Map<String, Object> getDisplay(String displayId);

    Map<String, Object> getDisplayPortlist();

    Map<String, Object> patchDisplayTestpattern(Map<String, Object> param);

}