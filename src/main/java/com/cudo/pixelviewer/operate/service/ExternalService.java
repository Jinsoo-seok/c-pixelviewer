package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface ExternalService {

    Map<String, Object> getExternalVideo(String screenId);

    Map<String, Object> postExternalVideo(Map<String, Object> param);

    Map<String, Object> postExternalInfo(Map<String, Object> param);

}