package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface DeviceService {
    Map<String, Object> setDevicePower(Integer power);

    Map<String, Object> getTemphumi();
}
