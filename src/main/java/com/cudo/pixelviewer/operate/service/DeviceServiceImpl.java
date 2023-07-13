package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.bo.mapper.LedconMapper;
import com.cudo.pixelviewer.component.DeviceControllerClient;
import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedconVo;
import com.cudo.pixelviewer.vo.ResponseWithIpVo;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    final DeviceControllerClient deviceControllerClient;

    final LedControllerClient ledControllerClient;

    final LedconMapper ledconMapper;

    @Override
    public Map<String, Object> setDevicePower(Integer power) {
        Map<String, Object> responseMap = new HashMap<>();

        byte[] message = {0x02, (byte) 0xFF, (byte) 0xB1, 0x00, 0x01, 0x00, (byte) 0x4F, 0x03}; // 전원 OFF 초기값

        if (power == 1) { // 전원 ON
            int onOffHexValue = Integer.decode("0x01");
            int crcHexValue = Integer.decode("0x4E");

            message[5] = (byte) onOffHexValue;
            message[6] = (byte) crcHexValue;
        }

        try {
//            CompletableFuture<ResponseWithIpVo[]> future = deviceControllerClient.sendMessage(message); // 전원 제어
//            ResponseWithIpVo[] hexResponseWithIp = future.get(10, TimeUnit.SECONDS); // 응답 값 수신

            // TODO 유닛 전원 제어 하드코딩 제거
            ResponseWithIpVo[] hexResponseWithIp = new ResponseWithIpVo[1];
            byte[] responseByte = {0x02, (byte) 0xFF, 0x06, 0x00, 0x02, 0x24, 0x01, (byte) 0xDF, 0x03};
            hexResponseWithIp[0] = new ResponseWithIpVo(responseByte, "192.168.123.111");
            // TODO 유닛 전원 제어 하드코딩 제거

            int successCount = 0;

            for (ResponseWithIpVo response : hexResponseWithIp) {
                if (response.getResponse().length > 0) {
                    successCount += 1;
                }
            }

            if (successCount > 0) {
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

        try {
            Map<Channel, CompletableFuture<ResponseWithIpVo>> ledControllerChannelMap = ledControllerClient.getChannelFutureMap();
//            CompletableFuture<ResponseWithIpVo[]> future = deviceControllerClient.sendMessage(message); // 유닛 컨트롤러 전원 상태 값 조회
//            ResponseWithIpVo[] hexResponseWithIp = future.get(10, TimeUnit.SECONDS); // 응답 값 수신

            // TODO 유닛 전원 상태 조회 하드코딩 제거
            ResponseWithIpVo[] hexResponseWithIp = new ResponseWithIpVo[1];
            byte[] responseByte = {0x02, (byte) 0xFF, 0x06, 0x00, 0x02, 0x24, 0x01, (byte) 0xDF, 0x03};
            hexResponseWithIp[0] = new ResponseWithIpVo(responseByte, "192.168.123.111");
            // TODO 유닛 전원 상태 조회 하드코딩 제거

            // 유닛 전원 상태 조회
            for (ResponseWithIpVo response : hexResponseWithIp) {
                if (response.getResponse().length >= 6) {
                    int powerState = Integer.parseInt(String.format("%02X", response.getResponse()[6]));
                    Map<String, Object> powerMap = new HashMap<>();

                    powerMap.put("ip", response.getIp());
                    powerMap.put("state", powerState);
                    powerMap.put("type", "unit");

                    powerStateList.add(powerMap);
                }
            }

            // LED 컨트롤러 전원 상태 조회
            List<LedconVo> ipPortList = ledconMapper.getLedconList();
            Map<String, Channel> ipMap = new HashMap();

            for (Channel channel : ledControllerChannelMap.keySet()) {
                SocketAddress remoteAddress = channel.remoteAddress();

                if (remoteAddress instanceof InetSocketAddress) {
                    InetSocketAddress inetAddress = (InetSocketAddress) remoteAddress;
                    String ip = inetAddress.getHostString();

                    ipMap.put(ip, channel);
                }
            }

            // LED 컨트롤러 전원 상태 값 파싱
            for (LedconVo ipPort : ipPortList) {
                Map<String, Object> powerMap = new HashMap<>();

                powerMap.put("ip", ipPort.getIp());
                powerMap.put("type", "led");

                if (ipMap.containsKey(ipPort.getIp()) && ipMap.get(ipPort.getIp()).isActive()) {
                    powerMap.put("state", 1);
                } else {
                    powerMap.put("state", 0);
                }

                powerStateList.add(powerMap);
            }

            if (powerStateList.size() > 0) {
                dataMap.put("powerState", powerStateList);

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
        List<Map<String, Object>> dataMapList = new ArrayList<>();

        byte[] message = {0x02, (byte) 0xFF, (byte) 0x6A, 0x00, 0x00, (byte) 0x95, 0x03};

        try {
//            CompletableFuture<ResponseWithIpVo[]> future = deviceControllerClient.sendMessage(message); // 온습도 조회
//            ResponseWithIpVo[] hexResponseWithIp = future.get(10, TimeUnit.SECONDS); // 응답 값 수신

            // TODO 유닛 온습도 조회 하드코딩 제거
            ResponseWithIpVo[] hexResponseWithIp = new ResponseWithIpVo[1];
            byte[] responseByte = {0x02, (byte) 0xFF, 0x06, 0x00, 0x0A, 0x6A, 0x2B, 0x00, 0x03, 0x01, 0x05, 0x04, 0x05, 0x07, 0x02, (byte) 0xB2, 0x03};
            hexResponseWithIp[0] = new ResponseWithIpVo(responseByte, "192.168.123.111");
            // TODO 유닛 온습도 조회 하드코딩 제거

            for (ResponseWithIpVo response : hexResponseWithIp) {
                Map<String, Object> tempHumiMap = new HashMap<>();
                StringBuilder temperature = new StringBuilder();
                StringBuilder humidity = new StringBuilder();

                if (response.getResponse().length >= 14) {
                    int minusCheck = 1;
                    int index = 6; // 영하 온도 index 체크

                    if (String.format("%02X", response.getResponse()[index]).equals("2B")) {
                        minusCheck = -1;
                        index += 1;
                    }

                    // 온도 파싱
                    temperature.append(Integer.parseInt(String.format("%02X", response.getResponse()[index])));
                    temperature.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 1]))).append(".");

                    temperature.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 2])));
                    temperature.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 3])));

                    // 습도 파싱
                    humidity.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 4])));
                    humidity.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 5]))).append(".");

                    humidity.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 6])));
                    humidity.append(Integer.parseInt(String.format("%02X", response.getResponse()[index + 7])));

                    tempHumiMap.put("ip", response.getIp());
                    tempHumiMap.put("temperature", Double.parseDouble(temperature.toString()) * minusCheck);
                    tempHumiMap.put("humidity", Double.parseDouble(humidity.toString()));

                    dataMapList.add(tempHumiMap);
                }
            }


            if (dataMapList.size() > 0) {
                responseMap.put("data", dataMapList);
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
