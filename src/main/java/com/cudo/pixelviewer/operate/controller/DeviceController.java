package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.DeviceService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.parameterInt;
import static com.cudo.pixelviewer.util.ParameterUtils.parameterValidation;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/device-control")
public class DeviceController {

    final DeviceService deviceService;

    /**
     * * LED 전광판 전원 상태 확인
     */
    @GetMapping("/power")
    public Map<String, Object> getDevicePower(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = deviceService.getDevicePower();
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime - startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    /**
     * * LED 전광판 전원 제어
     */
    @PatchMapping("/power")
    public Map<String, Object> setDevicePower(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"powerState"};

        try {
            parameterValidation(param, keyList);
            parameterInt("powerState", param.get("powerState"), true);

            responseMap = deviceService.setDevicePower((Integer) param.get("powerState"));
        } catch (ParamException paramException) {
            log.error("[paramException] - {}", paramException.getMessage());

            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime - startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    /**
     * * 온도/습도 값 조회
     */
    @GetMapping("/temphumi")
    public Map<String, Object> getTempHumi(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        try {
            responseMap = deviceService.getTempHumi();
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime - startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}
