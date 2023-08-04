package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.operate.mapper.ScreenMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresetServiceImpl implements PresetService {

    final ScreenMapper screenMapper;

    final PresetMapper presetMapper;

    final LayerMapper layerMapper;

    final PlaylistMapper playlistMapper;



    @Value("${values.protocol}")
    private String protocol;

    @Value("${values.was.ip}")
    private String wasIp;
    @Value("${values.was.port}")
    private String wasPort;
    @Value("${values.was.path}")
    private String wasPath;

    @Value("${values.agent.ip}")
    private String agentIp;
    @Value("${values.agent.port}")
    private String agentPort;
    @Value("${values.agent.path}")
    private String agentPath;

    @Value("${values.agent.restore}")
    private String agentRestorePath;



    String presetStatusPlay = "play";
    String presetStatusStop = "stop";
    String presetStatusPause = "pause";
    String presetStatusNone = "none";

    Boolean CALL_YN = true;
//    Boolean CALL_YN = false;

    Integer CHECK_TIME = 10;

    @Override
    public Map<String, Object> getPresetList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<PresetVo> presetVoList = presetMapper.getPresetList();

        if(presetVoList.size() > 0){
            resultMap.put("data", presetVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getPreset(String presetId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        PresetVo presetVo = presetMapper.getPreset(presetId);

        if(presetVo != null){
            dataMap.put("preset", presetVo);

            List<LayerVo> layerVoList = presetMapper.getPresetLayers(presetId);
            if(layerVoList.size() > 0){
                for(LayerVo layer : layerVoList){
                    Map<String, Object> tempLayerObject = layerMapper.getLayerObject(layer.getLayerId());
                    if(tempLayerObject.containsKey("subtitleStyleInfo")){
                        String temp = (String) tempLayerObject.get("subtitleStyleInfo");
                        try {
                            JSONParser parser = new JSONParser();
                            Object obj = parser.parse(temp);
                            tempLayerObject.put("subtitleStyleInfo", obj);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    layer.setLayerObjectList(tempLayerObject);
                }
                dataMap.put("layerList", layerVoList);
            }

            resultMap.put("data", dataMap);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getUsingPreset() {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        PresetStatusRunVo usingPresetVo = presetMapper.getUsingPreset();

        if(usingPresetVo != null){
            dataMap.put("preset", usingPresetVo);
            List<LayerStatusRunVo> runLayerVoList = layerMapper.getRunLayersStatus(usingPresetVo.getPresetId());
            if(runLayerVoList.size() > 0){
                dataMap.put("layerInfoList", runLayerVoList);
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PRESET_ALLOCATE_RUN_LAYERS.getCode());
                resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PRESET_ALLOCATE_RUN_LAYERS.getMessage());
            }

            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postPreset(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        int presetCheck = 0;

        if(presetCheck == 0){
            int postPresetResult = presetMapper.postPreset(param);

            if(postPresetResult == 1){
                dataMap.put("presetId", param.get("presetId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_PRESET.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_PRESET.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_PRESET.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_PRESET.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deletePreset(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int presetCheck = presetMapper.deletePresetValid(param);

        if(presetCheck == 1){
            int deletePresetResult = presetMapper.deletePreset(param);

            if(deletePresetResult > 0){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_PRESET.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_PRESET.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PRESET.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PRESET.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchPresetName(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int presetCheck = presetMapper.patchPresetNameValid(param);

        if(presetCheck == 1){
            int patchPresetNameResult = presetMapper.patchPresetName(param);

            if(patchPresetNameResult == 1){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PRESET.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PRESET.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PRESET.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PRESET.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putPreset(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        int presetCheck = presetMapper.putPresetValid(param);

        if(presetCheck == 1){
            int putPresetResult = presetMapper.putPreset(param);

            if(putPresetResult == 1){

                Boolean layerClearYn = false;
                if(param.get("deleteType").equals(true)) {
                    int deleteLayerResult = presetMapper.putPresetDeleteLayers(param);
                    if(deleteLayerResult > 0){
                        layerClearYn = true;
                    }
                    else{
                        resultMap.put("code", ResponseCode.FAIL_DELETE_PRESET_ALLOCATE_LAYERS.getCode());
                        resultMap.put("message", ResponseCode.FAIL_DELETE_PRESET_ALLOCATE_LAYERS.getMessage());
                    }
                }
                else{
                    layerClearYn = true;
                }
                if(layerClearYn){
                    int saveLayerResult = presetMapper.saveLayer(param);
                    if(saveLayerResult > 0){
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    }
                    else{
                        resultMap.put("code", ResponseCode.FAIL_INSERT_PRESET_ALLOCATE_LAYERS.getCode());
                        resultMap.put("message", ResponseCode.FAIL_INSERT_PRESET_ALLOCATE_LAYERS.getMessage());
                    }
                }
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PRESET.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PRESET.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PRESET.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PRESET.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchPresetRun(Map<String, Object> param) throws InterruptedException {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> webClientResponse = new HashMap<>();


        PresetStatusRunVo usingPresetVo = presetMapper.getUsingPreset();
        Boolean usingPresetYn = false;
        if(usingPresetVo != null){
            usingPresetYn = true;
        }

        String controlType = (String) param.get("controlType");
        Object newPresetId = param.get("presetId");

        String agentUrl = protocol + agentIp + ":" + agentPort + agentPath;
        Map<String, Object> requestMap = createRequestBodyMap(param);
        JsonMapToPrint(requestMap);

        if(CALL_YN) {
            if (controlType.equals("apply")) {
                if(usingPresetYn) {
                    // 현재 프리셋 == 신규 프리셋 >> 레이어 정보 업데이트
                    if (param.get("presetId").equals(usingPresetVo.getPresetId())) {
                        List<Map<String, Object>> tempLayerInfoList = (List<Map<String, Object>>) param.get("layerInfoList");
                        List<Map<String, Object>> resultLayerInfoList = new ArrayList<>();

                        // 업데이트 레이어 추출
                        for (Map<String, Object> layerInfo : tempLayerInfoList) {
                            if ((Boolean) layerInfo.get("updateYn")) {
                                resultLayerInfoList.add(layerInfo);
                            }
                        }

                        // 현재 프리셋 : 레이어[UPDATE], 프리셋버전[UPDATE]
                        if (resultLayerInfoList.size() > 0) {
                            param.put("layerInfoList", resultLayerInfoList);
                            int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);
                            int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(usingPresetVo.getPresetId());

                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                        } else {
                            resultMap.put("code", ResponseCode.FAIL_UPDATE_NOT_EXIST_PLAYLIST.getCode());
                            resultMap.put("message", ResponseCode.FAIL_UPDATE_NOT_EXIST_PLAYLIST.getMessage());
                        }
                    }

                    // 현재 프리셋 != 신규 프리셋
                    // 현재 프리셋 : presetStatus[none]
                    // 신규 프리셋 : 레이어[UPDATE], presetStatus[stop], presetRun
                    // 현재 프리셋 : presetStatus[none], 하위 레이어 Status[none]
                    else {
                        int nonePresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(usingPresetVo.getPresetId(), presetStatusNone));


                        int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);
                        int stopPresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(newPresetId, presetStatusStop));

                        webClientResponse = webClientFunction("apply", agentUrl, requestMap);
                        resultMap = agentCallResult(webClientResponse);


                        int clearPresetAndLayerStatusForceResult = presetMapper.clearPresetAndLayerStatusForce(usingPresetVo.getPresetId());
                    }
                }

                // 신규 프리셋 : 레이어[UPDATE] / presetStatus[stop] / presetRun
                else{
                    int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);
                    int stopPresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(newPresetId, presetStatusStop));

                    webClientResponse = webClientFunction("apply", agentUrl, requestMap);
                    resultMap = agentCallResult(webClientResponse);
                }
            }

            else if (controlType.equals("play")) {
                if(usingPresetYn) {
                    // Check : 기존 프리셋 == 신규 프리셋
                    if (param.get("presetId").equals(usingPresetVo.getPresetId())) {
                        // 1. 기존 상태값 == "play" >> 예외 return
                        if (usingPresetVo.getPresetStatus().equals(presetStatusPlay)) {
                            resultMap.put("code", ResponseCode.ALREADY_PLAYING_PRESET.getCode());
                            resultMap.put("message", ResponseCode.ALREADY_PLAYING_PRESET.getMessage());
                        }
                        // 2. 기존 상태값 != "play" >> presetStatus[play]
                        else {
                            int runPresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(usingPresetVo.getPresetId(), presetStatusPlay));

                            resultMap.put("data", usingPresetVo.getPresetId());
                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                        }
                    }

                    // Check : 기존 프리셋 != 신규 프리셋
                    // 기존 프리셋 : presetStatus[none]
                    // 신규 프리셋 : 플레이리스트 업데이트 / presetStatus[play] / presetRun
                    // 기존 프리셋 : presetStatus[none], 하위 레이어 Status[none]
                    else {
                        int nonePresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(usingPresetVo.getPresetId(), presetStatusNone));


                        int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);
                        int runPresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(newPresetId, presetStatusPlay));
                        webClientResponse = webClientFunction("playAndNew", agentUrl, requestMap);
                        resultMap = agentCallResult(webClientResponse);


                        int clearPresetAndLayerStatusForceResult = presetMapper.clearPresetAndLayerStatusForce(usingPresetVo.getPresetId());
                    }
                }

                else {
                    // 신규 프리셋 : presetStatus[play] / presetRun
                    int runPresetSetResult = presetMapper.patchPresetStatusSet(presetStatusMap(newPresetId, presetStatusPlay));
                    webClientResponse = webClientFunction("playAndNew", agentUrl, requestMap);
                    resultMap = agentCallResult(webClientResponse);
                }
            }

            else {
                resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE.getCode());
                resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE.getMessage());
            }
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchPresetControl(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        if(param.get("controlType").equals("pause") || param.get("controlType").equals("stop") || param.get("controlType").equals("none")) {
            int clearPresetStatusResult = presetMapper.clearPresetAndLayerStatus();

            int patchPresetStatusSetResult = presetMapper.patchPresetStatusSet(param);

            if (patchPresetStatusSetResult > 0) {
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PRESET_STATUS.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PRESET_STATUS.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_PRESET_STATUS.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_PRESET_STATUS.getMessage());
        }

        return resultMap;
    }


    ////////////////////////////////////////////////////////////////////////////////////////// Function.
    public void JsonMapToPrint(Map<String, Object> printMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(printMap);
            log.info("[presetRun >> WAS to Agent] requestBodyMap = " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> webClientFunction(String type, String url, Map<String, Object> requestBodyMap){
        Map<String, Object> returnMap = new HashMap<>();

        WebClient webClient2 = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl(url)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String responseMono = webClient2.method(HttpMethod.POST)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(BodyInserters.fromValue(requestBodyMap))
                .retrieve()
                .bodyToMono(String.class)
                .block();


        returnMap.put("data", responseMono);

        return returnMap;
    }

    public Map<String, Object> createRequestBodyMap(Map<String, Object> param) {
        Map<String, Object> requestBodyMap = new HashMap<>();

        String baseUrl = protocol + wasIp + ":" + wasPort + wasPath;

        requestBodyMap.put("playInfoUrl", baseUrl + "playInfo");
        requestBodyMap.put("updateCheckUrl", baseUrl + "updateAndHealthCheck");
        requestBodyMap.put("previewImgUrl", baseUrl + "previewImg/10");

        String screenId = String.valueOf(param.get("screenId"));
        String presetId = String.valueOf(param.get("presetId"));

        Map<String, Object> screenInfo = screenMapper.getScreen(screenId);
        List<LayerToAgentVo> layerInfos = presetMapper.getPresetLayersToAgent(presetId);
        String removeKey = "screenNm";
        screenInfo.remove(removeKey);

        requestBodyMap.put("presetId", param.get("presetId"));
        requestBodyMap.put("screenInfo", screenInfo);
        requestBodyMap.put("layers", layerInfos);

        return requestBodyMap;
    }

    public Map<String, Object> presetStatusMap (Object presetId, String targetStatus){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("presetId", presetId);
        queryMap.put("controlType", targetStatus);

        return queryMap;
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
            agentResultMap.put("code", ResponseCode.FAIL_PRESET_RUN_TO_AGENT.getCode());
            agentResultMap.put("message", ResponseCode.FAIL_PRESET_RUN_TO_AGENT.getMessage());
        }

        return agentResultMap;
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void scheduleHealthCheckAndLayerRestore(){

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String logMessage = "[Scheduled][Check Viewer]";

        PresetStatusRunVo usingPresetVo = presetMapper.getUsingPreset();
        if(usingPresetVo != null){
            log.info("{} - {}", logMessage, usingPresetVo);

            List<LayerVo> usingLayers = layerMapper.getUsingLayers(usingPresetVo.getPresetId());
            if (!usingLayers.isEmpty()) {
                List<LayerToAgentVo> restoreLayers = new ArrayList<>();

                for(LayerVo usingLayer : usingLayers){
                    LocalDateTime dbTime = LocalDateTime.parse(usingLayer.getUpdateDate(), formatter);
                    long differenceInSeconds = Math.abs(Duration.between(localDateTime, dbTime).getSeconds());

                    if (differenceInSeconds > CHECK_TIME) {
                        LayerToAgentVo layerToAgentVo = new LayerToAgentVo();
                        layerToAgentVo.setLayerId(usingLayer.getLayerId());
                        layerToAgentVo.setPosX(usingLayer.getPosX());
                        layerToAgentVo.setPosY(usingLayer.getPosY());
                        layerToAgentVo.setWidth(usingLayer.getWidth());
                        layerToAgentVo.setHeight(usingLayer.getHeight());
                        layerToAgentVo.setOrd(usingLayer.getOrd());

                        restoreLayers.add(layerToAgentVo);
                    }
                }
                if (!restoreLayers.isEmpty()) {
                    Map<String, Object> resultMap = new HashMap<>();
                    Map<String, Object> requestBodyMap = new HashMap<>();

                    String baseUrl = protocol + wasIp + ":" + wasPort + wasPath;
                    requestBodyMap.put("playInfoUrl", baseUrl + "playInfo");
                    requestBodyMap.put("updateCheckUrl", baseUrl + "updateAndHealthCheck");
                    requestBodyMap.put("previewImgUrl", baseUrl + "previewImg/10");

                    Map<String, Object> screenInfo = screenMapper.getScreen(String.valueOf(usingPresetVo.getScreenId()));
                    String removeKey = "screenNm";
                    screenInfo.remove(removeKey);

                    requestBodyMap.put("presetId", usingPresetVo.getPresetId());
                    requestBodyMap.put("screenInfo", screenInfo);
                    requestBodyMap.put("layers", restoreLayers);

                    JsonMapToPrint(requestBodyMap);

                    String agentUrl = protocol + agentIp + ":" + agentPort + agentRestorePath;

                    if(CALL_YN){
                        Map<String, Object> webClientResponse = webClientFunction("restore", agentUrl, requestBodyMap);
                        resultMap = agentCallResult(webClientResponse);
                    }
                    else{
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    }
                    log.info("{} - {}", logMessage, resultMap);
                }
                else{
                    log.info("{}[SUCCESS]", logMessage);
                }
            }
            else{
                log.error("{} - No Layers Running", logMessage);
            }
        } else {
            log.info("{} - No Presets in Use", logMessage);
        }
    }
}