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
//        CompletableFuture<String> future = deviceControllerClient.sendMessage(message); // 전원 제어

        try {
//            future.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            String hexResponse = future.join(); // 응답 값 비동기 수신

            // TODO 하드코딩 삭제
            String hexResponse = power == 1 ? "02FF060002B1014B03" : "02FF060002B1004A03";

            int index = hexResponse.indexOf("B1") + 2;

            if (hexResponse.length() > (index + 2)) {
                int powerState = Integer.parseInt(hexResponse.substring(index, index + 2));

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

//        CompletableFuture<String> future = deviceControllerClient.sendMessage(message); // 전원 상태 값 조회

        try {
//            future.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            String hexResponse = future.join(); // 응답 값 비동기 수신

            // TODO 하드코딩 삭제 / 여러개의 값 동시에 진행
            String hexResponse = "02FF0600022401DE03";

            int index = hexResponse.indexOf("24") + 2;

            if (hexResponse.length() > (index + 2)) {
                int powerState = Integer.parseInt(hexResponse.substring(index, index + 2));

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

//        CompletableFuture<String> future = deviceControllerClient.sendMessage(message); // 온습도 조회

        try {
//            future.get(10, TimeUnit.SECONDS); // 응답 값 타임아웃 설정
//            String hexResponse = future.join(); // 응답 값 비동기 수신

            // TODO 하드코딩 삭제
          String hexResponse = "02FF06000A6A2B0003010504050702B203";

            int index = hexResponse.indexOf("6A") + 2;

            String tempHumi = "";

            StringBuilder temperature = new StringBuilder();
            StringBuilder humidity = new StringBuilder();

            if (hexResponse.length() > (index + 18)) { // 응답 값 길이 체크
                int minusCheck = 1;

                tempHumi = hexResponse.substring(index, index + 18);

                if (tempHumi.startsWith("2B")) {
                    minusCheck = -1;
                    tempHumi = tempHumi.substring(2);
                }

                // 온도 파싱
                temperature.append(Integer.parseInt(tempHumi.substring(0, 2)));
                temperature.append(Integer.parseInt(tempHumi.substring(2, 4))).append(".");

                temperature.append(Integer.parseInt(tempHumi.substring(4, 6)));
                temperature.append(Integer.parseInt(tempHumi.substring(6, 8)));

                // 습도 파싱
                humidity.append(Integer.parseInt(tempHumi.substring(8, 10)));
                humidity.append(Integer.parseInt(tempHumi.substring(10, 12))).append(".");

                humidity.append(Integer.parseInt(tempHumi.substring(12, 14)));
                humidity.append(Integer.parseInt(tempHumi.substring(14, 16)));

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
