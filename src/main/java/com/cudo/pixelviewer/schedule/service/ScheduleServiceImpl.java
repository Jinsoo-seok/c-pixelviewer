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
import java.util.*;

import static com.cudo.pixelviewer.component.scheduler.ScheduleCode.*;
import static com.cudo.pixelviewer.util.ParameterUtils.*;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    final ScheduleMapper scheduleMapper;

    final SchedulerManager schedulerManager;

    @Override
    public Map<String, Object> getCalenderStatus(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        List<ScheduleVo> playList = new ArrayList<>();

        playList.add(ScheduleVo.builder()
                .scheduleId(1L)
                .scheduleName("플레이리스트1")
                .startDate("20230601")
                .endDate("20230603")
                .startTime("09:00")
                .endTime("18:00")
                .type("playlist")
                .build());

        playList.add(ScheduleVo.builder()
                .scheduleId(2L)
                .scheduleName("플레이리스트2")
                .startDate("20230605")
                .endDate("20230606")
                .startTime("09:00")
                .endTime("18:00")
                .type("playlist")
                .build());

        playList.add(ScheduleVo.builder()
                .scheduleId(3L)
                .scheduleName("LED 전원1")
                .startDate("20230601")
                .endDate("20230603")
                .startTime("09:00")
                .endTime("18:00")
                .type("power")
                .build());

        playList.add(ScheduleVo.builder()
                .scheduleId(4L)
                .scheduleName("LED 전원2")
                .startDate("20230605")
                .endDate("20230606")
                .startTime("09:00")
                .endTime("18:00")
                .type("power")
                .build());

        playList.add(ScheduleVo.builder()
                .scheduleId(5L)
                .scheduleName("밝기1")
                .startDate("20230601")
                .endDate("20230603")
                .type("light")
                .build());

        playList.add(ScheduleVo.builder()
                .scheduleId(6L)
                .scheduleName("밝기2")
                .startDate("20230601")
                .endDate("20230603")
                .type("light")
                .build());

        resultMap.put("data", CalenderStatusVo.builder()
                .schedule(playList)
                .build());

        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> getScheduleStatus(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        List<Integer> scheduleDay = new ArrayList<>();

        scheduleDay.add(0);
        scheduleDay.add(1);
        scheduleDay.add(2);

        resultMap.put("data", PlayListDetailScheduleVo.builder()
                .scheduleId(1L)
                .preset(2)
                .playList(1)
                .scheduleName("플레이리스트 1")
                .startDate("20230601")
                .endDate("20230603")
                .startTime("09:00")
                .endTime("18:00")
                .scheduleDay(scheduleDay)
                .build());
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
    }

    @Override
    public Map<String, Object> setLedContent(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        return resultMap;
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
                .runDayWeek(String.valueOf(param.get("scheduleDay")))
                .build();

        int result = scheduleMapper.postLedPower(powerSchedule);

        if (result > 0) {
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

        int result = scheduleMapper.putLedPower(setPowerParam(param));

        if (result > 0) {
            PowerScheduleVo powerSchedule = PowerScheduleVo.builder()
                    .scheduleId(Long.parseLong(String.valueOf(param.get("scheduleId"))))
                    .schNm(String.valueOf(param.get("scheduleName")))
                    .schStartDate(String.valueOf(param.get("startDate")))
                    .schEndDate(String.valueOf(param.get("endDate")))
                    .timePwrOn(String.valueOf(param.get("startTime")))
                    .timePwrOff(String.valueOf(param.get("endTime")))
                    .runDayWeek(String.valueOf(param.get("scheduleDay")))
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

        int result = scheduleMapper.deleteLedPower(scheduleId);

        if (result > 0) {
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
                .runDayWeek(String.valueOf(param.get("scheduleDay")))
                .build();

        // 밝기 스케줄 등록
        int scheduleResult = scheduleMapper.postLight(lightSchedule);

        if (scheduleResult > 0) {
            List<LightListScheduleVo> lightList = new ArrayList<>();

            for (Object brightness : (ArrayList) param.get("brightnessList")) {
                lightList.add(LightListScheduleVo.builder()
                        .scheduleId(lightSchedule.getScheduleId())
                        .runtime(Integer.parseInt(String.valueOf(((Map<String, Object>) brightness).get("time"))) * 100)
                        .BrightnessVal(Integer.parseInt(String.valueOf(((Map<String, Object>) brightness).get("brightness"))))
                        .build());
            }

            // 밝기 제어 리스트 등록
            int resultList = scheduleMapper.postLightList(lightList);

            if (resultList > 0) {
                Map<String, Object> listParam = new HashMap<>();
                List<LightScheduleVo> lightScheduleList = new ArrayList<>(); // 스케줄 등록 용

                listParam.put("listId", lightList.get(0).getListId());
                listParam.put("scheduleId", lightSchedule.getScheduleId());
                listParam.put("count", resultList);

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
        int scheduleResult = scheduleMapper.putLight(param);

        if (scheduleResult > 0) {
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

                    if (deleteSet.contains(Long.parseLong(String.valueOf(lightInfo.get("listId"))))) {
                        deleteSet.remove(Long.parseLong(String.valueOf(lightInfo.get("listId"))));
                    }
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
                                .runDayWeek(String.valueOf(param.get("scheduleDay")))
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
                                .runDayWeek(String.valueOf(param.get("scheduleDay")))
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

        int result = scheduleMapper.deleteLight(scheduleId);

        for (Long listId : deleteList) { // 스케줄 삭제
            schedulerManager.deleteJob(LIGHT.getValue() + listId);
        }

        if (result > 0) {
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
        String scheduleDay = "";

        for (Object day : (ArrayList) scheduleDayList) {
            scheduleDay += "," + day;
        }

        return scheduleDay.replaceFirst(",", "");
    }
}
