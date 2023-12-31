package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.component.InputSourceComponent;
import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.LedService;
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
@RequestMapping("/api-manager/led-control")
public class LedController {
    final LedService ledService;

    final InputSourceComponent inputSourceComponent;

    /**
     * * LED 전광판 밝기 제어
     */
    @PatchMapping("/brightness")
    public Map<String, Object> setBrightness(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"brightness"};

        try {
            parameterValidation(param, keyList);
            parameterDouble("brightness", param.get("brightness"), true, 1.0);

            responseMap = ledService.setBrightness(Float.parseFloat(String.valueOf(param.get("brightness"))));
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
     * * LED 영상 입력소스 선택
     */
    @PatchMapping("/videoinput")
    public Map<String, Object> setInputSource(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"inputSource"};

        try {
            parameterValidation(param, keyList);
            parameterString("inputSource", param.get("inputSource"), true, 0, null);
            inputSourceComponent.getInputSourceCode((String) param.get("inputSource"));

            responseMap = ledService.setInputSource(String.valueOf(param.get("inputSource")));
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
     * * LED 컨트롤러의 저장된 프리셋 실행
     */
    @PatchMapping("/preset-load")
    public Map<String, Object> loadPreset(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"presetNumber"};

        try {
            parameterValidation(param, keyList);
            parameterString("presetNumber", param.get("presetNumber"), true, 0, null);

            responseMap = ledService.loadPreset(String.valueOf(param.get("presetNumber")));
        } catch (ParamException paramException) {
            log.error("[paramException] - {}", paramException.getMessage());

            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    /**
     * * 전광판 상태 확인
     */
    @GetMapping("/status")
    public Map<String, Object> getLedStatus(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = ledService.getLedStatus();
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    /**
     * * 프리셋 리스트 조회
     */
    @GetMapping("/preset")
    public Map<String, Object> getLedPreset(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = ledService.getLedPreset();
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    /**
     * * 프리셋 명 수정
     */
    @PatchMapping("/preset/name")
    public Map<String, Object> putLedPresetName(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"presetNumber", "presetName"};

        try {
            parameterValidation(param, keyList);
            parameterString("presetNumber", param.get("presetNumber"), true, 0, null);
            parameterString("presetName", param.get("presetName"), true, 0, null);

            responseMap = ledService.putLedPresetName(param);

        } catch (ParamException paramException) {
            log.error("[paramException] - {}", paramException.getMessage());

            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }
}
