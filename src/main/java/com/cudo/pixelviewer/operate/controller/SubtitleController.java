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
                "layerId", "type"
                , "posX", "posY", "width", "height", "ord"
                , "scrollWay", "scrollSpeed", "subtitleStyleArray"};

        try {
            parameterValidation(param, keyList);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("type", param.get("type"), true);
            parameterInt("posX", param.get("posX"), true);
            parameterInt("posY", param.get("posY"), true);
            parameterInt("width", param.get("width"), true);
            parameterInt("height", param.get("height"), true);
            parameterInt("ord", param.get("ord"), true);

            parameterInt("scrollWay", param.get("scrollWay"), true);
            parameterInt("scrollSpeed", param.get("scrollSpeed"), true);
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

    @PatchMapping("/name")
    public Map<String, Object> patchSubtitleName(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"subtitleId", "subtitleNm"};

        try {
            parameterValidation(param, keyList);
            parameterInt("subtitleId", param.get("subtitleId"), true);
            parameterString("subtitleNm", param.get("subtitleNm"), true, 0, null);

            responseMap = subtitleService.patchSubtitleName(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSubtitleName] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSubtitleName] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

}