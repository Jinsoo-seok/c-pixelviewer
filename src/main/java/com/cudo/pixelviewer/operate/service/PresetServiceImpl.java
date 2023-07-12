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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresetServiceImpl implements PresetService {

    final ScreenMapper screenMapper;

    final PresetMapper presetMapper;

    final LayerMapper layerMapper;

    final PlaylistMapper playlistMapper;

    private static final String protocol = "http://";

    private static final String wasIp = "106.245.226.42";
    private static final String wasPort = "9898";
    private static final String wasPath = "/api-viewer/";

    private static final String agentIp = "192.168.123.12";
    private static final String agentPort = "8800";
    private static final String agentPath = "/vieweragent/Preset/layer-placement";

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
    public Map<String, Object> getRunPreset() {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        PresetStatusRunVo runPresetVo = presetMapper.getRunPreset();

        if(runPresetVo != null){
            dataMap.put("preset", runPresetVo);
            List<LayerStatusRunVo> runLayerVoList = layerMapper.getRunLayersStatus(runPresetVo.getPresetId());
            if(runLayerVoList.size() > 0){
                dataMap.put("layerInfoList", runLayerVoList);
                resultMap.put("data", dataMap);
            }
            else{
                // TODO : no content layers
                resultMap.put("code", ResponseCode.FAIL_INSERT_PRESET.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_PRESET.getMessage());
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

        if(presetCheck == 0){ // Not Exist : 0
            int postPresetResult = presetMapper.postPreset(param);

            if(postPresetResult == 1){ // Success : 1
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

        if(presetCheck == 1){  // Exist : 1
            int deletePresetResult = presetMapper.deletePreset(param);

            if(deletePresetResult > 1){ // Success
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

        if(presetCheck == 1){  // Exist : 1
            int patchPresetNameResult = presetMapper.patchPresetName(param);

            if(patchPresetNameResult == 1){ // Success : 1
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

        if(presetCheck == 1){  // Exist : 1
            int putPresetResult = presetMapper.putPreset(param);

            if(putPresetResult == 1){ // Success : 1

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
    public Map<String, Object> patchPresetRun(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> webClientResponse = new HashMap<>();

        String presetStatusPlay = "play";
        String presetStatusStop = "stop";
        String presetStatusPause = "pause";
        String presetStatusNone = "none";


        // TODO : run Preset이 없는 경우 예외 처리
        PresetStatusRunVo runPresetVo = presetMapper.getRunPreset();
        String controlType = (String) param.get("controlType");


        //////////////////////////////////////////////////////////////////// Set Agent
        // URL
        String agentUrl = protocol + agentIp + ":" + agentPort + agentPath;
        // request Body
        Map<String, Object> requestMap = createRequestBodyMap(param);
        JsonMapToPrint(requestMap);
        resultMap.put("data", requestMap);

        Boolean callYn = false;

        if(callYn) {
            // 프리셋 id가 다를 때, "적용 버튼" 클릭 >> agent->viewer까지 정보를 줘야하니 presetRun로직을 태우면서 상태값은 stop으로 하여 검은화면 표출
            if (controlType.equals("applyNew")) {
                int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId()); //[UPDATE] 기존 프리셋 버전
                int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();// 기존 프리셋 : play -> stop

                // TODO : PresetRun (Status = stop)
                // Agent Call
                webClientResponse = webClientFunction("applyAndNew", agentUrl, requestMap);

                if (webClientResponse.containsKey("data")) {
                    String response = (String) webClientResponse.get("data");
                    if (response.contains("200")) {
                        // 신규
                        int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                        // 신규 프리셋 : (stop, pause, none) -> play
                        param.put("controlType", presetStatusStop);
                        int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    } else {
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                    }
                } else {
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                }
            }
            // 프리셋 id가 같을 때, "적용 버튼" 클릭 >> 레이어당 플레이리스트 id만 업데이트 >> 뷰어는 새로운 플레이리스트 정보를 재생
            else if (controlType.equals("applySame")) {
                if (param.containsKey("layerInfoList")) {
                    // 레이어당 플레이리스트 업데이트
                    int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);
                    int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId()); //[UPDATE] 기존 프리셋 버전
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                }
            }
            // TODO : 적용 버튼 하나로 통일, 로직 확인 및 테스트 필요
            // 프론트에서는 프리셋의 정보가 확실치 않으니 여기서 판단
            // 1. 프리셋 play와 현재 프리셋id 매칭 확인
            // 1-1 같으면 레이어 for문으로 업데이트 할 레이어만 업데이트
            // 1-2 다르면 레이어 전체 업데이트 후, presetRun
            else if (controlType.equals("apply")) {

                // 1-1
                // 현재 프리셋 == 신규 프리셋
                if (param.get("presetId").equals(runPresetVo.getPresetId())) {
                    List<Map<String, Object>> tempLayerInfoList = (List<Map<String, Object>>) param.get("layerInfoList");
                    List<Map<String, Object>> resultLayerInfoList = new ArrayList<>();


                    // 업데이트 레이어 추출
                    for(Map<String, Object> layerInfo : tempLayerInfoList){
                        if((Boolean) layerInfo.get("updateYn")){
                            resultLayerInfoList.add(layerInfo);
                        }
                    }

                    // 레이어 업데이트
                    if(resultLayerInfoList.size() > 0){
                        param.put("layerInfoList", resultLayerInfoList);
                        int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param); //[UPDATE] 레이어 >> 플레이리스트
                        int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId()); // 기존 프리셋 버전 업데이트

                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    }
                    else{
                        resultMap.put("code", ResponseCode.FAIL_UPDATE_NOT_EXIST_PLAYLIST.getCode());
                        resultMap.put("message", ResponseCode.FAIL_UPDATE_NOT_EXIST_PLAYLIST.getMessage());
                    }
                }
                // 1-2
                // 현재 프리셋 != 신규 프리셋
                else{
                    // 현재 프리셋 상태 stop, 버전 업데이트

                    int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId()); //[UPDATE] 기존 프리셋 버전
                    int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();// 기존 프리셋 : play -> stop

                    // 전체 레이어 업데이트
                    int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param); //[UPDATE] 레이어 >> 플레이리스트

                    // 신규 프리셋 PresetRun

                    webClientResponse = webClientFunction("apply", agentUrl, requestMap);
                    if (webClientResponse.containsKey("data")) {
                        String response = (String) webClientResponse.get("data");
                        if (response.contains("200")) {
                            int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId")); //[UPDATE] 신규 프리셋 버전
                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                        } else {
                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                        }
                    } else {
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                    }
                }
            }
            // 프리셋 id가 같을 때, "재생 버튼" 클릭 >> 프리셋 id의 db 상태값이 play면 무시, stop/pause이면 play로 변경 >> 실시간으로 정보 요청하던 뷰어들이 play상태를 보고, 플레이리스트를 재생하기 시작.
            else if (controlType.equals("play")) {

                // Check : 기존 프리셋 == 신규 프리셋
                if (param.get("presetId").equals(runPresetVo.getPresetId())) {

                    // Check : 기존 상태값 == "play"
                    if (runPresetVo.getPresetStatus().equals(presetStatusPlay)) {

                        // Status : 이미 play 중 >> 예외 return
                        resultMap.put("code", ResponseCode.ALREADY_PLAYING_PRESET.getCode());
                        resultMap.put("message", ResponseCode.ALREADY_PLAYING_PRESET.getMessage());
                    }
                    // Set : 프리셋 상태 update
                    else {
                        int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId()); //[UPDATE] 기존 프리셋 버전
                        int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();// 기존 프리셋 : play -> stop

                        // TODO : presetRun 로직
                        // Agent Call
                        webClientResponse = webClientFunction("playAndOld", agentUrl, requestMap);

                        if (webClientResponse.containsKey("data")) {
                            String response = (String) webClientResponse.get("data");
                            if (response.contains("200")) {
                                // 신규
                                int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                                // 신규 프리셋 : (stop, pause, none) -> play
                                int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                            } else {
                                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                            }
                        } else {
                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                        }
                    }
                } else {
                    int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId()); //[UPDATE] 기존 프리셋 버전
                    int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();// 기존 프리셋 : play -> stop

                    // Agent Call
                    webClientResponse = webClientFunction("playAndNew", agentUrl, requestMap);

                    if (webClientResponse.containsKey("data")) {
                        String response = (String) webClientResponse.get("data");
                        if (response.contains("200")) {
                            // 신규
                            int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                            // 신규 프리셋 : (stop, pause, none) -> play
                            int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                        } else {
                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                        }
                    } else {
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                    }
                }
            } else {
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

        if(param.get("controlType").equals("pause") || param.get("controlType").equals("stop")) {
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
            System.out.println("[presetRun >> WAS to Agent] requestBodyMap = " + json);
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

}