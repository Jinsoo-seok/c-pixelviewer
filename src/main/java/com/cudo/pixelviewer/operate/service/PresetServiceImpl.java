package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.operate.mapper.ScreenMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LayerToAgentVo;
import com.cudo.pixelviewer.vo.LayerVo;
import com.cudo.pixelviewer.vo.PresetStatusRunVo;
import com.cudo.pixelviewer.vo.PresetVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
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
        List<LayerVo> layerVoList = presetMapper.getPresetLayers(presetId);

        if(presetVo != null){
            dataMap.put("preset", presetVo);
            dataMap.put("layerList", layerVoList);
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
            resultMap.put("data", dataMap);

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
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> webClientResponse = new HashMap<>();

        String presetStatusPlay = "play";
        String presetStatusStop = "stop";
        String presetStatusPause = "pause";
        String presetStatusNone = "none";

        PresetStatusRunVo runPresetVo = presetMapper.getRunPreset();
        String controlType = (String) param.get("controlType");


        // Set Agent
        String ip = "192.168.123.12";
        String port = "8800";

        String agentUrl = "http://" + ip + ":" + port + "/vieweragent/Preset/layer-placement";

        // To Agent
        Map<String, Object> requestMap = createRequestBodyMap(param);
        JsonMapToPrint(requestMap);
        resultMap.put("data", requestMap);

        if(controlType.equals("applyNew")){
            // 프리셋 id가 다를 때, "적용 버튼" 클릭 >> agent->viewer까지 정보를 줘야하니 presetRun로직을 태우면서 상태값은 stop으로 하여 검은화면 표출

            // 기존
            int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());

            // 기존 프리셋 : play -> stop
            int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

            // TODO : PresetRun (Status = stop)
            // Agent Call
            webClientResponse = webClientFunction("applyAndNew", agentUrl, requestMap);

            if(webClientResponse.containsKey("data")){
                String response = (String) webClientResponse.get("data");
                if(response.contains("200")){
                    // 신규
                    int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                    // 신규 프리셋 : (stop, pause, none) -> play
                    param.put("controlType", presetStatusStop);
                    int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                }
                else{
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                }
            }
            else{
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }
        }
        else if(controlType.equals("applySame")){
            // 프리셋 id가 같을 떄, "적용 버튼" 클릭 >> 레이어당 플레이리스트 id만 업데이트 >> 뷰어는 새로운 플레이리스트 정보를 재생
            
            if(param.containsKey("layerInfoList")){
                // 레이어당 플레이리스트 업데이트
                int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);

                // 기존
                int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());

                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }
        }
        else if(controlType.equals("play")){
            // 프리셋 id가 같을 떄, "재생버튼" 클릭 >> 프리셋 id의 db 상태값이 play면 무시, stop/pause이면 play로 변경 >> 실시간으로 정보 요청하던 뷰어들이 play상태를 보고, 플레이리스트를 재생하기 시작.
            
            // Check : 기존 프리셋 == 신규 프리셋
            if(param.get("presetId").equals(runPresetVo.getPresetId())){

                // Check : 기존 상태값 == "play"
                if(runPresetVo.getPresetStatus().equals(presetStatusPlay)){

                    // Status : 이미 play 중 >> 예외 return
                    resultMap.put("code", ResponseCode.ALREADY_PLAYING_PRESET.getCode());
                    resultMap.put("message", ResponseCode.ALREADY_PLAYING_PRESET.getMessage());
                }
                // Set : 프리셋 상태 update
                else{
                    // 기존
                    int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());
                    // 기존 프리셋 : play -> stop
                    int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

                    // TODO : presetRun 로직
                    // Agent Call
                    webClientResponse = webClientFunction("playAndOld", agentUrl, requestMap);

                    if(webClientResponse.containsKey("data")){
                        String response = (String) webClientResponse.get("data");
                        if(response.contains("200")){
                            // 신규
                            int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                            // 신규 프리셋 : (stop, pause, none) -> play
                            int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                        }
                        else{
                            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                        }
                    }
                    else{
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                    }
                }
            }
            else{
                // 기존
                int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());
                // 기존 프리셋 : play -> stop
                int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

                // Agent Call
                webClientResponse = webClientFunction("playAndNew", agentUrl, requestMap);

                if(webClientResponse.containsKey("data")){
                    String response = (String) webClientResponse.get("data");
                    if(response.contains("200")){
                        // 신규
                        int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                        // 신규 프리셋 : (stop, pause, none) -> play
                        int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    }
                    else{
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                    }
                }
                else{
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
                }
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE.getMessage());
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

        String localIp = "106.245.226.42";
        String localPort = "9898";
        String baseUrl = "http://" + localIp + ":" + localPort + "/api-viewer/";

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