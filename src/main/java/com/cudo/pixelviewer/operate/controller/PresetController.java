package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.PresetService;
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
@RequestMapping("/api-manager/operate/preset")
public class PresetController {

    final PresetService presetService;

    @GetMapping
    public Map<String, Object> getPresetList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = presetService.getPresetList();
        }
        catch (Exception exception) {
            log.error("[Exception][getPresetList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @GetMapping("/{presetId}")
    public Map<String, Object> getPreset(HttpServletRequest request
                                        , @PathVariable String presetId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, presetId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = presetService.getPreset(presetId);
        }
        catch (Exception exception) {
            log.error("[Exception][getPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }


    @PostMapping
    public Map<String, Object> postPreset(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));


        try {
            responseMap = presetService.postPreset(param);
        }
        catch (Exception exception) {
            log.error("[Exception][postPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @DeleteMapping
    public Map<String, Object> deletePreset(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"presetId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);

            responseMap = presetService.deletePreset(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deletePreset] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deletePreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PatchMapping("/name")
    public Map<String, Object> patchPresetName(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"presetId", "presetNm"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);
            parameterString("presetNm", param.get("presetNm"), true, 0, null);

            responseMap = presetService.patchPresetName(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchPresetName] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchPresetName] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    // TODO : 테이블 필드 수정되면 작업 예정
    @PutMapping
    public Map<String, Object> putPreset(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"presetId", "presetNm"};

        try {
            parameterValidation(param, keyList);
            parameterInt("presetId", param.get("presetId"), true);
            parameterString("presetNm", param.get("presetNm"), true, 0, null);

            responseMap = presetService.putPreset(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putPreset] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putPreset] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    // TODO : 로직 설명 듣기
    @PatchMapping("/run")
    public Map<String, Object> patchPresetRun(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

//        String[] keyList = {"presetId", "presetNm"};

        try {
//            parameterValidation(param, keyList);
//            parameterInt("presetId", param.get("presetId"), true);
//            parameterString("presetNm", param.get("presetNm"), true, 0, null);

            responseMap = presetService.patchPresetRun(param);
        }
//        catch (ParamException paramException){
//            log.error("[paramException][patchPresetRun] - {}", paramException.getMessage());
//            responseMap.put("code", paramException.getCode());
//            responseMap.put("message", paramException.getMessage());
//        }
        catch (Exception exception) {
            log.error("[Exception][patchPresetRun] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    // TODO : 로직 설명 듣기
    @PatchMapping("/stop")
    public Map<String, Object> patchPresetStop(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

//        String[] keyList = {"presetId", "presetNm"};

        try {
//            parameterValidation(param, keyList);
//            parameterInt("presetId", param.get("presetId"), true);
//            parameterString("presetNm", param.get("presetNm"), true, 0, null);

            responseMap = presetService.patchPresetStop(param);
        }
//        catch (ParamException paramException){
//            log.error("[paramException][patchPresetStop] - {}", paramException.getMessage());
//            responseMap.put("code", paramException.getCode());
//            responseMap.put("message", paramException.getMessage());
//        }
        catch (Exception exception) {
            log.error("[Exception][patchPresetStop] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}