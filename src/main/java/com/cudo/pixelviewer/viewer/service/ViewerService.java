package com.cudo.pixelviewer.viewer.service;

import java.util.Map;

public interface ViewerService {

    Map<String, Object> getPlayInfo(String screenId, String presetId, String layerId);

}