package com.cudo.pixelviewer.bo.controller;

import com.cudo.pixelviewer.bo.service.AdminSettingService;
import com.cudo.pixelviewer.config.ParamException;
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
@RequestMapping("/api-manager/adminsetting")
public class AdminSettingController {

    final AdminSettingService adminSettingService;

    @GetMapping
    public Map<String, Object> getAdminSettingList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = adminSettingService.getAdminSettingList();
        }
        catch (Exception exception) {
            log.error("[Exception][getAdminSettingList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/layer-topmost")
    public Map<String, Object> patchLayerTopMost(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"type"};

        try {
            parameterValidation(param, keyList);
            parameterInt("type", param.get("type"), true);

            responseMap = adminSettingService.patchLayerTopMost(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchLayerTopMost] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchLayerTopMost] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/temphumi-enable")
    public Map<String, Object> patchTempHumi(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"type"};

        try {
            parameterValidation(param, keyList);
            parameterInt("type", param.get("type"), true);

            responseMap = adminSettingService.patchTempHumi(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchTempHumi] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchTempHumi] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/con-type")
    public Map<String, Object> patchControlType(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"type"};

        try {
            parameterValidation(param, keyList);
            parameterInt("type", param.get("type"), true);

            responseMap = adminSettingService.patchControlType(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchControlType] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchControlType] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/ledpreset-enable")
    public Map<String, Object> patchLedPresetEnable(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"type"};

        try {
            parameterValidation(param, keyList);
            parameterInt("type", param.get("type"), true);

            responseMap = adminSettingService.patchLedPresetEnable(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchLedPresetEnable] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchLedPresetEnable] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/ledpreset-count")
    public Map<String, Object> patchLedPresetCount(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"presetCount"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetCount", param.get("presetCount"), true);

            responseMap = adminSettingService.patchLedPresetCount(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchLedPresetCount] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchLedPresetCount] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/ledinput-enable")
    public Map<String, Object> patchLedInputEnable(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"type"};

        try {
            parameterValidation(param, keyList);
            parameterInt("type", param.get("type"), true);

            responseMap = adminSettingService.patchLedInputEnable(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchLedInputEnable] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchLedInputEnable] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @GetMapping("/displayinfo")
    public Map<String, Object> getDisplayInfoList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        try {
            responseMap = adminSettingService.getDisplayInfoList();
        }
        catch (Exception exception) {
            log.error("[Exception][getDisplayInfoList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @GetMapping("/displayinfo/{displayId}")
    public Map<String, Object> getDisplayInfo(HttpServletRequest request
                                                  , @PathVariable String displayId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, displayId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        try {
            responseMap = adminSettingService.getDisplayInfo(displayId);
        }
        catch (Exception exception) {
            log.error("[Exception][getDisplayInfo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PostMapping("/displayinfo")
    public Map<String, Object> postDisplayInfo(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"displayNm", "gpuNm"
                , "posX", "posY"
                , "width", "height"
                , "primaryFl"
        };

        try {
            parameterValidation(param, keyList);
            parameterString("displayNm", param.get("displayNm"), true, 0, null);
            parameterString("gpuNm", param.get("gpuNm"), true, 0, null);
            parameterInt("posX", param.get("posX"), true);
            parameterInt("posY", param.get("posY"), true);
            parameterInt("width", param.get("width"), true);
            parameterInt("height", param.get("height"), true);
            parameterInt("primaryFl", param.get("primaryFl"), true);

            responseMap = adminSettingService.postDisplayInfo(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postDisplayInfo] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postDisplayInfo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PutMapping("/displayinfo")
    public Map<String, Object> putDisplayInfo(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {
                "displayId"
                ,"displayNm", "gpuNm"
                , "posX", "posY"
                , "width", "height"
                , "primaryFl"
        };

        try {
            parameterValidation(param, keyList);
            parameterInt("displayId", param.get("displayId"), true);
            parameterString("displayNm", param.get("displayNm"), true, 0, null);
            parameterString("gpuNm", param.get("gpuNm"), true, 0, null);
            parameterInt("posX", param.get("posX"), true);
            parameterInt("posY", param.get("posY"), true);
            parameterInt("width", param.get("width"), true);
            parameterInt("height", param.get("height"), true);
            parameterInt("primaryFl", param.get("primaryFl"), true);

            responseMap = adminSettingService.putDisplayInfo(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putDisplayInfo] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putDisplayInfo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @DeleteMapping("/displayinfo")
    public Map<String, Object> deleteDisplayInfo(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"displayId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("displayId", param.get("displayId"), true);

            responseMap = adminSettingService.deleteDisplayInfo(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deleteDisplayInfo] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deleteDisplayInfo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}