package com.cudo.pixelviewer.user.service;

import java.util.Map;

public interface UserService {

    Map<String, Object> postLogin(Map<String, Object> param);

    Map<String, Object> postLogout(Map<String, Object> param);

}
