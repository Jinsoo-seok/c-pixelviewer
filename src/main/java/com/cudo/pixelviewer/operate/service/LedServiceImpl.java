package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.bo.mapper.LedconMapper;
import com.cudo.pixelviewer.bo.mapper.PwrconMapper;
import com.cudo.pixelviewer.component.DeviceControllerClient;
import com.cudo.pixelviewer.component.InputSourceComponent;
import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.mapper.LedMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedconVo;
import com.cudo.pixelviewer.vo.PwrconVo;
import com.cudo.pixelviewer.vo.ResponseWithIpVo;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cudo.pixelviewer.util.TcpClientUtil.*;

@Service
@RequiredArgsConstructor
public class LedServiceImpl implements LedService {
    final LedControllerClient ledControllerClient;

    final DeviceControllerClient deviceControllerClient;

    final LedMapper ledMapper;

    final PwrconMapper pwrconMapper;

    final LedconMapper ledconMapper;

    final InputSourceComponent inputSourceComponent;

    @Override
    public Map<String, Object> setBrightness(float brightness) {
        Map<String, Object> responseMap = new HashMap<>();

        String light = floatToHex(brightness);
        byte[] lightMessage = getLightByte(light);

        try {
            Map<Channel, CompletableFuture<ResponseWithIpVo>> ledControllerChannelMap = ledControllerClient.getChannelFutureMap();

            if (ledControllerChannelMap.size() > 0) {
                ledControllerClient.sendMessage(lightMessage);

                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                responseMap.put("exceptionMessage", "No active LED Controller Connection");
            }
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
            Map<Channel, CompletableFuture<ResponseWithIpVo>> ledControllerChannelMap = ledControllerClient.getChannelFutureMap();

            if (ledControllerChannelMap.size() > 0) {
                byte[] message = {0x33, 0x00, 0x12, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};

                int hexValue = inputSourceComponent.getInputSourceCode(source);

                message[17] = (byte) hexValue;

                ledControllerClient.sendMessage(message);

                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                responseMap.put("exceptionMessage", "No active LED Controller Connection");
            }
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
            Map<Channel, CompletableFuture<ResponseWithIpVo>> ledControllerChannelMap = ledControllerClient.getChannelFutureMap();

            if (ledControllerChannelMap.size() > 0) {
                byte[] message = {0x74, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

                int presetValue = Integer.decode(presetNumber);
                message[16] = (byte) presetValue;

                ledControllerClient.sendMessage(message);

                Integer updateLastLoad = ledMapper.putLedPreset(presetNumber); // 프리셋 마지막 실행시간 업데이트

                if (updateLastLoad > 0) {
                    responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    responseMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
                }
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                responseMap.put("exceptionMessage", "No active LED Controller Connection");
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
        List<Map<String, Object>> ledControllerList = new ArrayList<>();
        List<Map<String, Object>> unitControllerList = new ArrayList<>();

        try {
            byte[] ledStatusMessage = {0x01, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x16}; // LED 컨트롤러 상태 조회

            // 밝기/입력소스 조회
            CompletableFuture<ResponseWithIpVo[]> ledControllerFuture = ledControllerClient.sendMessage(ledStatusMessage);
            ResponseWithIpVo[] ledHexResponse = ledControllerFuture.get(10, TimeUnit.SECONDS); // 응답 값 수신

            // LED 컨트롤 프리셋 조회
            String presetNumber = ledMapper.getLastLoadPreset();

            // led 컨트롤러 리스트 조회
            List<LedconVo> ledControllerInfoList = ledconMapper.getLedconList();
            Map<String, Date> ledControllerInfoMap = IntStream.range(0, ledControllerInfoList.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> ledControllerInfoList.get(i).getIp(), i -> ledControllerInfoList.get(i).getRegDt(), (v1, v2) -> v2, HashMap::new));

            float light = -1; // 밝기
            String inputSource = ""; // 입력 소스

            // TODO 260까지 자르기
            for (ResponseWithIpVo response : ledHexResponse) {
                Map<String, Object> ledStatusMap = new HashMap<>();

                if (response.getResponse().length >= 88) {
                    StringBuilder lightHexReverse = new StringBuilder();

                    for (int i = 26; i >= 23; i--) {
                        lightHexReverse.append(String.format("%02X", response.getResponse()[i]));
                    }

                    light = hexToFloat(lightHexReverse.toString());

                    // TODO default 값 HDMI1로 했는데 수정 필요
                    inputSource = inputSourceComponent.getInputSourceValue(String.format("%02X", response.getResponse()[88]));

                    ledStatusMap.put("ip", response.getIp());
                    ledStatusMap.put("brightness", light);
                    ledStatusMap.put("inputSource", inputSource);
                    ledStatusMap.put("createDate", ledControllerInfoMap.getOrDefault(response.getIp(), null));

                    if (presetNumber != null) {
                        ledStatusMap.put("presetNumber", presetNumber);
                    }

                    ledControllerList.add(ledStatusMap);
                }
            }

            byte[] powerMessage = {0x02, (byte) 0xFF, (byte) 0x24, 0x00, 0x00, (byte) 0xDB, 0x03}; // 전원 상태 확인

            // 유닛 전원 상태 조회
            CompletableFuture<ResponseWithIpVo[]> unitControllerFuture = deviceControllerClient.sendMessage(powerMessage);
            ResponseWithIpVo[] powerHexResponse = unitControllerFuture.get(10, TimeUnit.SECONDS); // 응답 값 수신

            List<PwrconVo> unitControllerInfoList = pwrconMapper.getPwrconList();
            Map<String, Date> unitControllerInfoMap = IntStream.range(0, unitControllerInfoList.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> unitControllerInfoList.get(i).getIp(), i -> unitControllerInfoList.get(i).getRegDt(), (v1, v2) -> v2, HashMap::new));


            // 유닛 전원 상태 조회
            for (ResponseWithIpVo response : powerHexResponse) {
                if (response.getResponse().length >= 6) {
                    int powerState = Integer.parseInt(String.format("%02X", response.getResponse()[6]));
                    Map<String, Object> powerMap = new HashMap<>();

                    powerMap.put("ip", response.getIp());
                    powerMap.put("state", powerState);
                    powerMap.put("createDate", unitControllerInfoMap.getOrDefault(response.getIp(), null));

                    unitControllerList.add(powerMap);
                }
            }

            if (ledControllerList.size() > 0 || unitControllerList.size() > 0) {
                Map<String, Object> dataMap = new HashMap<>();

                dataMap.put("ledController", ledControllerList);
                dataMap.put("unitController", unitControllerList);

                responseMap.put("data", dataMap);
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
