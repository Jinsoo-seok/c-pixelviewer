package com.cudo.pixelviewer.bo.controller;

import com.cudo.pixelviewer.bo.service.PwrconService;
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
@RequestMapping("/api-manager/pwrcon")
public class PwrconController {

    final PwrconService pwrconService;

    @GetMapping
    public Map<String, Object> getPwrconList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = pwrconService.getPwrconList();
        }
        catch (Exception exception) {
            log.error("[Exception][getPwrconList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PostMapping
    public Map<String, Object> postPwrcon(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"ip", "port", "modelNm", "serialNo"};

        try {
            parameterValidation(param, keyList);
            parameterString("ip", param.get("ip"), true, 0, null);
            parameterInt("port", param.get("port"), true);
            parameterString("modelNm", param.get("modelNm"), true, 0, null);
            parameterString("serialNo", param.get("serialNo"), true, 0, null);

            responseMap = pwrconService.postPwrcon(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postPwrcon] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postPwrcon] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @DeleteMapping
    public Map<String, Object> deletePwrcon(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"condeviceId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("condeviceId", param.get("condeviceId"), true);

            responseMap = pwrconService.deletePwrcon(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deletePwrcon] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deletePwrcon] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PutMapping
    public Map<String, Object> putPwrcon(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

//        String[] keyList = {
//                "condeviceId", "ip", "port", "modelNm", "serialNo", "mpcmuCnt", "dpcmuCnt", "state",
//                "spec1", "spec2", "spec3", "ledPwr", "temp", "humi", "ledKey"
//        };
        String[] keyList = {"condeviceId", "ip", "port", "modelNm", "serialNo"};

        try {
            parameterValidation(param, keyList);
            parameterInt("condeviceId", param.get("condeviceId"), true);
            parameterString("ip", param.get("ip"), true, 0, null);
            parameterInt("port", param.get("port"), true);
            parameterString("modelNm", param.get("modelNm"), true, 0, null);
            parameterString("serialNo", param.get("serialNo"), true, 0, null);
//            parameterInt("mpcmuCnt", param.get("mpcmuCnt"), true);
//            parameterInt("dpcmuCnt", param.get("dpcmuCnt"), true);
//            parameterInt("state", param.get("state"), true);
//            parameterString("state", param.get("state"), true, 0, null);
//            parameterString("spec1", param.get("spec1"), true, 0, null);
//            parameterString("spec2", param.get("spec2"), true, 0, null);
//            parameterString("spec3", param.get("spec3"), true, 0, null);
//            parameterInt("ledPwr", param.get("ledPwr"), true);
//            parameterInt("temp", param.get("temp"), true);
//            parameterInt("humi", param.get("humi"), true);
//            parameterString("ledKey", param.get("ledKey"), true, 0, null);

            responseMap = pwrconService.putPwrcon(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putPwrcon] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putPwrcon] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}