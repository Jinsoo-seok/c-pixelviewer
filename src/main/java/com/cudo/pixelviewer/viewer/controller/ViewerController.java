package com.cudo.pixelviewer.viewer.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.viewer.service.ViewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-viewer")
public class ViewerController {

    final ViewerService viewerService;

    @GetMapping("/playInfo/{screenId}/{presetId}/{layerId}")
    public ResponseEntity<Map<String, Object>> getPlayInfo(HttpServletRequest request
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


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        return new ResponseEntity(responseMap, headers, HttpStatus.OK);
    }

    @PutMapping("/updateAndHealthCheck")
    public Map<String, Object> putUpdateAndHealthCheck(HttpServletRequest request
                                                    , @RequestBody Map<String, Object> param){
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"screenId", "presetId", "layerId", "viewerStatus"};

        try {
            parameterValidation(param, keyList);
            parameterInt("screenId", param.get("screenId"), true);
            parameterInt("presetId", param.get("presetId"), true);
            parameterInt("layerId", param.get("layerId"), true);
            parameterMap("viewerStatus", param.get("viewerStatus"), true);

            responseMap = viewerService.putUpdateAndHealthCheck(param);

        }
        catch (ParamException paramException){
            log.error("[paramException][putUpdateAndHealthCheck] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putUpdateAndHealthCheck] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }
        Integer codeValue = 500;
        if(responseMap.size() > 0){
            codeValue = 200;
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, codeValue);

        return responseMap;
    }

    @PostMapping("/previewImg/{type}/{name}")
    public Map<String, Object> postPreviewImgUpload(HttpServletRequest request
            , @PathVariable String type
            , @PathVariable String name
            , MultipartFile file){
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - type : {}, name : {}", apiInfo, startTime, type, name);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            if (type.equals("10") || type.equals("20") || type.equals("30")
                || type.equals("40") || type.equals("50") || type.equals("60")
                || type.equals("70") || type.equals("80") || type.equals("90")
                || type.equals("100") || type.equals("110") || type.equals("120")
                || type.equals("130") || type.equals("140")){
                responseMap = viewerService.postPreviewImgUpload(type, name, file);
            }
            else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }
        }
        catch (Exception exception) {
            log.error("[Exception][postPreviewImgUpload] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}