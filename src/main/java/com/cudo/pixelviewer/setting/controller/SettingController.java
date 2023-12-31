package com.cudo.pixelviewer.setting.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.setting.service.SettingService;
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
public class SettingController {

    final SettingService settingService;

    /**
     * setting >> service Key-Value
     *
     * @return responseMap
     */
    @GetMapping("/api/setting/getValue")
    public Map<String, Object> settingGetValue(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = settingService.serviceGetValue();
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            responseMap.putAll(ParameterUtils.responseOption("FAIL"));
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));    
        return responseMap;
    }

    @PatchMapping("/api-manager/operate/setting/image-defaultplaytime")
    public Map<String, Object> patchSettingImageDefaultPlaytime(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"playtime"};

        try {
            parameterValidation(param, keyList);
            parameterInt("playtime", param.get("playtime"), true);

            responseMap = settingService.patchSettingImageDefaultPlaytime(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchSettingImageDefaultPlaytime] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchSettingImageDefaultPlaytime] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));    
        return responseMap;
    }

    @GetMapping("/api-manager/setting/font-list")
    public Map<String, Object> getFontList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = settingService.getFontList();
        }
        catch (Exception exception) {
            log.error("[Exception][getFontList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));    
        return responseMap;
    }
}
