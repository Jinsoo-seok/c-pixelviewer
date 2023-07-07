package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.operate.mapper.ScreenMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LayerToAgentVo;
import com.cudo.pixelviewer.vo.LayerVo;
import com.cudo.pixelviewer.vo.PresetVo;
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

        PresetVo runPresetVo = presetMapper.getRunPreset();

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

        String presetStatusPlay = "play";
        String presetStatusStop = "stop";
        String presetStatusPause = "pause";
        String presetStatusNone = "none";

        PresetVo runPresetVo = presetMapper.getRunPreset();
        String controlType = (String) param.get("controlType");

        if(controlType.equals("applyNew")){
            // 프리셋 id가 다를 때, "적용 버튼" 클릭 >> agent->viewer까지 정보를 줘야하니 presetRun로직을 태우면서 상태값은 stop으로 하여 검은화면 표출


            // TODO : PresetRun (Status = stop)
            // 신규
            int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));

            // 기존
            int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());

            // 기존 프리셋 : play -> stop
            int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

            // 신규 프리셋 : (stop, pause, none) -> play
            param.put("controlType", presetStatusStop);
            int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

            // TODO : PresetRun 로직
        }
        else if(controlType.equals("applySame")){
            // 프리셋 id가 같을 떄, "적용 버튼" 클릭 >> 레이어당 플레이리스트 id만 업데이트 >> 뷰어는 새로운 플레이리스트 정보를 재생
            
            if(param.containsKey("layerInfoList")){
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
                    // 신규
                    int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));

                    // 기존
                    int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());

                    // 기존 프리셋 : play -> stop
                    int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

                    // 신규 프리셋 : (stop, pause, none) -> play
                    int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);
                    // TODO : presetRun 로직
                }
            }
            else{
                // 신규
                int refreshPresetUpdateDateNew = presetMapper.refreshPresetUpdateDate(param.get("presetId"));

                // 기존
                int refreshPresetUpdateDateOld = presetMapper.refreshPresetUpdateDate(runPresetVo.getPresetId());

                // 기존 프리셋 : play -> stop
                int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

                // 신규 프리셋 : (stop, pause, none) -> play
                int presetStatusRunResult = presetMapper.patchPresetStatusSet(param);

                // TODO : presetRun 로직
                // TODO : 어차피 기존 프리셋 내려가고 신규 프리셋 실행
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_PRESET_CONTROL_TYPE.getMessage());
        }

        // TODO : 예외 처리
        int presetStatusClearResult = presetMapper.patchPresetStatusRunClear();

        if(param.containsKey("layerInfoList")){
            int setPlaylistSelectYnResult = playlistMapper.setPlaylistSelectYn(param);
        }

        // TODO : 프리셋 update_date
        int refreshPresetUpdateDate = presetMapper.refreshPresetUpdateDate(param.get("presetId"));

        String localIp = "106.245.226.42";
        String localPort = "9898";
        String baseUrl = "http://" + localIp + ":" + localPort + "/api-viewer/";

        String playInfoUrl = baseUrl + "playInfo";
        String updateCheckUrl = baseUrl + "updateAndHealthCheck";
        String previewImgUrl = baseUrl + "previewImg/10";

        requestBodyMap.put("playInfoUrl", playInfoUrl);
        requestBodyMap.put("updateCheckUrl", updateCheckUrl);
        requestBodyMap.put("previewImgUrl", previewImgUrl);

        String screenId = String.valueOf(param.get("screenId"));
        String presetId = String.valueOf(param.get("presetId"));

        Map<String, Object> screenInfo = screenMapper.getScreen(screenId);
        String removeKey = "screenNm";
        screenInfo.remove(removeKey);

        List<LayerToAgentVo>  layerInfos = presetMapper.getPresetLayersToAgent(presetId);


        log.info("test");
        requestBodyMap.put("presetId", param.get("presetId"));
        requestBodyMap.put("screenInfo", screenInfo);
        requestBodyMap.put("layers", layerInfos);

        resultMap.put("data", requestBodyMap);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestBodyMap);

            System.out.println("[presetRun >> WAS to Agent] requestBodyMap = " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ip = "192.168.123.12";
        String port = "8800";

        String agentUrl = "http://" + ip + ":" + port + "/vieweragent/Preset/layer-placement";
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
        // TODO : Agent 예외처리
        responseMono.subscribe(response -> {
            String data = response.toString();
//            System.out.println("data = " + data);

            Map<String, Object> responseMap = new HashMap<>();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                responseMap = objectMapper.readValue(data, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(responseMap.get("code").equals(200)){


                // TODO : [DB] preset Status >> RUN
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("presetId", param.get("presetId"));
                queryMap.put("presetStatus", "play");
                int patchPresetStatusSetResult = presetMapper.patchPresetStatusSet(queryMap);

                if(patchPresetStatusSetResult > 0) {
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                }
                else{
                    // TODO : Agent >> 프리셋 상태 업데이트 예외 처리
//                    resultMap.put("code", ResponseCode.FAIL_UPDATE_DISPLAY.getCode());
//                    resultMap.put("message", ResponseCode.FAIL_UPDATE_DISPLAY.getMessage());
                }
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DISPLAY_SETTING_TO_AGENT.getCode());
                resultMap.put("message", ResponseCode.FAIL_DISPLAY_SETTING_TO_AGENT.getMessage());

            }
        });

        // TODO : 위 예외 처리 끝나면 삭제
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
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

}