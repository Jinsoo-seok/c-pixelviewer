package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.ExternalService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.*;
import static com.cudo.pixelviewer.util.ParameterUtils.parameterInt;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/operate/external")
public class ExternalController {

    final ExternalService externalService;

    // LayerObject Type
    // type (10, 20, 30, 40)   (LayerObjects)
    // 10 : 외부 영상(날씨)      (ExternalVideo)
    // 20 : 외부 정보           (External_info)
    // 30 : 자막               (Subtitles)
    // 40 : 외부 정보(미세 먼지) (External_info)


    @GetMapping("/video/{layerId}")
    public Map<String, Object> getExternalVideo(HttpServletRequest request
                                            , @PathVariable String layerId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, layerId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = externalService.getExternalVideo(layerId);
        }
        catch (Exception exception) {
            log.error("[Exception][getExternalVideo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PostMapping("/video")
    public Map<String, Object> postExternalVideo(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "objectNm", "type", "posX", "posY", "width", "height", "ord", "rtspUrl", "videoNm", "videoFormat"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterString("objectNm", param.get("objectNm"), true, 0, null);
            parameterInt("type", param.get("type"), true);
            parameterInt("posX", param.get("posX"), true);
            parameterInt("posY", param.get("posY"), true);
            parameterInt("width", param.get("width"), true);
            parameterInt("height", param.get("height"), true);
            parameterInt("ord", param.get("ord"), true);
            parameterString("rtspUrl", param.get("rtspUrl"), true, 0, null);
            parameterInt("videoNm", param.get("videoNm"), true);
            parameterString("videoFormat", param.get("videoFormat"), true, 0, null);

            responseMap = externalService.postExternalVideo(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postExternalVideo] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postExternalVideo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PostMapping("/info")
    public Map<String, Object> postExternalInfo(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"layerId", "objectNm", "type", "posX", "posY", "width", "height", "ord"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterString("objectNm", param.get("objectNm"), true, 0, null);
            parameterInt("type", param.get("type"), true);
            parameterInt("posX", param.get("posX"), true);
            parameterInt("posY", param.get("posY"), true);
            parameterInt("width", param.get("width"), true);
            parameterInt("height", param.get("height"), true);
            parameterInt("ord", param.get("ord"), true);

            responseMap = externalService.postExternalInfo(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postExternalInfo] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postExternalInfo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}