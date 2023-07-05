package com.cudo.pixelviewer.schedule.service;

import org.quartz.SchedulerException;

import java.util.Map;

public interface ScheduleService {
    Map<String, Object> getCalenderStatus(Map<String, Object> param);
    Map<String, Object> getScheduleStatus(Map<String, Object> param);
    Map<String, Object> setLedContent(Map<String, Object> param);

    Map<String, Object> postLedPower(Map<String, Object> param);
    Map<String, Object> putLedPower(Map<String, Object> param);
    Map<String, Object> deleteLedPower(Map<String, Object> param) throws SchedulerException;
}
