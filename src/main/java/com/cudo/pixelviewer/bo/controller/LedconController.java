package com.cudo.pixelviewer.bo.controller;

import com.cudo.pixelviewer.bo.service.LedconService;
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
@RequestMapping("/api-manager/ledcon")
public class LedconController {

    final LedconService ledconService;

    @GetMapping
    public Map<String, Object> getLedconList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = ledconService.getLedconList();
        }
        catch (Exception exception) {
            log.error("[Exception][getLedconList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PostMapping
    public Map<String, Object> postLedcon(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        // TODO : 추후 확장 예정
//        String[] keyList = {"ip", "port", "modelNm", "serialNo", "version", "firmwareVer"
//                , "state", "spec1", "spec2", "spec3"
//                , "connectFl", "videoinputMode"
//                , "inputResolutionWidth", "inputResolutionHeight", "frame"
//                , "colorDepth", "colorMode", "hdrEn"
//                , "preset", "brightness", "colorTemp"};
        String[] keyList = {"ip", "port", "modelNm", "serialNo"};

        try {
            parameterValidation(param, keyList);
            parameterString("ip", param.get("ip"), true, 0, null);
            parameterInt("port", param.get("port"), true);
            parameterString("modelNm", param.get("modelNm"), true, 0, null);
            parameterString("serialNo", param.get("serialNo"), true, 0, null);

            responseMap = ledconService.postLedcon(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postLedcon] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postLedcon] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @DeleteMapping
    public Map<String, Object> deleteLedcon(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"ledControllerId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("ledControllerId", param.get("ledControllerId"), true);

            responseMap = ledconService.deleteLedcon(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deleteLedcon] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deleteLedcon] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PutMapping
    public Map<String, Object> putLedcon(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        // TODO : 추후 확장 예정
//        String[] keyList = {
//                "ledControllerId",
//                "ip", "port", "modelNm", "version", "serialNo", "firmwareVer", "state",
//                "spec1", "spec2", "spec3", "connectFl",
//                "videoinputMode", "inputResolutionWidth", "inputResolutionHeight", "frame",
//                "colorDepth", "colorMode", "hdrEn", "preset", "brightness", "colorTemp"
//        };
        String[] keyList = {"ledControllerId", "ip", "port", "modelNm", "serialNo"};

        try {
            parameterValidation(param, keyList);
            parameterInt("ledControllerId", param.get("ledControllerId"), true);
            parameterString("ip", param.get("ip"), true, 0, null);
            parameterInt("port", param.get("port"), true);
            parameterString("modelNm", param.get("modelNm"), true, 0, null);
            parameterString("serialNo", param.get("serialNo"), true, 0, null);

//            parameterString("version", param.get("version"), true, 0, null);
//            parameterString("firmwareVer", param.get("firmwareVer"), true, 0, null);
//
//            parameterInt("state", param.get("state"), true);
//            parameterString("spec1", param.get("spec1"), true, 0, null);
//            parameterString("spec2", param.get("spec2"), true, 0, null);
//            parameterString("spec3", param.get("spec3"), true, 0, null);
//
//            parameterInt("connectFl", param.get("connectFl"), true);
//
//            parameterString("videoinputMode", param.get("videoinputMode"), true, 0, null);
//            parameterString("inputResolutionWidth", param.get("inputResolutionWidth"), true, 0, null);
//            parameterString("inputResolutionHeight", param.get("inputResolutionHeight"), true, 0, null);
//            parameterInt("frame", param.get("frame"), true);
//
//            parameterInt("colorDepth", param.get("colorDepth"), true);
//            parameterString("colorMode", param.get("colorMode"), true, 0, null);
//
//            parameterInt("hdrEn", param.get("hdrEn"), true);
//            parameterInt("preset", param.get("preset"), true);
//            parameterInt("brightness", param.get("brightness"), true);
//            parameterInt("colorTemp", param.get("colorTemp"), true);

            responseMap = ledconService.putLedcon(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putLedcon] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putLedcon] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }
        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}