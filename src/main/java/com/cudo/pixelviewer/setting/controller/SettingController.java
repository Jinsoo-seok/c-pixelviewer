package com.cudo.pixelviewer.setting.controller;

import com.cudo.pixelviewer.component.TcpSender;
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

import static com.cudo.pixelviewer.util.ParameterUtils.parameterString;
import static com.cudo.pixelviewer.util.ParameterUtils.parameterValidation;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SettingController {

    final SettingService settingService;

    final TcpSender tcpSender;

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

        Map<String, Object> responseMap = new HashMap<>();


        byte[] payload = {01, 02, 03};
        tcpSender.sendMessage(payload);


        try {
            responseMap = settingService.serviceGetValue();
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            responseMap.putAll(ParameterUtils.responseOption("FAIL"));
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    // TODO : 이미지 재생시간 관련 테이블이 어디?
    // TODO : 전체를 바꾼다고해도 기존에 이미지별로 재생시간 바꾼게 있으면 어떻게 체크?
    @PutMapping("/api-manager/operate/setting/image-defaultplaytime")
    public Map<String, Object> putSettingImageDefaultPlaytime(HttpServletRequest request
            , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));

        String[] keyList = {"playtime"};

        try {
            parameterValidation(param, keyList);
            parameterString("playtime", param.get("playtime"), true, 0, null);

            responseMap = settingService.putSettingImageDefaultPlaytime(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putSettingSet] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putSettingSet] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}
