package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.SubtitleService;
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
@RequestMapping("/api-manager/operate/subtitle")
public class SubtitleController {

    final SubtitleService subtitleService;

    @PostMapping
    public Map<String, Object> postSubtitle(HttpServletRequest request
                                            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {
                "presetId", "screenId"
                , "layerId", "type"
                , "subtitleFirstYn", "subtitleSecondYn"
//                , "posX", "posY", "width", "height", "ord"
//                , "scrollWay", "scrollSpeed", "scrollStartLocation"
                , "subtitleStyleArray"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);
            parameterInt("screenId", param.get("screenId"), true);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("type", param.get("type"), true);
            parameterBoolean("subtitleFirstYn", param.get("subtitleFirstYn"), true);
            parameterBoolean("subtitleSecondYn", param.get("subtitleSecondYn"), true);
//            parameterInt("posX", param.get("posX"), true);
//            parameterInt("posY", param.get("posY"), true);
//            parameterInt("width", param.get("width"), true);
//            parameterInt("height", param.get("height"), true);
//            parameterInt("ord", param.get("ord"), true);
//
//            parameterInt("scrollWay", param.get("scrollWay"), true);
//            parameterInt("scrollSpeed", param.get("scrollSpeed"), true);
//            parameterInt("scrollStartLocation", param.get("scrollStartLocation"), true);

            parameterArray("subtitleStyleArray", param.get("subtitleStyleArray"), true);

            responseMap = subtitleService.postSubtitle(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postSubtitle] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postSubtitle] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/text")
    public Map<String, Object> patchSubtitleText(HttpServletRequest request
                                                 , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "subtitleText"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterString("subtitleText", param.get("subtitleText"), true, 0, null);

            responseMap = subtitleService.patchSubtitleText(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSubtitleText] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSubtitleText] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/location")
    public Map<String, Object> patchSubtitleLocation(HttpServletRequest request
                                                     , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "posX", "posY"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("posX", param.get("posX"), true);
            parameterInt("posY", param.get("posY"), true);

            responseMap = subtitleService.patchSubtitleLocation(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSubtitleLocation] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSubtitleLocation] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/size")
    public Map<String, Object> patchSubtitleSize(HttpServletRequest request
                                                 , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "width", "height"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("width", param.get("width"), true);
            parameterInt("height", param.get("height"), true);

            responseMap = subtitleService.patchSubtitleSize(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSubtitleSize] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSubtitleSize] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/style")
    public Map<String, Object> patchSubtitleStyle(HttpServletRequest request
                                                  , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "subtitleStyleArray"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterArray("subtitleStyleArray", param.get("subtitleStyleArray"), true);

            responseMap = subtitleService.patchSubtitleStyle(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSubtitleStyle] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSubtitleStyle] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/scroll")
    public Map<String, Object> patchSubtitleScroll(HttpServletRequest request
                                                   , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "scrollWay", "scrollSpeed", "scrollStartLocation"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("scrollWay", param.get("scrollWay"), true);
            parameterInt("scrollSpeed", param.get("scrollSpeed"), true);
            parameterInt("scrollStartLocation", param.get("scrollStartLocation"), true);

            responseMap = subtitleService.patchSubtitleScroll(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSubtitleScroll] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSubtitleScroll] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

}