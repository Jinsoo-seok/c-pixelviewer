package com.cudo.pixelviewer.schedule.service;

import com.cudo.pixelviewer.component.scheduler.SchedulerManager;
import com.cudo.pixelviewer.schedule.mapper.ScheduleMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.*;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    final ScheduleMapper scheduleMapper;

    final SchedulerManager schedulerManager;

    @Override
    public Map<String, Object> selectCalenderStatus(Map<String, Object> param) {
        Map<String, Object> responseMap = new HashMap<>();
        String startDate = (String) param.get("startDate"),
                endDate = (String) param.get("endDate");

        if (startDate == null) {
            LocalDate firstDayOfYear = LocalDate.now().withDayOfYear(1);
            param.put("startDate", firstDayOfYear);
        }

        if (endDate == null) {
            LocalDate lastDayOfYear = LocalDate.now().withDayOfYear(365);
            param.put("endDate", lastDayOfYear);
        }

        List<Map<String, Object>> scheduleList = scheduleMapper.selectCalenderStatus(param);

        if (scheduleList.size() > 0) {
            List<ScheduleVo> dataList = new ArrayList<>();

            for (Map<String, Object> schedule : scheduleList) {
                dataList.add(ScheduleVo.builder()
                        .scheduleId(Long.parseLong(String.valueOf(schedule.get("scheduleId"))))
                        .scheduleName((String) schedule.get("scheduleName"))
                        .startDate((String) schedule.get("startDate"))
                        .endDate((String) schedule.get("endDate"))
                        .startTime(schedule.get("startTime") == null ? null : (String) schedule.get("startTime"))
                        .endTime(schedule.get("endTime") == null ? null : (String) schedule.get("endTime"))
                        .type((String) schedule.get("type"))
                        .scheduleDay((schedule.get("scheduleDay") == null || schedule.get("scheduleDay").equals(""))
                                ? null : Arrays.stream(String.valueOf(schedule.get("scheduleDay")).split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toCollection(ArrayList::new)))
                        .build());
            }

            responseMap.put("data", dataList);
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> selectScheduleStatus(Map<String, Object> param) {
        Map<String, Object> responseMap = new HashMap<>();

        Map<String, Object> resultMap = new HashMap<>();
        Long scheduleId = Long.parseLong(String.valueOf(param.get("scheduleId")));
        Object data = null;

        if (param.get("type").equals("0")) { // LED 플레이리스트 스케줄
            resultMap = scheduleMapper.selectPlaylistInfoSchedule(scheduleId);

            if (resultMap != null) {
                data = PlaylistStatusVo.builder()
                        .scheduleId(Long.parseLong(String.valueOf(resultMap.get("schedule_id"))))
                        .presetId(Long.parseLong(String.valueOf(resultMap.get("preset_id"))))
                        .layerId(Long.parseLong(String.valueOf(resultMap.get("layer_id"))))
                        .playListId(Long.parseLong(String.valueOf(resultMap.get("playlist_id"))))
                        .scheduleName(String.valueOf(resultMap.get("sch_nm")))
                        .startDate(String.valueOf(resultMap.get("sch_start_date")).substring(0, 10).replace("-", ""))
                        .endDate(String.valueOf(resultMap.get("sch_end_date")).substring(0, 10).replace("-", ""))
                        .startTime(String.valueOf(resultMap.get("time_start")).substring(0, 5).replace(":", ""))
                        .endTime(String.valueOf(resultMap.get("time_end")).substring(0, 5).replace(":", ""))
                        .scheduleDay((resultMap.get("run_day_week") == null || resultMap.get("run_day_week").equals("")) ?
                                null : Arrays.stream(String.valueOf(resultMap.get("run_day_week")).split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()))
                        .build();
            }

        } else if (param.get("type").equals("1")) { // 전원 스케줄
            resultMap = scheduleMapper.selectPowerInfoSchedule(scheduleId);

            if (resultMap != null) {
                data = PowerStatusVo.builder()
                        .scheduleId(Long.parseLong(String.valueOf(resultMap.get("schedule_id"))))
                        .scheduleName(String.valueOf(resultMap.get("sch_nm")))
                        .startDate(String.valueOf(resultMap.get("sch_start_date")).substring(0, 10).replace("-", ""))
                        .endDate(String.valueOf(resultMap.get("sch_end_date")).substring(0, 10).replace("-", ""))
                        .powerOnTime(String.valueOf(resultMap.get("time_pwr_on")).substring(0, 5).replace(":", ""))
                        .powerOffTime(String.valueOf(resultMap.get("time_pwr_off")).substring(0, 5).replace(":", ""))
                        .scheduleDay((resultMap.get("run_day_week") == null || resultMap.get("run_day_week").equals("")) ?
                                null : Arrays.stream(String.valueOf(resultMap.get("run_day_week")).split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()))
                        .build();
            }
        } else if (param.get("type").equals("2")) { // 밝기 스케줄
            List<Map<String, Object>> resultList = scheduleMapper.selectLightInfoSchedule(scheduleId);

            List<LightListStatusVo> lightList = new ArrayList<>();

            for (Map<String, Object> lightInf : resultList) {
                lightList.add(LightListStatusVo.builder()
                        .listId(Long.parseLong(String.valueOf(lightInf.get("list_id"))))
                        .time(String.valueOf(lightInf.get("runtime")).substring(0, 5).replace(":", ""))
                        .brightness(String.valueOf(lightInf.get("Brightness_val")))
                        .build());
            }

            if (resultList.size() > 0) {
                data = LightStatusVo.builder()
                        .scheduleId(Long.parseLong(String.valueOf(resultList.get(0).get("schedule_id"))))
                        .scheduleName(String.valueOf(resultList.get(0).get("sch_nm")))
                        .startDate(String.valueOf(resultList.get(0).get("sch_start_date")).substring(0, 10).replace("-", ""))
                        .endDate(String.valueOf(resultList.get(0).get("sch_end_date")).substring(0, 10).replace("-", ""))
                        .scheduleDay((resultList.get(0).get("run_day_week") == null || resultList.get(0).get("run_day_week").equals("")) ?
                                null : Arrays.stream(String.valueOf(resultList.get(0).get("run_day_week")).split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()))
                        .brightnessList(lightList)
                        .build();
            }
        }

        if (data != null) {
            responseMap.put("data", data);
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLedPlaylistSchedule(Map<String, Object> param) throws SchedulerException, ParseException {
        Map<String, Object> responseMap = new HashMap<>();

        int insertCount = scheduleMapper.postLedPlaylistSchedule(setPowerParam(param));

        if (insertCount > 0) {
            // TODO 스케줄 등록

            LedPlayScheduleVo ledPlayScheduleVo = LedPlayScheduleVo.builder()
                    .scheduleId(Long.parseLong(String.valueOf(param.get("scheduleId"))))
                    .presetId(Long.parseLong(String.valueOf(param.get("presetId"))))
                    .schStartDate(String.valueOf(param.get("startDate")))
                    .schEndDate(String.valueOf(param.get("endDate")))
                    .timeStart(String.valueOf(param.get("startTime")))
                    .timeEnd(String.valueOf(param.get("endTime")))
                    .runDayWeek((param.get("scheduleDay") == null || param.get("scheduleDay").equals(""))
                            ? null : String.valueOf(param.get("scheduleDay")))
                    .build();

            // 스케줄 등록
            schedulerManager.setJob(ledPlayScheduleVo, LED_PLAY_LIST_START.getValue(), false);
            schedulerManager.setJob(ledPlayScheduleVo, LED_PLAY_LIST_END.getValue(), false);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> puLedPlaylistSchedule(Map<String, Object> param) throws SchedulerException, ParseException{
        Map<String, Object> responseMap = new HashMap<>();

        int insertCount = scheduleMapper.putLedPlaylistSchedule(setPowerParam(param));

        if (insertCount > 0) {
            LedPlayScheduleVo ledPlayScheduleVo = LedPlayScheduleVo.builder()
                    .scheduleId(Long.parseLong(String.valueOf(param.get("scheduleId"))))
                    .presetId(Long.parseLong(String.valueOf(param.get("presetId"))))
                    .schStartDate(String.valueOf(param.get("startDate")))
                    .schEndDate(String.valueOf(param.get("endDate")))
                    .timeStart(String.valueOf(param.get("startTime")))
                    .timeEnd(String.valueOf(param.get("endTime")))
                    .runDayWeek((param.get("scheduleDay") == null || param.get("scheduleDay").equals(""))
                            ? null : String.valueOf(param.get("scheduleDay")))
                    .build();

            // 스케줄 수정
            schedulerManager.setJob(ledPlayScheduleVo, LED_PLAY_LIST_START.getValue(), true);
            schedulerManager.setJob(ledPlayScheduleVo, LED_PLAY_LIST_END.getValue(), true);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    public Map<String, Object> deletePlaylistSchedule(Map<String, Object> param) throws SchedulerException {
        Map<String, Object> responseMap = new HashMap<>();

        Long scheduleId = Long.parseLong(String.valueOf(param.get("scheduleId")));
        int insertCount = scheduleMapper.deletePlayListSchedule(scheduleId);

        if (insertCount > 0) {
            // 스케줄 삭제
            schedulerManager.deleteJob(LED_PLAY_LIST_START.getValue() + scheduleId);
            schedulerManager.deleteJob(LED_PLAY_LIST_END.getValue() + scheduleId);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLedPower(Map<String, Object> param) throws SchedulerException, ParseException {
        Map<String, Object> responseMap = new HashMap<>();

        setPowerParam(param);

        PowerScheduleVo powerSchedule = PowerScheduleVo.builder()
                .schNm(String.valueOf(param.get("scheduleName")))
                .schStartDate(String.valueOf(param.get("startDate")))
                .schEndDate(String.valueOf(param.get("endDate")))
                .timePwrOn(String.valueOf(param.get("startTime")))
                .timePwrOff(String.valueOf(param.get("endTime")))
                .runDayWeek(param.get("scheduleDay") == null ? null : String.valueOf(param.get("scheduleDay")))
                .build();

        int insertCount = scheduleMapper.postLedPower(powerSchedule);

        if (insertCount > 0) {
            // 스케줄 등록
            schedulerManager.setJob(powerSchedule, POWER_ON.getValue(), false);
            schedulerManager.setJob(powerSchedule, POWER_OFF.getValue(), false);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putLedPower(Map<String, Object> param) throws SchedulerException, ParseException {
        Map<String, Object> responseMap = new HashMap<>();

        int updateCount = scheduleMapper.putLedPower(setPowerParam(param));

        if (updateCount > 0) {
            PowerScheduleVo powerSchedule = PowerScheduleVo.builder()
                    .scheduleId(Long.parseLong(String.valueOf(param.get("scheduleId"))))
                    .schNm(String.valueOf(param.get("scheduleName")))
                    .schStartDate(String.valueOf(param.get("startDate")))
                    .schEndDate(String.valueOf(param.get("endDate")))
                    .timePwrOn(String.valueOf(param.get("startTime")))
                    .timePwrOff(String.valueOf(param.get("endTime")))
                    .runDayWeek(param.get("scheduleDay") == null ? null : String.valueOf(param.get("scheduleDay")))
                    .build();

            // 스케줄 수정
            schedulerManager.setJob(powerSchedule, POWER_ON.getValue(), true);
            schedulerManager.setJob(powerSchedule, POWER_OFF.getValue(), true);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteLedPower(Map<String, Object> param) throws SchedulerException {
        Map<String, Object> responseMap = new HashMap<>();

        Long scheduleId = Long.parseLong(String.valueOf(param.get("scheduleId")));

        int deleteCount = scheduleMapper.deleteLedPower(scheduleId);

        if (deleteCount > 0) {
            // 스케줄 삭제
            schedulerManager.deleteJob(POWER_ON.getValue() + scheduleId);
            schedulerManager.deleteJob(POWER_OFF.getValue() + scheduleId);

            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLight(Map<String, Object> param) throws SchedulerException, ParseException {
        Map<String, Object> responseMap = new HashMap<>();

        param.put("scheduleDay", parseScheduleDay(param.get("scheduleDay")));

        LightScheduleVo lightSchedule = LightScheduleVo.builder()
                .schNm(String.valueOf(param.get("scheduleName")))
                .schStartDate(String.valueOf(param.get("startDate")))
                .schEndDate(String.valueOf(param.get("endDate")))
                .runDayWeek(param.get("scheduleDay") == null ? null : String.valueOf(param.get("scheduleDay")))
                .build();

        // 밝기 스케줄 등록
        int insertLightCount = scheduleMapper.postLight(lightSchedule);

        if (insertLightCount > 0) {
            List<LightListScheduleVo> lightList = new ArrayList<>();

            for (Object brightness : (ArrayList) param.get("brightnessList")) {
                lightList.add(LightListScheduleVo.builder()
                        .scheduleId(lightSchedule.getScheduleId())
                        .runtime(Integer.parseInt(String.valueOf(((Map<String, Object>) brightness).get("time"))) * 100)
                        .BrightnessVal(Integer.parseInt(String.valueOf(((Map<String, Object>) brightness).get("brightness"))))
                        .build());
            }

            // 밝기 제어 리스트 등록
            int insertLightListCount = scheduleMapper.postLightList(lightList);

            if (insertLightListCount > 0) {
                Map<String, Object> listParam = new HashMap<>();
                List<LightScheduleVo> lightScheduleList = new ArrayList<>(); // 스케줄 등록 용

                listParam.put("listId", lightList.get(0).getListId());
                listParam.put("scheduleId", lightSchedule.getScheduleId());
                listParam.put("count", insertLightListCount);

                List<Long> listId = scheduleMapper.selectListId(listParam); // list id 가져오기

                for (int i = 0; i < lightList.size(); i++) {
                    lightScheduleList.add(LightScheduleVo.builder()
                            .scheduleId(lightSchedule.getScheduleId())
                            .schNm(lightSchedule.getSchNm())
                            .schStartDate(lightSchedule.getSchStartDate())
                            .schEndDate(lightSchedule.getSchEndDate())
                            .runDayWeek(lightSchedule.getRunDayWeek())
                            .listId(listId.get(i))
                            .runtime(String.valueOf(lightList.get(i).getRuntime()))
                            .BrightnessVal(String.valueOf(lightList.get(i).getBrightnessVal()))
                            .build());
                }

                // 스케줄 등록
                schedulerManager.setJob(lightScheduleList, LIGHT.getValue(), false);

                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } else {
                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
            }
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putLight(Map<String, Object> param) throws SchedulerException, ParseException {
        Map<String, Object> responseMap = new HashMap<>();

        param.put("scheduleDay", parseScheduleDay(param.get("scheduleDay")));

        List<Long> deleteListId = scheduleMapper.selectLightList(Long.parseLong(String.valueOf(param.get("scheduleId"))));
        Set<Long> deleteSet = new HashSet<>(deleteListId);

        for (Long listId : deleteListId) { // 밝기 전체 스케줄 삭제
            schedulerManager.deleteJob(LIGHT.getValue() + listId);
        }

        // 밝기 스케줄 수정
        int updateLightCount = scheduleMapper.putLight(param);

        if (updateLightCount > 0) {
            List<LightListScheduleVo> insertLightList = new ArrayList<>(),
                    updateLightList = new ArrayList<>();

            List<LightScheduleVo> lightScheduleList = new ArrayList<>(); // 스케줄 등록 용

            // 기존/새롭게 추가 구별 해서 list 세팅
            for (Object light : (ArrayList) param.get("brightnessList")) {
                Map<String, Object> lightInfo = (Map<String, Object>) light;

                if (lightInfo.get("listId") == null) { // 새로운 밝기 추가
                    insertLightList.add(LightListScheduleVo.builder()
                            .scheduleId(Long.parseLong(String.valueOf(param.get("scheduleId"))))
                            .runtime(Integer.parseInt(String.valueOf(lightInfo.get("time"))) * 100)
                            .BrightnessVal(Integer.parseInt(String.valueOf(lightInfo.get("brightness"))))
                            .build());

                } else { // 기존에 있던 밝기 수정
                    updateLightList.add(LightListScheduleVo.builder()
                            .listId(Long.parseLong(String.valueOf(lightInfo.get("listId"))))
                            .scheduleId(Long.parseLong(String.valueOf(param.get("scheduleId"))))
                            .runtime(Integer.parseInt(String.valueOf(lightInfo.get("time"))) * 100)
                            .BrightnessVal(Integer.parseInt(String.valueOf(lightInfo.get("brightness"))))
                            .build());

                    deleteSet.remove(Long.parseLong(String.valueOf(lightInfo.get("listId"))));
                }
            }

            // 기존에 있던 밝기 중 삭제된 것은 삭제
            if (deleteSet.size() > 0) {
                List<Long> deleteList = new ArrayList<>(deleteSet);

                // 기존 밝기 삭제
                scheduleMapper.deleteLightList(deleteList);
            }

            int insertResultList = 0, updateResultList = 0;

            // 새로운 밝기 추가
            if (insertLightList.size() > 0) {
                insertResultList = scheduleMapper.postLightList(insertLightList);

                if (insertResultList > 0) {
                    Map<String, Object> listParam = new HashMap<>();

                    listParam.put("listId", insertLightList.get(0).getListId());
                    listParam.put("scheduleId", Long.parseLong(String.valueOf(param.get("scheduleId"))));
                    listParam.put("count", insertResultList);

                    List<Long> listId = scheduleMapper.selectListId(listParam); // list id 가져오기

                    for (int i = 0; i < insertLightList.size(); i++) {
                        // 스케줄 등록할 vo
                        lightScheduleList.add(LightScheduleVo.builder()
                                .scheduleId(insertLightList.get(i).getScheduleId())
                                .schNm(String.valueOf(param.get("scheduleName")))
                                .schStartDate(String.valueOf(param.get("startDate")))
                                .schEndDate(String.valueOf(param.get("endDate")))
                                .runDayWeek(param.get("scheduleDay") == null ? null : String.valueOf(param.get("scheduleDay")))
                                .listId(listId.get(i))
                                .runtime(String.valueOf(insertLightList.get(i).getRuntime()))
                                .BrightnessVal(String.valueOf(insertLightList.get(i).getBrightnessVal()))
                                .build());
                    }
                }
            }

            // 기존 밝기 수정
            if (updateLightList.size() > 0) {
                updateResultList = scheduleMapper.putLightList(updateLightList);

                if (updateResultList > 0) {
                    for (LightListScheduleVo lightListScheduleVo : updateLightList) {
                        // 스케줄 등록할 vo
                        lightScheduleList.add(LightScheduleVo.builder()
                                .scheduleId(lightListScheduleVo.getScheduleId())
                                .schNm(String.valueOf(param.get("scheduleName")))
                                .schStartDate(String.valueOf(param.get("startDate")))
                                .schEndDate(String.valueOf(param.get("endDate")))
                                .runDayWeek(param.get("scheduleDay") == null ? null : String.valueOf(param.get("scheduleDay")))
                                .listId(lightListScheduleVo.getListId())
                                .runtime(String.valueOf(lightListScheduleVo.getRuntime()))
                                .BrightnessVal(String.valueOf(lightListScheduleVo.getBrightnessVal()))
                                .build());
                    }
                }
            }

            // 오류 없이 완료할 경우 Response 값 세팅
            if ((insertLightList.size() > 0 && updateLightList.size() > 0 && insertResultList > 0 && updateResultList > 0)
                    || (insertLightList.size() > 0 && insertResultList > 0) || (updateLightList.size() > 0 && updateResultList > 0)) {

                // 스케줄 등록
                schedulerManager.setJob(lightScheduleList, LIGHT.getValue(), true);

                responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteLight(Map<String, Object> param) throws SchedulerException {
        Map<String, Object> responseMap = new HashMap<>();

        Long scheduleId = Long.parseLong(String.valueOf(param.get("scheduleId")));

        // 삭제될 listId 조회
        List<Long> deleteList = scheduleMapper.selectLightList(Long.parseLong(String.valueOf(param.get("scheduleId"))));

        int deleteCount = scheduleMapper.deleteLight(scheduleId);

        for (Long listId : deleteList) { // 스케줄 삭제
            schedulerManager.deleteJob(LIGHT.getValue() + listId);
        }

        if (deleteCount > 0) {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        } else {
            responseMap.putAll(ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName()));
        }

        return responseMap;
    }

    private Map<String, Object> setPowerParam(Map<String, Object> param) {
        param.put("startTime", Integer.parseInt(String.valueOf(param.get("startTime"))) * 100);
        param.put("endTime", Integer.parseInt(String.valueOf(param.get("endTime"))) * 100);

        param.put("scheduleDay", parseScheduleDay(param.get("scheduleDay")));

        return param;
    }

    private String parseScheduleDay(Object scheduleDayList) {
        if (scheduleDayList != null && ((ArrayList<Integer>) scheduleDayList).size() > 0) {
            String scheduleDay = "";

            for (Object day : (ArrayList) scheduleDayList) {
                scheduleDay += "," + day;
            }

            return scheduleDay.replaceFirst(",", "");
        }

        return null;
    }
}
