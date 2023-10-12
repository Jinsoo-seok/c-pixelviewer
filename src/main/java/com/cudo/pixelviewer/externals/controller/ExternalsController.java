package com.cudo.pixelviewer.externals.controller;

import com.cudo.pixelviewer.externals.service.ExternalsService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/externals")
public class ExternalsController {

    final ExternalsService externalsService;

//    @GetMapping("/weather")
//    public Map<String, Object> getExternalWeather(HttpServletRequest request) {
//        long startTime = System.currentTimeMillis();
//        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
//        log.info("{} [START] [{}]", apiInfo, startTime);
//
//        Map<String, Object> responseMap = new HashMap<>();
//        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
//
//
//        try {
//            responseMap = externalsService.getExternalWeather();
//        }
//        catch (Exception exception) {
//            log.error("[Exception][getExternalWeather] - {}", exception.getMessage());
//            responseMap.put("exceptionMessage", exception.getMessage());
//        }
//
//        long endTime = System.currentTimeMillis();
//        long procTime = endTime-startTime;
//        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));
//
//        return responseMap;
//    }
//
//
//    @GetMapping("/air")
//    public Map<String, Object> getExternalAir(HttpServletRequest request) {
//        long startTime = System.currentTimeMillis();
//        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
//        log.info("{} [START] [{}]", apiInfo, startTime);
//
//        Map<String, Object> responseMap = new HashMap<>();
//        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
//
//
//        try {
//            responseMap = externalsService.getExternalAir();
//        }
//        catch (Exception exception) {
//            log.error("[Exception][getExternalAir] - {}", exception.getMessage());
//            responseMap.put("exceptionMessage", exception.getMessage());
//        }
//
//        long endTime = System.currentTimeMillis();
//        long procTime = endTime-startTime;
//        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));
//
//        return responseMap;
//    }
}