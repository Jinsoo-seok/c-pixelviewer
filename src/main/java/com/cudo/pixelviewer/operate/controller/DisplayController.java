package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.DisplayService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.parameterInt;
import static com.cudo.pixelviewer.util.ParameterUtils.parameterValidation;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/operate/display")
public class DisplayController {

    final DisplayService displayService;

    @GetMapping("/{screenId}")
    public Map<String, Object> getDisplayList(HttpServletRequest request
                                              , @PathVariable String screenId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());



        try {
            responseMap = displayService.getDisplayList(screenId);
        }
        catch (Exception exception) {
            log.error("[Exception][getDisplayList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @GetMapping("/detail/{displayId}")
    public Map<String, Object> getDisplay(HttpServletRequest request
                                        , @PathVariable String displayId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, displayId);

        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());



        try {
            responseMap = displayService.getDisplay(displayId);
        }
        catch (Exception exception) {
            log.error("[Exception][getDisplay] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @GetMapping("/portlist")
    public Map<String, Object> getDisplayPortlist(HttpServletRequest request){
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());



        try {
            responseMap = displayService.getDisplayPortlist();
        }
        catch (Exception exception) {
            log.error("[Exception][getDisplayPortlist] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }


    @PatchMapping("/testpattern")
    public Map<String, Object> patchDisplayTestpattern(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"screenId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("screenId", param.get("screenId"), true);

            responseMap = displayService.patchDisplayTestpattern(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchDisplayTestpattern] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchDisplayTestpattern] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }
}