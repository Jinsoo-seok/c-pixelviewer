package com.cudo.pixelviewer.user.service;

import com.cudo.pixelviewer.user.mapper.UserMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLogin(Map<String, Object> param){
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.putAll(ParameterUtils.responseOption("SUCCESS"));

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLogout(Map<String, Object> param){
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.putAll(ParameterUtils.responseOption("SUCCESS"));

        return resultMap;
    }

}
