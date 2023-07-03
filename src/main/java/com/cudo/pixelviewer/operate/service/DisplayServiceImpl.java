package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.operate.mapper.DisplayMapper;
import com.cudo.pixelviewer.operate.mapper.ScreenMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.DisplayVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService {

    final DisplayMapper displayMapper;

    final AdminSettingMapper adminSettingMapper;

    final ScreenMapper screenMapper;


    @Override
    public Map<String, Object> getDisplayList(String screenId) {
        Map<String, Object> resultMap = new HashMap<>();

        List<DisplayVo> displayVoList = displayMapper.getDisplayList(screenId);

        if(displayVoList.size() > 0){
            resultMap.put("data", displayVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getDisplay(String displayId) {
        Map<String, Object> resultMap = new HashMap<>();

        DisplayVo displayVo = displayMapper.getDisplay(displayId);

        if(displayVo != null){
            resultMap.put("data", displayVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getDisplayPortlist() {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> displayPortlist = displayMapper.getDisplayPortlist();

        if(displayPortlist.size() > 0){
            resultMap.put("data", displayPortlist);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchDisplayTestpattern(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int displayCheck = displayMapper.patchDisplayTestpatternValid(param);

        if(displayCheck > 0){

            // TODO : +TEST PATTERN LOGIC START
            // TODO : 테스트 패턴에 대한 내용

            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> displayArray = new ArrayList<>();

            String testPatternColor = adminSettingMapper.getTestPattern();
            String[] colorSplit = testPatternColor.split(",");
            int index = 0;
            System.out.println("testPatternColor = " + testPatternColor);

            Object tempScreenId = param.get("screenId");
            String screenId = tempScreenId.toString();

            List<Map<String, Object>> screenAllocateDisplays = screenMapper.getScreenAllocateDisplays(screenId);

            for (Map<String, Object> display : screenAllocateDisplays) {
                Map<String, Object> tempMap = new HashMap<>();
                Map<String, Object> tempDataMap = new HashMap<>();

                tempMap.put("displayId", display.get("displayId"));
                tempMap.put("displayNm", display.get("displayNm"));
                tempMap.put("bgColor", colorSplit[index]);

                tempDataMap.put("posX", display.get("posX"));
                tempDataMap.put("posY", display.get("posY"));
                tempDataMap.put("width", display.get("width"));
                tempDataMap.put("height", display.get("height"));
                tempMap.put("position", tempDataMap);

                displayArray.add(tempMap);
                index = (index == colorSplit.length - 1) ? 0 : (index + 1);
            }

            System.out.println("testPatternColor = " + testPatternColor);

            int currentTime = 1;

            requestBodyMap.put("patternInfo", displayArray);
            requestBodyMap.put("presentationTime", currentTime);

            // TODO : Agent 연동 테스트 필요
/*
            String ip = "192.168.123.12";
            String port = "8800";

            String agentUrl = "http://" + ip + ":" + port + "/vieweragent/test-pattern";
            // WebClient 생성
            WebClient webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector())
                    .baseUrl(agentUrl)
                    .build();

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // POST 요청 보내기
            Mono<String> responseMono = webClient.method(HttpMethod.POST)
                    .uri(agentUrl)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(BodyInserters.fromValue(requestBodyMap))
                    .retrieve()
                    .bodyToMono(String.class);

            // 응답 처리
            // TODO : 예외처리
            responseMono.subscribe(response -> {
                String data = response.toString();
                System.out.println("data = " + data);
//            int statusCode = Integer.parseInt(response);
//            if (statusCode == 200) {
//                log.info("요청 성공");
//            } else {
//                log.info("요청 실패 상태 코드: {}", statusCode);
//            }
            });

 */
//            int patchDisplayNameResult = displayMapper.patchDisplayTestpattern(param);

//            if(patchDisplayNameResult == 1){ // Success : 1
//                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//            }
//            else{
//                resultMap.put("code", ResponseCode.FAIL_UPDATE_DISPLAY.getCode());
//                resultMap.put("message", ResponseCode.FAIL_UPDATE_DISPLAY.getMessage());
//            }
            // TODO : TEST PATTERN LOGIC END
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_DISPLAY.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_DISPLAY.getMessage());
        }
        return resultMap;
    }

}