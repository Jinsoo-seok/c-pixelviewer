package com.cudo.pixelviewer.schedule.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ScheduleMapper {
    int postLedPower(Map<String, Object> param);
}
