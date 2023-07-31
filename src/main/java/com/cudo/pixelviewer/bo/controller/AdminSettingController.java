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
import static com.cudo.pixelviewer.util.ResponseCode.FAIL_GET_EXTERNALS_DATA;

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

    @PutMapping()
    public Map<String, Object> putAdminSetting(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {
                "viewTopmostEn",
                "viewTemphumiEn",
                "ledCommType",
                "ledPresetEn",
                "ledInputSelectEn",
                "ledPresetCount",
                "loginEn",
                "imgDefaultPlaytime",
                "externalinfoArea",
                "nx", "ny"
        };

        try {
            parameterValidation(param, keyList);
            parameterBoolean("viewTopmostEn", param.get("viewTopmostEn"), true);
            parameterBoolean("viewTemphumiEn", param.get("viewTemphumiEn"), true);
            parameterBoolean("ledCommType", param.get("ledCommType"), true);
            parameterBoolean("ledPresetEn", param.get("ledPresetEn"), true);
            parameterBoolean("ledInputSelectEn", param.get("ledInputSelectEn"), true);
            parameterInt("ledPresetCount", param.get("ledPresetCount"), true);
            parameterBoolean("loginEn", param.get("loginEn"), true);
            parameterInt("imgDefaultPlaytime", param.get("imgDefaultPlaytime"), true);
            parameterString("externalinfoArea", param.get("externalinfoArea"), true, 0, null);
            parameterInt("nx", param.get("nx"), true);
            parameterInt("ny", param.get("ny"), true);

            responseMap = adminSettingService.putAdminSetting(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putAdminSetting] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            if(exception.getMessage().equals("[FAIL] GET Externals Data")){
                responseMap.put("code", FAIL_GET_EXTERNALS_DATA.getCode());
                responseMap.put("message", FAIL_GET_EXTERNALS_DATA.getMessage());
            }
            log.error("[Exception][putAdminSetting] - {}", exception.getMessage());
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