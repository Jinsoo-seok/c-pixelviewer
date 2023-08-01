package com.cudo.pixelviewer.externals.service;

import java.util.Map;

public interface ExternalsService {

//    Map<String, Object> getExternalWeather();

//    Map<String, Object> getExternalAir();

    void getExternalWeather(String coords);

    void getExternalAir(String stationName);


}