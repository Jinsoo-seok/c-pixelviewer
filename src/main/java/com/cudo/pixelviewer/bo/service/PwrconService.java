package com.cudo.pixelviewer.bo.service;

import java.util.Map;

public interface PwrconService {

    Map<String, Object> getPwrconList();

    Map<String, Object> postPwrcon(Map<String, Object> param);

    Map<String, Object> deletePwrcon(Map<String, Object> param);

    Map<String, Object> putPwrcon(Map<String, Object> param);

}