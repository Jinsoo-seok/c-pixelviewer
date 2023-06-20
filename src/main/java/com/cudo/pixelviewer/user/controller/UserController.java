package com.cudo.pixelviewer.user.controller;

import com.cudo.pixelviewer.user.service.UserService;
import com.cudo.pixelviewer.util.ParameterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController("/api-manager/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    final UserService userService;

    @PostMapping("/login")
    public Map<String, Object> postLogin(HttpServletRequest request
                                        , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();

        String[] keyList = {"", ""};

        try {
//            parameterValidation(param, keyList);
//            parameterString("", param.get(""), true, 0, null);
//            responseMap = settingService.serviceGetValue();
        }
//        catch (ParamException paramException){
//            log.error("[paramException][postLogin] - {}", paramException.getMessage());
//            responseMap.put("code", paramException.getCode());
//            responseMap.put("message", paramException.getMessage());
//        }
        catch (Exception exception) {
            log.error("[Exception][postLogin] - {}", exception.getMessage());
            responseMap.putAll(ParameterUtils.responseOption("FAIL"));
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    @PostMapping("/logout")
    public Map<String, Object> postLogout(HttpServletRequest request
                                       , @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);

        Map<String, Object> responseMap = new HashMap<>();

        String[] keyList = {"", ""};

        try {
//            parameterValidation(param, keyList);
//            parameterString("", param.get(""), true, 0, null);
//            responseMap = settingService.serviceGetValue();
        }
//        catch (ParamException paramException){
//            log.error("[paramException][postLogout] - {}", paramException.getMessage());
//            responseMap.put("code", paramException.getCode());
//            responseMap.put("message", paramException.getMessage());
//        }
        catch (Exception exception) {
            log.error("[Exception][postLogout] - {}", exception.getMessage());
            responseMap.putAll(ParameterUtils.responseOption("FAIL"));
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        log.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}
