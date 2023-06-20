package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface DisplayService {

    Map<String, Object> getDisplayList();

    Map<String, Object> getDisplay(String screenId);

    Map<String, Object> getDisplayPortlist();

    Map<String, Object> patchDisplayTestpattern(Map<String, Object> param);

}