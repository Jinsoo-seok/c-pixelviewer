package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.component.DeviceControllerClient;
import com.cudo.pixelviewer.operate.mapper.DeviceMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    final DeviceControllerClient deviceControllerClient;

    final DeviceMapper deviceMapper;

    @Override
    public Map<String, Object> setDevicePower(Integer power) {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        byte[] message = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x00, (byte) 0x4F, 0x03}; // 전원 OFF 초기값

        if (power == 1) { // 전원 ON
            int onOffHexValue = Integer.decode("0x01");
            int crcHexValue = Integer.decode("0x4E");

            message[5] = (byte) onOffHexValue;
            message[6] = (byte) crcHexValue;
        }


        try {
//            CompletableFuture<byte[]> future = deviceControllerClient.sendMessage(message); // 전원 제어
//            future.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            byte[] hexResponse = future.join(); // 응답 값 비동기 수신

            // TODO 하드코딩 삭제
            byte[] hexResponse = {0x02, (byte) 0xFF, 0x06, 0x00, 0x02, (byte) 0xB1, 0x01, (byte) 0x4B, 0x03};

            int powerState = -1;

            if (hexResponse.length >= 6) {
                powerState = Integer.parseInt(String.format("%02X", hexResponse[6]));

                dataMap.put("powerState", powerState);
            }


            if (dataMap.size() > 0) {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }

        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> getDevicePower() {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        byte[] message = {0x02, (byte) 0xFF, (byte) 0x24, 0x00, 0x00, (byte) 0xDB, 0x03}; // 전원 상태 확인
        List<Map<String, Object>> powerStateList = new ArrayList<>();

        List<Map<String, Object>> devicePowerList = deviceMapper.getDeviceIpPort(); // 디바이스 목록 조회


        try {
//            CompletableFuture<byte[]> future = deviceControllerClient.sendMessage(message); // 전원 상태 값 조회
//            future.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            byte[] hexResponse = future.join(); // 응답 값 비동기 수신

            // TODO 하드코딩 삭제 / 여러개의 값 동시에 진행
            byte[] hexResponse = {0x02, (byte) 0xFF, 0x06, 0x00, 0x02, (byte) 0xB1, 0x01, (byte) 0x4B, 0x03};

            if (hexResponse.length >= 6) {
                int powerState = Integer.parseInt(String.format("%02X", hexResponse[6]));

                for (Map<String, Object> devicePower : devicePowerList) {
                    Map<String, Object> powerMap = new HashMap<>();

                    // 하드 코딩
                    powerMap.put("deviceId", devicePower.get("deviceId"));
                    powerMap.put("type", devicePower.get("type"));
                    powerMap.put("state", powerState);

                    powerStateList.add(powerMap);
                }

                dataMap.put("powerState", powerStateList);
            }

            if (dataMap.size() > 0) {
                responseMap.put("data", dataMap);
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }

        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> getTempHumi() {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        byte[] message = {0x02, (byte) 0xFF, (byte) 0x6A, 0x00, 0x00, (byte) 0x95, 0x03};

        try {
//            CompletableFuture<byte[]> future = deviceControllerClient.sendMessage(message); // 온습도 조회
//            future.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            byte[] hexResponse = future.join(); // 응답 값 비동기 수신

            // TODO 하드코딩 삭제
            byte[] hexResponse = {0x02, (byte) 0xFF, 0x06, 0x00, 0x0A, 0x6A, 0x2B, 0x00, 0x03, 0x01, 0x05, 0x04, 0x05, 0x07, 0x02, (byte) 0xB2, 0x03};

            StringBuilder temperature = new StringBuilder();
            StringBuilder humidity = new StringBuilder();

            if (hexResponse.length >= 14) {
                int minusCheck = 1;
                int index = 6; // 영하 온도 index 체크

                if (String.format("%02X", hexResponse[index]).equals("2B")) {
                    minusCheck = -1;
                    index += 1;
                }

                // 온도 파싱
                temperature.append(Integer.parseInt(String.format("%02X", hexResponse[index])));
                temperature.append(Integer.parseInt(String.format("%02X", hexResponse[index + 1]))).append(".");

                temperature.append(Integer.parseInt(String.format("%02X", hexResponse[index + 2])));
                temperature.append(Integer.parseInt(String.format("%02X", hexResponse[index + 3])));

                // 습도 파싱
                humidity.append(Integer.parseInt(String.format("%02X", hexResponse[index + 4])));
                humidity.append(Integer.parseInt(String.format("%02X", hexResponse[index + 5]))).append(".");

                humidity.append(Integer.parseInt(String.format("%02X", hexResponse[index + 6])));
                humidity.append(Integer.parseInt(String.format("%02X", hexResponse[index + 7])));

                dataMap.put("temperature", Double.parseDouble(temperature.toString()) * minusCheck);
                dataMap.put("humidity", Double.parseDouble(humidity.toString()));
            }


            if (dataMap.size() > 0) {
                responseMap.put("data", dataMap);
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }

        } catch (Exception e) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            responseMap.put("exceptionMessage", e.getMessage());
        }

        return responseMap;
    }
}
