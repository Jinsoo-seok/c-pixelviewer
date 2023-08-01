package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.PresetService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/operate/preset")
public class PresetController {

    final PresetService presetService;

    @GetMapping
    public Map<String, Object> getPresetList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = presetService.getPresetList();
        }
        catch (Exception exception) {
            log.error("[Exception][getPresetList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @GetMapping("/{presetId}")
    public Map<String, Object> getPreset(HttpServletRequest request, @PathVariable String presetId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, presetId);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = presetService.getPreset(presetId);
        }
        catch (Exception exception) {
            log.error("[Exception][getPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }
    @GetMapping("/using")
    public Map<String, Object> getUsingPreset(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = presetService.getUsingPreset();
        }
        catch (Exception exception) {
            log.error("[Exception][getRunPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }


    @PostMapping
    public Map<String, Object> postPreset(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"screenId", "presetNm"};

        try {
            parameterValidation(param, keyList);
            parameterInt("screenId", param.get("screenId"), true);
            parameterString("presetNm", param.get("presetNm"), true, 0, null);

            responseMap = presetService.postPreset(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postPreset] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @DeleteMapping
    public Map<String, Object> deletePreset(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"presetId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);

            responseMap = presetService.deletePreset(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deletePreset] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deletePreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PatchMapping("/name")
    public Map<String, Object> patchPresetName(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"presetId", "presetNm"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);
            parameterString("presetNm", param.get("presetNm"), true, 0, null);

            responseMap = presetService.patchPresetName(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchPresetName] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchPresetName] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PutMapping
    public Map<String, Object> putPreset(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"presetId", "presetNm", "rowsize", "columnsize", "layerList", "deleteType"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);
            parameterString("presetNm", param.get("presetNm"), true, 0, null);
            parameterInt("rowsize", param.get("rowsize"), true);
            parameterInt("columnsize", param.get("columnsize"), true);
            parameterArray("layerList", param.get("layerList"), true);
            parameterBoolean("deleteType", param.get("deleteType"), true);

            responseMap = presetService.putPreset(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putPreset] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PatchMapping("/run")
    public Map<String, Object> patchPresetRun(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"screenId", "presetId", "layerInfoList"};

        try {
            parameterValidation(param, keyList);
            parameterInt("screenId", param.get("screenId"), true);
            parameterInt("presetId", param.get("presetId"), true);
            parameterArray("layerInfoList", param.get("layerInfoList"), true);

            responseMap = presetService.patchPresetRun(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchPresetRun] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchPresetRun] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PatchMapping("/control")
    public Map<String, Object> patchPresetControl(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"screenId", "presetId", "controlType"};

        try {
            parameterValidation(param, keyList);
            parameterInt("screenId", param.get("screenId"), true);
            parameterInt("presetId", param.get("presetId"), true);
            parameterString("controlType", param.get("controlType"), true, 0, null);

            responseMap = presetService.patchPresetControl(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchPresetStop] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchPresetStop] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }
}