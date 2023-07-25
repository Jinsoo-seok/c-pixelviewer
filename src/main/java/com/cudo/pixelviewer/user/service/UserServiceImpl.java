package com.cudo.pixelviewer.user.service;

import com.cudo.pixelviewer.user.mapper.UserMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.UserVo;
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

        UserVo userVo = userMapper.postLogin(param);
        Boolean loginYn = false;

        if(userVo!=null){
            if(userVo.getPassword().equals(param.get("userPw"))){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                loginYn = true;
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INVALID_USER_PASSWORD.getCode());
                resultMap.put("message", ResponseCode.FAIL_INVALID_USER_PASSWORD.getMessage());
                loginYn = false;
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_INVALID_USER_ID.getCode());
            resultMap.put("message", ResponseCode.FAIL_INVALID_USER_ID.getMessage());
            loginYn = false;
        }
        resultMap.put("data", loginYn);

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
