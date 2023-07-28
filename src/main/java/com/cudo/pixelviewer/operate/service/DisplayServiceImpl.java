package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.operate.mapper.DisplayMapper;
import com.cudo.pixelviewer.operate.mapper.ScreenMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.DisplayVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService {

    final DisplayMapper displayMapper;

    final AdminSettingMapper adminSettingMapper;

    final ScreenMapper screenMapper;

    @Value("${values.agent.ip}")
    private String agentIp;
    @Value("${values.agent.port}")
    private String agentPort;

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
            Map<String, Object> requestBodyMap = new HashMap<>();

            List<Map<String, Object>> testPatternList = adminSettingMapper.getTestPattern();

            String testPatternColor = getTestPatternColor(testPatternList, "#FFFF8000,#FFFF0080,#FFC0C0C0,#FF808000,#FF800000,#FFFF00FF,#FF400080,#FF8080FF,#FFC08080,#FF008080,#FF0000FF,#FF808040");
            Integer testPatternTime = getTestPatternTime(testPatternList, 8);

            String[] colorSplit = testPatternColor.split(",");

            Object tempScreenId = param.get("screenId");
            String screenId = tempScreenId.toString();

            List<Map<String, Object>> screenAllocateDisplays = screenMapper.getScreenAllocateDisplays(screenId);
            List<Map<String, Object>> displayArray = setPatternInfo(screenAllocateDisplays, colorSplit);


            requestBodyMap.put("patternInfo", displayArray);
            requestBodyMap.put("presentationTime", testPatternTime);

            String agentUrl = "http://" + agentIp + ":" + agentPort + "/vieweragent/test-pattern";

            Map<String, Object> webClientResponse = webClientFunction("testPattern", agentUrl, requestBodyMap);
            resultMap = agentCallResult(webClientResponse);
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_DISPLAY.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_DISPLAY.getMessage());
        }
        return resultMap;
    }

    public String getTestPatternColor(List<Map<String, Object>> testPatternList, String defaultValue) {
        return Optional.ofNullable(testPatternList)
                .orElse(List.of())
                .stream()
                .filter(map -> "testPattern".equals(map.get("settingKey")))
                .map(map -> (String) map.get("settingValue"))
                .findFirst()
                .orElse(defaultValue);
    }

    public Integer getTestPatternTime(List<Map<String, Object>> testPatternList, Integer defaultValue) {
        return Optional.ofNullable(testPatternList)
                .orElse(List.of())
                .stream()
                .filter(map -> "testPatternTime".equals(map.get("settingKey")))
                .map(map -> (String) map.get("settingValue"))
                .map(Integer::parseInt)
                .findFirst()
                .orElse(defaultValue);
    }

    public Map<String, Object> webClientFunction(String type, String url, Map<String, Object> requestBodyMap){
        Map<String, Object> returnMap = new HashMap<>();

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl(url)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String responseMono = webClient.method(HttpMethod.POST)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(BodyInserters.fromValue(requestBodyMap))
                .retrieve()
                .bodyToMono(String.class)
                .block();


        returnMap.put("data", responseMono);

        return returnMap;
    }

    public Map<String, Object> agentCallResult (Map<String, Object> agentResponseMap){
        Map<String, Object> agentResultMap = new HashMap<>();

        if (agentResponseMap.containsKey("data")) {
            String response = (String) agentResponseMap.get("data");
            if (response.contains("200")) {
                agentResultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else {
                agentResultMap.put("code", ResponseCode.FAIL_AGENT_TO_VIEWER.getCode());
                agentResultMap.put("message", ResponseCode.FAIL_AGENT_TO_VIEWER.getMessage());
            }
        } else {
            agentResultMap.put("code", ResponseCode.FAIL_DISPLAY_SETTING_TO_AGENT.getCode());
            agentResultMap.put("message", ResponseCode.FAIL_DISPLAY_SETTING_TO_AGENT.getMessage());
        }
        return agentResultMap;
    }

    public static List<Map<String, Object>> setPatternInfo(List<Map<String, Object>> screenAllocateDisplays, String[] colorSplit) {
        List<Map<String, Object>> displayArray = new ArrayList<>();
        int index = 0;

        for (Map<String, Object> display : screenAllocateDisplays) {
            Map<String, Object> tempMap = new HashMap<>();
            Map<String, Object> tempDataMap = new HashMap<>();

            tempMap.put("displayId", display.get("displayId"));
            tempMap.put("displayNm", display.get("displayNm"));
            tempMap.put("bgColor", colorSplit[index]);

            tempDataMap.put("x", display.get("posX"));
            tempDataMap.put("y", display.get("posY"));
            tempDataMap.put("width", display.get("width"));
            tempDataMap.put("height", display.get("height"));
            tempMap.put("position", tempDataMap);

            displayArray.add(tempMap);
            index = (index == colorSplit.length - 1) ? 0 : (index + 1);
        }
        return displayArray;
    }
}