package com.cudo.pixelviewer.schedule.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.schedule.service.ScheduleService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/schedule")
public class ScheduleController {

    final ScheduleService scheduleService;

    /**
     * * 스케줄 상태 조회
     */
    @GetMapping("/calender-status")
    public Map<String, Object> selectCalenderStatus(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = scheduleService.selectCalenderStatus(param);
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    /**
     * * 개별 스케줄 상세 조회
     */
    @GetMapping("/status")
    public Map<String, Object> getScheduleStatus(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"type", "scheduleId"};

        try {
            parameterValidation(param, keyList);
            parameterString("type", param.get("type"), true, 0, null);
            parameterString("scheduleId", param.get("scheduleId"), true, 0, null);

            if (Integer.parseInt(String.valueOf(param.get("type"))) < 0 || Integer.parseInt(String.valueOf(param.get("type"))) > 2) {
                throw new ParamException(ResponseCode.INVALID_PARAM_VALUE, "type");
            }

            responseMap = scheduleService.selectScheduleStatus(param);
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
     * * LED 플레이리스트 영상 스케줄 등록
     */
    @PostMapping("/contetnts-reg")
    public Map<String, Object> postLedPlaylist(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"presetId", "layerId", "playListId", "scheduleName", "startDate", "endDate", "startTime", "endTime"};

        try {
            parameterValidation(param, keyList);

            parameterInt("presetId", param.get("presetId"), true);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("playListId", param.get("playListId"), true);
            parameterString("scheduleName", param.get("scheduleName"), true, 0, null);
            parameterDate("startDate", param.get("startDate"), true);
            parameterDate("endDate", param.get("endDate"), true);
            parameterTime("startTime", param.get("startTime"), true);
            parameterTime("endTime", param.get("endTime"), true);
            parameterCompareDate("startDate", "endDate", param.get("startDate"), param.get("endDate"));
            parameterScheduleDay("scheduleDay", param.get("scheduleDay"));

            responseMap = scheduleService.postLedPlaylistSchedule(param);
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
     * * LED 플레이리스트 영상 스케줄 수정
     */
    @PatchMapping("/contetnts-edit")
    public Map<String, Object> editLedContent(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleId", "presetId", "layerId", "playListId", "scheduleName", "startDate", "endDate","startTime", "endTime"};

        try {
            parameterValidation(param, keyList);

            parameterInt("scheduleId", param.get("scheduleId"), true);
            parameterInt("presetId", param.get("presetId"), true);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("playListId", param.get("playListId"), true);
            parameterString("scheduleName", param.get("scheduleName"), true, 0, null);
            parameterDate("startDate", param.get("startDate"), true);
            parameterDate("endDate", param.get("endDate"), true);
            parameterTime("startTime", param.get("startTime"), true);
            parameterTime("endTime", param.get("endTime"), true);
            parameterCompareDate("startDate", "endDate", param.get("startDate"), param.get("endDate"));
            parameterScheduleDay("scheduleDay", param.get("scheduleDay"));

            responseMap = scheduleService.puLedPlaylistSchedule(param);
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
     * * LED 플레이리스트 영상 스케줄 삭제
     */
    @DeleteMapping("/contetnts-delete")
    public Map<String, Object> deleteLedContent(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("scheduleId", param.get("scheduleId"), true);

            responseMap = scheduleService.deletePlaylistSchedule(param);
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
     * * 전원 스케줄 등록
     */
    @PostMapping("/power-reg")
    public Map<String, Object> postLedPower(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleName", "startDate", "endDate","startTime", "endTime"};

        try {
            parameterValidation(param, keyList);

            parameterString("scheduleName", param.get("scheduleName"), true, 0, null);
            parameterDate("startDate", param.get("startDate"), true);
            parameterDate("endDate", param.get("endDate"), true);
            parameterTime("startTime", param.get("startTime"), true);
            parameterTime("endTime", param.get("endTime"), true);
            parameterCompareDate("startDate", "endDate", param.get("startDate"), param.get("endDate"));
            parameterScheduleDay("scheduleDay", param.get("scheduleDay"));

            responseMap = scheduleService.postLedPower(param);
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
     * * 전원 스케줄 수정
     */
    @PatchMapping("/power-edit")
    public Map<String, Object> editLedPower(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleId", "scheduleName", "startDate", "endDate","startTime", "endTime"};

        try {
            parameterValidation(param, keyList);

            parameterInt("scheduleId", param.get("scheduleId"), true);
            parameterString("scheduleName", param.get("scheduleName"), true, 0, null);
            parameterDate("startDate", param.get("startDate"), true);
            parameterDate("endDate", param.get("endDate"), true);
            parameterTime("startTime", param.get("startTime"), true);
            parameterTime("endTime", param.get("endTime"), true);
            parameterCompareDate("startDate", "endDate", param.get("startDate"), param.get("endDate"));
            parameterScheduleDay("scheduleDay", param.get("scheduleDay"));

            responseMap = scheduleService.putLedPower(param);
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
     * * 전원 스케줄 삭제
     */
    @DeleteMapping("/power-delete")
    public Map<String, Object> deletePower(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("scheduleId", param.get("scheduleId"), true);

            responseMap = scheduleService.deleteLedPower(param);
        } catch (ParamException paramException) {
            log.error("[paramException] - {}", paramException.getMessage());

            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        } catch (SchedulerException exception) {
            log.error("[SchedulerException] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", "Schedule deletion Exception");
        } catch (Exception exception) {
            log.error("[Exception] - {}", exception.getMessage());

            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    /**
     * * 밝기 스케줄 등록
     */
    @PostMapping("/brightness-reg")
    public Map<String, Object> setBrightness(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleName", "startDate", "endDate", "brightnessList"};

        try {
            parameterValidation(param, keyList);

            parameterString("scheduleName", param.get("scheduleName"), true, 0, null);
            parameterDate("startDate", param.get("startDate"), true);
            parameterDate("endDate", param.get("endDate"), true);
            parameterCompareDate("startDate", "endDate", param.get("startDate"), param.get("endDate"));
            parameterArray("brightnessList", param.get("brightnessList"), true);
            parameterScheduleDay("scheduleDay", param.get("scheduleDay"));

            for (Object brightness : (ArrayList) param.get("brightnessList")) {
                parameterMap("brightnessList", brightness, true);
                parameterTime("time", ((Map<String, Object>) brightness).get("time"), true);
                parameterInt("brightness", ((Map<String, Object>) brightness).get("brightness"), true);
            }

            responseMap = scheduleService.postLight(param);
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
     * * 밝기 스케줄 수정
     */
    @PatchMapping("/brightness-edit")
    public Map<String, Object> editBrightness(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleId", "scheduleName", "startDate", "endDate", "brightnessList"};

        try {
            parameterValidation(param, keyList);

            parameterInt("scheduleId", param.get("scheduleId"), true);
            parameterString("scheduleName", param.get("scheduleName"), true, 0, null);
            parameterDate("startDate", param.get("startDate"), true);
            parameterDate("endDate", param.get("endDate"), true);
            parameterCompareDate("startDate", "endDate", param.get("startDate"), param.get("endDate"));
            parameterArray("brightnessList", param.get("brightnessList"), true);
            parameterScheduleDay("scheduleDay", param.get("scheduleDay"));

            for (Object brightness : (ArrayList) param.get("brightnessList")) {
                parameterMap("brightnessList", brightness, true);
                parameterTime("time", ((Map<String, Object>) brightness).get("time"), true);
                parameterInt("brightness", ((Map<String, Object>) brightness).get("brightness"), true);
            }

            responseMap = scheduleService.putLight(param);
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
     * * 밝기 스케줄 삭제
     */
    @DeleteMapping("/brightness-delete")
    public Map<String, Object> deleteBrightness(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"scheduleId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("scheduleId", param.get("scheduleId"), true);

            responseMap = scheduleService.deleteLight(param);
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
     * * 스케줄 삭제
     */
    @DeleteMapping
    public Map<String, Object> deleteSchedule(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "[" + request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());
        

        String[] keyList = {"contentId", "type"};

        try {
            parameterValidation(param, keyList);
            parameterInt("contentId", param.get("contentId"), true);
            parameterInt("type", param.get("type"), true);

            responseMap = scheduleService.deleteSchedule(param);
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
