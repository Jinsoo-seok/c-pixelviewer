package com.cudo.pixelviewer.schedule.service;

import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.BrightnessScheduleVo;
import com.cudo.pixelviewer.vo.CalenderStatusVo;
import com.cudo.pixelviewer.vo.PlayListDetailScheduleVo;
import com.cudo.pixelviewer.vo.PlayListPowerScheduleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
    @Override
    public Map<String, Object> getCalenderStatus(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        // 하드 코딩용
        List<PlayListPowerScheduleVo> playList = new ArrayList<>();
        List<PlayListPowerScheduleVo> ledPower = new ArrayList<>();
        List<BrightnessScheduleVo> brightness = new ArrayList<>();

        playList.add(PlayListPowerScheduleVo.builder()
                .scheduleId(1L)
                .scheduleName("플레이리스트1")
                .startDate("20230601")
                .endDate("20230603")
                .startTime("09:00")
                .endTime("18:00")
                .build());

        playList.add(PlayListPowerScheduleVo.builder()
                .scheduleId(2L)
                .scheduleName("플레이리스트2")
                .startDate("20230605")
                .endDate("20230606")
                .startTime("09:00")
                .endTime("18:00")
                .build());

        ledPower.add(PlayListPowerScheduleVo.builder()
                .scheduleId(1L)
                .scheduleName("LED 전원1")
                .startDate("20230601")
                .endDate("20230603")
                .startTime("09:00")
                .endTime("18:00")
                .build());

        ledPower.add(PlayListPowerScheduleVo.builder()
                .scheduleId(2L)
                .scheduleName("LED 전원2")
                .startDate("20230605")
                .endDate("20230606")
                .startTime("09:00")
                .endTime("18:00")
                .build());

        brightness.add(BrightnessScheduleVo.builder()
                .scheduleId(1L)
                .scheduleName("밝기1")
                .startDate("20230601")
                .endDate("20230603")
                .build());

        brightness.add(BrightnessScheduleVo.builder()
                .scheduleId(2L)
                .scheduleName("밝기2")
                .startDate("20230601")
                .endDate("20230603")
                .build());

        resultMap.put("data", CalenderStatusVo.builder()
                .playList(playList)
                .ledPower(ledPower)
                .brightness(brightness)
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
}
