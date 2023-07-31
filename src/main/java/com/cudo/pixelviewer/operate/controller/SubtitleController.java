package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.SubtitleService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public Map<String, Object> postSubtitle(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {
                "presetId", "screenId"
                , "layerId", "type"
                , "subFirstEn", "subSecondEn"
                , "subtitleStyleArray"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);
            parameterInt("screenId", param.get("screenId"), true);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("type", param.get("type"), true);
            parameterBoolean("subFirstEn", param.get("subFirstEn"), true);
            parameterBoolean("subSecondEn", param.get("subSecondEn"), true);
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

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));    
        return responseMap;
    }
}