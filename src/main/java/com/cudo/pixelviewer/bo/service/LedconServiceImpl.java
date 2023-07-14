package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.LedconMapper;
import com.cudo.pixelviewer.component.LedControllerClient;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedconVo;
import com.cudo.pixelviewer.vo.ResponseWithIpVo;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedconServiceImpl implements LedconService {

    final LedconMapper ledconMapper;

    final LedControllerClient ledControllerClient;

    @Override
    public Map<String, Object> getLedconList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<LedconVo> ledconVoList = ledconMapper.getLedconList();

        if (ledconVoList.size() > 0) {
            resultMap.put("data", ledconVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLedcon(Map<String, Object> param) throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        List<String> ipList = ledconMapper.postIpLedPwrValid(String.valueOf(param.get("ip")));

        if (ipList.size() == 0) { // Ip Not Exist : 0
            String firmwareVersion = connectIp(String.valueOf(param.get("ip")), Integer.parseInt(String.valueOf(param.get("port"))));

            param.put("firmwareVer", firmwareVersion);

            int postLedconResult = ledconMapper.postLedcon(param);

            if (postLedconResult == 1) { // Success : 1
                dataMap.put("ledControllerId", param.get("ledControllerId"));
                dataMap.put("firmwareVersion", firmwareVersion);
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            } else {
                resultMap.put("code", ResponseCode.FAIL_INSERT_LEDCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_LEDCON.getMessage());
            }
        } else {
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_IP.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_IP.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteLedcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int ledconCheck = ledconMapper.deleteLedconValid(param);

        if (ledconCheck == 1) {  // Exist : 1
            Map<String, Object> beforeIpPort = ledconMapper.putLedConValid(Integer.parseInt(String.valueOf(param.get("ledControllerId"))));
            String beforeIp = String.valueOf(beforeIpPort.get("ip"));
            int beforePort = Integer.parseInt(String.valueOf(beforeIpPort.get("port")));

            // 기존 채널 삭제
            disConnectChannel(beforeIp, beforePort);

            int deleteLedconResult = ledconMapper.deleteLedcon(param);

            if (deleteLedconResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                resultMap.put("code", ResponseCode.FAIL_DELETE_LEDCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_LEDCON.getMessage());
            }
        } else {
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_LEDCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_LEDCON.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putLedcon(Map<String, Object> param) throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, Object> resultMap = new HashMap<>();

        int ledconCheck = ledconMapper.putLedconValid(param);

        if (ledconCheck == 1) {  // Exist : 1

            Map<String, Object> beforeIpPort = ledconMapper.putLedConValid(Integer.parseInt(String.valueOf(param.get("ledControllerId"))));
            String beforeIp = String.valueOf(beforeIpPort.get("ip"));
            int beforePort = Integer.parseInt(String.valueOf(beforeIpPort.get("port")));

            // ip가 달라질 경우
            if (!beforeIp.equals(param.get("ip"))) {
                List<String> ipList = ledconMapper.postIpLedPwrValid(String.valueOf(param.get("ip")));

                if (ipList.size() == 0) { // ip 겹치지 않을 경우
                    // 기존 채널 삭제
                    disConnectChannel(beforeIp, beforePort);

                    // LED 컨트롤러 커넥트
                    String firmwareVersion = connectIp(String.valueOf(param.get("ip")), Integer.parseInt(String.valueOf(param.get("port"))));

                    param.put("firmwareVer", firmwareVersion);

                } else { // ip 겹칠 경우
                    resultMap.put("code", ResponseCode.FAIL_DUPLICATE_IP.getCode());
                    resultMap.put("message", ResponseCode.FAIL_DUPLICATE_IP.getMessage());

                    return resultMap;
                }
            }

            int putLedconResult = ledconMapper.putLedcon(param);

            if (putLedconResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                resultMap.put("code", ResponseCode.FAIL_UPDATE_LEDCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_LEDCON.getMessage());
            }
        } else {
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_LEDCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_LEDCON.getMessage());
        }
        return resultMap;
    }

    private Set<Channel> getChannelSet(String ip, int port) {
        Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap = ledControllerClient.getChannelFutureMap();

        return channelFutureMap.keySet().stream()
                .filter(channel -> {
                    SocketAddress remoteAddress = channel.remoteAddress();

                    if (remoteAddress instanceof InetSocketAddress) {
                        InetSocketAddress inetAddress = (InetSocketAddress) remoteAddress;
                        String remoteIp = inetAddress.getHostString();
                        int remotePort = inetAddress.getPort();

                        return ip.equals(remoteIp) && port == remotePort;
                    }
                    return false;
                })
                .collect(Collectors.toSet());
    }

    private String connectIp(String ip, int port) throws ExecutionException, InterruptedException, TimeoutException {
        String firmwareVersion = null;

        // LED 컨트롤러 커넥트
        ledControllerClient.connectChannel(ip, port, 0);

        // 커넥트 채널 Set으로 파싱
        Set<Channel> connectCheck = getChannelSet(ip, port);

        if (connectCheck.size() > 0) {
            byte[] ledStatusMessage = {0x01, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x16}; // LED 컨트롤러 상태 조회

            // 펌웨어 버전 조회
            CompletableFuture<ResponseWithIpVo[]> ledControllerFuture = ledControllerClient.sendMessage(ledStatusMessage);
            ResponseWithIpVo[] ledHexResponse = ledControllerFuture.get(10, TimeUnit.SECONDS); // 응답 값 수신

            StringBuilder firmwareVer = new StringBuilder();

            for (ResponseWithIpVo response : ledHexResponse) {
                if (response.getIp().equals(String.valueOf(ip))) {
                    byte[] responseByte = response.getResponse();

                    if (responseByte.length >= 14) {
                        firmwareVer.append(Integer.parseInt(String.valueOf(responseByte[13]), 16)).append(".");
                        firmwareVer.append(Integer.parseInt(String.valueOf(responseByte[14]), 16));

                        firmwareVersion = firmwareVer.toString();
                    }
                    break;
                }
            }

        }

        return firmwareVersion;
    }

    private boolean disConnectChannel(String ip, int port) {
        Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap = ledControllerClient.getChannelFutureMap();

        // 커넥트 채널 Set으로 파싱
        Set<Channel> connectCheck = getChannelSet(ip, port);

        if (connectCheck.size() == 1) {

            for (Channel channel : connectCheck) {
                channelFutureMap.remove(channel);
            }

            ledControllerClient.setChannelFutureMap(channelFutureMap);

            return true;
        }

        return false;
    }
}