package com.cudo.pixelviewer.user.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.user.service.UserService;
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
@RequestMapping("/api-manager/user")
public class UserController {

    final UserService userService;

    @PostMapping("/login")
    public Map<String, Object> postLogin(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"userId", "userPw"};

        try {
            parameterValidation(param, keyList);
            parameterString("userId", param.get("userId"), true, 0, null);
            parameterString("userPw", param.get("userPw"), true, 0, null);

            responseMap = userService.postLogin(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postLogin] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postLogin] - {}", exception.getMessage());
            responseMap.putAll(ParameterUtils.responseOption("FAIL"));
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));    
        return responseMap;
    }

    @PostMapping("/logout")
    public Map<String, Object> postLogout(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"userId"};

        try {
            parameterValidation(param, keyList);
            parameterString("userId", param.get("userId"), true, 0, null);

            responseMap = userService.postLogout(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postLogout] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postLogout] - {}", exception.getMessage());
            responseMap.putAll(ParameterUtils.responseOption("FAIL"));
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));    
        return responseMap;
    }
}
