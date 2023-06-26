package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.operate.mapper.ScreenMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LayerToAgentVo;
import com.cudo.pixelviewer.vo.LayerVo;
import com.cudo.pixelviewer.vo.PresetVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


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

    // TODO : 중복체크, 에러코드
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postPreset(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

//        int presetCheck = presetMapper.postPresetValid(param);
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

    // TODO : 에러코드
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deletePreset(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int presetCheck = presetMapper.deletePresetValid(param);

        if(presetCheck == 1){  // Exist : 1
            int deletePresetResult = presetMapper.deletePreset(param);

            if(deletePresetResult == 1){ // Success : 1
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
                int deleteLayerResult = presetMapper.putPresetDeleteLayers(param);

                // TODO : 예외처리
                int saveLayerResult = presetMapper.saveLayer(param);

                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_SCREEN.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_SCREEN.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_SCREEN.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_SCREEN.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchPresetRun(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> requestBodyMap = new HashMap<>();

        String localIp = "192.168.123.89";
        String localPort = "80";
        String baseUrl = "http://" + localIp + ":" + localPort + "/api-viewer/";

        String agentUrl = "http://host:port/vieweragent/Preset/layer-placemen";


        // TODO : 쿼리스트링에 LayerID?
        String screenIdQueryString = "screenId=" + param.get("screenId");
        String presetIdQueryString = "presetId=" + param.get("presetId");

//        String playInfoUrl = baseUrl + "playInfo?" + screenIdQueryString + "&" + presetIdQueryString;
//        String updateCheckUrl = baseUrl + "updateAndHealthCheck?" + screenIdQueryString + "&" + presetIdQueryString;
//        String previewImgUrl = baseUrl + "previewImg?" + screenIdQueryString;

        String playInfoUrl = baseUrl + "playInfo";
        String updateCheckUrl = baseUrl + "updateAndHealthCheck";
        String previewImgUrl = baseUrl + "previewImg";

        requestBodyMap.put("playInfoUrl", playInfoUrl);
        requestBodyMap.put("updateCheckUrl", updateCheckUrl);
        requestBodyMap.put("previewImgUrl", previewImgUrl);

//        Integer screenId = Integer.parseInt((String) param.get("screenId"));
//        Integer presetId = Integer.parseInt((String) param.get("presetId"));
        String screenId = String.valueOf(param.get("screenId"));
        String presetId = String.valueOf(param.get("presetId"));

        // TODO : 파라미터 매칭해야함
        Map<String, Object> screenInfo = screenMapper.getScreen(screenId);
//        List<LayerVo>  layerInfos = presetMapper.getPresetLayers(presetId);
        List<LayerToAgentVo>  layerInfos = presetMapper.getPresetLayersToAgent(presetId);

//        Map<String, Object> screenInfo = screenMapper.getScreen((String) param.get("screenId"));
//        List<LayerVo>  layerInfos = presetMapper.getPresetLayers((String) param.get("presetId"));

        log.info("test");
        requestBodyMap.put("presetId", param.get("presetId"));
        requestBodyMap.put("screenInfo", screenInfo);
        requestBodyMap.put("layers", layerInfos);

        resultMap.put("data", requestBodyMap);

        // TODO : Agent 연동
//        import org.springframework.http.HttpHeaders;
//        import org.springframework.http.HttpMethod;
//        import org.springframework.http.MediaType;
//        import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//        import org.springframework.web.reactive.function.BodyInserters;
//        import org.springframework.web.reactive.function.client.ClientResponse;
//        import org.springframework.web.reactive.function.client.WebClient;
//        import reactor.core.publisher.Mono;

//        // WebClient 생성
//        WebClient webClient = WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector())
//                .baseUrl("http://host:port/vieweragent/Preset/layer-placemen") // 요청 URL 설정
//                .build();
//
//        // 요청 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // POST 요청 보내기
//        Mono<ClientResponse> responseMono = webClient.method(HttpMethod.POST)
//                .uri(agentUrl)
//                .headers(httpHeaders -> httpHeaders.addAll(headers))
//                .body(BodyInserters.fromValue(requestBodyMap))
//                .exchange()
//                .flatMap(response -> response.bodyToMono(ClientResponse.class));
//
//        // 응답 처리
//        responseMono.subscribe(response -> {
//            int statusCode = response.rawStatusCode();
//            if (statusCode == 200) {
//                // 성공적인 응답 처리
//                log.info("요청이 성공적으로 처리되었습니다.");
//            } else {
//                // 응답 실패 처리
//                log.info("요청이 실패했습니다. 상태 코드: {}", statusCode);
//            }
//        });

        // TODO : [DB] preset Status >> RUN
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("presetId", param.get("presetId"));
        queryMap.put("presetStatus", "RUN");
         int patchPresetStatusResult = presetMapper.patchPresetStatus(queryMap);

        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchPresetStop(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        String agentUrl = "http://host:port/vieweragent/Preset/layer-placemen";

        // TODO : Agent 연동
        /*

         */
        // TODO : [DB] preset Status >> RUN
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("presetId", param.get("presetId"));
        queryMap.put("presetStatus", "WAIT");
         int patchPresetStatusResult = presetMapper.patchPresetStatus(queryMap);



        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        return resultMap;
    }

}