package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.LedconMapper;
import com.cudo.pixelviewer.bo.mapper.PwrconMapper;
import com.cudo.pixelviewer.component.DeviceControllerClient;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.PwrconVo;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PwrconServiceImpl implements PwrconService {

    final PwrconMapper pwrconMapper;

    final LedconMapper ledconMapper;

    final DeviceControllerClient deviceControllerClient;

    @Override
    public Map<String, Object> getPwrconList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<PwrconVo> pwrconVoList = pwrconMapper.getPwrconList();

        if(pwrconVoList.size() > 0){
            resultMap.put("data", pwrconVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postPwrcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        List<String> ipList = ledconMapper.postIpLedPwrValid(String.valueOf(param.get("ip")));

        if(ipList.size() == 0){ // Ip Not Exist : 0
            // 커넥트
            deviceControllerClient.connectChannel(String.valueOf(param.get("ip")), Integer.parseInt(String.valueOf(param.get("port"))), 0);

            int postPwrconResult = pwrconMapper.postPwrcon(param);

            if(postPwrconResult == 1){ // Success : 1
                dataMap.put("condeviceId", param.get("condeviceId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_PWRCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_PWRCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_IP.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_IP.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deletePwrcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int pwrconCheck = pwrconMapper.deletePwrconValid(param);

        if(pwrconCheck == 1){  // Exist : 1
            Map<String, Object> beforeIpPort = pwrconMapper.putPwrConValid(Integer.parseInt(String.valueOf(param.get("condeviceId"))));
            String beforeIp = String.valueOf(beforeIpPort.get("ip"));
            int beforePort = Integer.parseInt(String.valueOf(beforeIpPort.get("port")));

            // 기존 채널 삭제
            disConnectChannel(beforeIp, beforePort);

            int deletePwrconResult = pwrconMapper.deletePwrcon(param);

            if(deletePwrconResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_PWRCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_PWRCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PWRCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PWRCON.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putPwrcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int pwrconCheck = pwrconMapper.putPwrconValid(param);

        if(pwrconCheck == 1){  // Exist : 1
            Map<String, Object> beforeIpPort = pwrconMapper.putPwrConValid(Integer.parseInt(String.valueOf(param.get("condeviceId"))));
            String beforeIp = String.valueOf(beforeIpPort.get("ip"));
            int beforePort = Integer.parseInt(String.valueOf(beforeIpPort.get("port")));

            // ip가 달라질 경우
            if (!beforeIp.equals(param.get("ip"))) {
                List<String> ipList = ledconMapper.postIpLedPwrValid(String.valueOf(param.get("ip")));

                if (ipList.size() == 0) { // ip 겹치지 않을 경우
                    // 기존 채널 삭제
                    disConnectChannel(beforeIp, beforePort);

                    // 전원 컨트롤러 커넥트
                    deviceControllerClient.connectChannel(String.valueOf(param.get("ip")), Integer.parseInt(String.valueOf(param.get("port"))), 0);

                } else { // ip 겹칠 경우
                    resultMap.put("code", ResponseCode.FAIL_DUPLICATE_IP.getCode());
                    resultMap.put("message", ResponseCode.FAIL_DUPLICATE_IP.getMessage());

                    return resultMap;
                }
            }

            int patchPwrconNameResult = pwrconMapper.putPwrcon(param);

            if(patchPwrconNameResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PWRCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PWRCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PWRCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PWRCON.getMessage());
        }
        return resultMap;
    }

    private boolean disConnectChannel(String ip, int port) {
        Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap = deviceControllerClient.getChannelFutureMap();

        // 커넥트 채널 Set으로 파싱
        Set<Channel> connectCheck = channelFutureMap.keySet().stream()
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

        if (connectCheck.size() == 1) {
            for (Channel channel : connectCheck) {
                channelFutureMap.remove(channel);
            }

            deviceControllerClient.setChannelFutureMap(channelFutureMap);

            return true;
        }
        return false;
    }
}