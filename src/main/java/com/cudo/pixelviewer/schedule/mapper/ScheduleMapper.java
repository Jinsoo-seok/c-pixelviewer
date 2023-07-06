package com.cudo.pixelviewer.schedule.mapper;

import com.cudo.pixelviewer.vo.PowerScheduleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduleMapper {
    int postLedPower(Map<String, Object> param);
    int putLedPower(Map<String, Object> param);
    int deleteLedPower(Long scheduleId);

    List<PowerScheduleVo> selectPowerSchedule(Map<String, Object> param);
}
