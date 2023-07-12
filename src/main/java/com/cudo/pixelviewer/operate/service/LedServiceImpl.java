package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.component.DeviceControllerClient;
import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.mapper.LedMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedStatusVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.cudo.pixelviewer.util.TcpClientUtil.*;

@Service
@RequiredArgsConstructor
public class LedServiceImpl implements LedService {
    final LedControllerClient ledControllerClient;

    final DeviceControllerClient deviceControllerClient;

    final LedMapper ledMapper;

    final static String PRESET_NAME_PREFIX = "프리셋";

    @Override
    public Map<String, Object> setBrightness(float brightness) {
        Map<String, Object> responseMap = new HashMap<>();

        String light = floatToHex(brightness);

        byte[] lightMessage = getLightByte(light);

        try {
//            ledControllerClient.sendMessage(lightMessage);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> setInputSource(String source) throws ParamException {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // TODO 레이어 번호 어떻게 설정하는지 확인 필요
            byte[] message = {0x33, 0x00, 0x12, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};

            int hexValue = getInputSourceCode(source);

            message[17] = (byte) hexValue;

//            ledControllerClient.sendMessage(message);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loadPreset(String presetNumber) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            byte[] message = {0x74, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            int presetValue = Integer.decode(presetNumber);
            message[16] = (byte) presetValue;

//            ledControllerClient.sendMessage(message);

            Integer updateLastLoad = ledMapper.putLedPreset(presetNumber); // 프리셋 마지막 실행시간 업데이트

            if (updateLastLoad > 0) {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
            }

        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> getLedStatus() {
        Map<String, Object> responseMap = new HashMap<>();

        /* TODO
                1. 전원 ON/OFF       -> 컨트롤 유닛
                2. LED 컨트롤 프리셋  -> 따로 DB 저장해서 가지고 있기 (프리셋 실행 할 때 마지막거 실행한 번호 업데이트)
                3. 영상 입력소스      -> 88
                4. LED 밝기 상태     -> 23
             */

        try {
            byte[] ledStatusMessage = {0x01, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x16}; // LED 컨트롤러 상태 조회

            // 밝기 / 입력소스 조회
//            CompletableFuture<byte[]> ledControllerFuture = ledControllerClient.sendMessage(ledStatusMessage);
//            ledControllerFuture.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            byte[] ledHexResponse = ledControllerFuture.join(); // 응답 값 비동기 수신

            // TODO 하드 코딩 삭제
            byte[] ledHexResponse = {(byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0xAB, (byte) 0xAB, (byte) 0xAB, (byte) 0xAB, (byte) 0x16, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x1B, (byte) 0x14, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x3F, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x3F, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x3F, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x3F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x70, (byte) 0x43, (byte) 0x3B, (byte) 0xDF, (byte) 0x2F, (byte) 0x3F, (byte) 0x89, (byte) 0x41, (byte) 0xA0, (byte) 0x3E, (byte) 0x00, (byte) 0xC0, (byte) 0x0E, (byte) 0x44, (byte) 0x12, (byte) 0x83, (byte) 0x40, (byte) 0x3E, (byte) 0x19, (byte) 0x04, (byte) 0x36, (byte) 0x3F, (byte) 0x00, (byte) 0x00, (byte) 0x58, (byte) 0x42, (byte) 0xBA, (byte) 0x49, (byte) 0x0C, (byte) 0x3E, (byte) 0x60, (byte) 0xE5, (byte) 0x50, (byte) 0x3D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            float light = -1; // 밝기

            String inputSource = "";

            if (ledHexResponse.length >= 88) {
                StringBuilder lightHexReverse = new StringBuilder();

                for (int i = 26; i >= 23; i--) {
                    lightHexReverse.append(String.format("%02X", ledHexResponse[i]));
                }

                light = hexToFloat(lightHexReverse.toString());
                inputSource = String.format("%02X", ledHexResponse[88]);
            }

            byte[] powerMessage = {0x02, (byte) 0xFF, (byte) 0x24, 0x00, 0x00, (byte) 0xDB, 0x03}; // 전원 상태 확인

            // 전원 상태 조회
//            CompletableFuture<byte[]> unitControllerFuture = deviceControllerClient.sendMessage(powerMessage);
//            unitControllerFuture.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            byte[] powerHexResponse = unitControllerFuture.join(); // 응답 값 비동기 수신
            // TODO 하드코딩 삭제 / 여러개의 값 동시에 진행

            byte[] powerHexResponse = {0x02, (byte) 0xFF, 0x06, 0x00, 0x02, 0x24, 0x01, (byte) 0xDF, 0x03};

            int powerState = -1;

            if (powerHexResponse.length >= 6) {
                powerState = Integer.parseInt(String.format("%02X", powerHexResponse[6]));
            }

            // LED 컨트롤 프리셋 조회
            String presetNumber = ledMapper.getLastLoadPreset();

            if (powerState != -1 && light != -1 && presetNumber != null) {
                // TODO 입력소스 수정 필요
                LedStatusVo ledStatusVo = LedStatusVo.builder()
                        .inputSource("HDMI1")
                        .powerState(powerState)
                        .brightness(light)
                        .presetNumber(presetNumber)
                        .build();

                responseMap.put("data", ledStatusVo);
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
            }
        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> getLedPreset() {
        Map<String, Object> responseMap = new HashMap<>();

        List<Map<String, Object>> ledPresetList = ledMapper.getLedPresetList();

        if (ledPresetList.size() > 0) {
            responseMap.put("data", ledPresetList);
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putLedPresetName(Map<String, Object> param) {
        Map<String, Object> responseMap = new HashMap<>();

        Integer updateCount = ledMapper.putLedPresetName(param);

        if (updateCount > 0) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }
}
