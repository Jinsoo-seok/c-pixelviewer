package com.cudo.pixelviewer.bo.service;

import java.util.Map;

public interface LedconService {

    Map<String, Object> getLedconList();

    Map<String, Object> postLedcon(Map<String, Object> param);

    Map<String, Object> deleteLedcon(Map<String, Object> param);

    Map<String, Object> putLedcon(Map<String, Object> param);

}