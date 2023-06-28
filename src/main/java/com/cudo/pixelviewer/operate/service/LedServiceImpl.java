package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedStatusVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LedServiceImpl implements LedService{
    @Override
    public Map<String, Object> setBrightness(Double brightness) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("brightness", brightness);

        resultMap.put("data", tempMap);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> setInputSource(String source) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("inputSource", source);

        resultMap.put("data", tempMap);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> loadPreset(Integer presetNumber) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("inputSource", presetNumber);

        resultMap.put("data", tempMap);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> getLedStatus() {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        LedStatusVo ledStatusVo = LedStatusVo.builder()
                .inputSource("HDMI1")
                .powerState(1)
                .brightness(0.51)
                .presetNumber(3)
                .build();

        resultMap.put("data", ledStatusVo);
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }
}
