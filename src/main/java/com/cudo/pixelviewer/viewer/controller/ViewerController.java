package com.cudo.pixelviewer.viewer.controller;

import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.viewer.service.ViewerService;
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
@RequestMapping("/api-viewer")
public class ViewerController {

    final ViewerService viewerService;

    @GetMapping("/playInfo/{screenId}/{presetId}/{layerId}")
    public Map<String, Object> getPlayInfo(HttpServletRequest request
                                        , @PathVariable String screenId
                                        , @PathVariable String presetId
                                        , @PathVariable String layerId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, layerId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = viewerService.getPlayInfo(screenId, presetId, layerId);
        }
        catch (Exception exception) {
            log.error("[Exception][getPlayInfo] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}