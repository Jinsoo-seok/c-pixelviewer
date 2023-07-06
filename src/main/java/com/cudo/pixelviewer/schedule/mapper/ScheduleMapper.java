package com.cudo.pixelviewer.schedule.mapper;

import com.cudo.pixelviewer.vo.LightListScheduleVo;
import com.cudo.pixelviewer.vo.LightScheduleVo;
import com.cudo.pixelviewer.vo.PowerScheduleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduleMapper {
    int postLedPower(PowerScheduleVo param);
    int putLedPower(Map<String, Object> param);
    int deleteLedPower(Long scheduleId);

    int postLight(LightScheduleVo param);
    int postLightList(List<LightListScheduleVo> param);

    List<Long> selectListId(Map<String, Object> param);
    List<Long> selectLightList(Long scheduleId);

    int deleteLightList(List<Long> listId);
    int putLight(Map<String, Object> param);
    int putLightList(List<LightListScheduleVo> param);
    int deleteLight(Long scheduleId);

    List<PowerScheduleVo> selectPowerSchedule(String nowDate);
    List<LightScheduleVo> selectLightSchedule(String nowDate);
}
