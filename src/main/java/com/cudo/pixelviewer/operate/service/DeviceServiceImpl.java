package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService{
    @Override
    public Map<String, Object> setDevicePower(Integer power) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("powerState", power);

        resultMap.put("data", tempMap);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> getTemphumi() {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("temperature", 13.05);
        tempMap.put("humidity", 45.72);

        resultMap.put("data", tempMap);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }
}
