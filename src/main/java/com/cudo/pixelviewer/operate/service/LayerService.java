package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface LayerService {

    Map<String, Object> getLayerList();

    Map<String, Object> getLayer(String layerId);

    Map<String, Object> postLayer(Map<String, Object> param);

    Map<String, Object> deleteLayer(Map<String, Object> param);

    Map<String, Object> putLayer(Map<String, Object> param);

}