package com.cudo.pixelviewer.schedule.service;

import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.Map;

public interface ScheduleService {
    Map<String, Object> getCalenderStatus(Map<String, Object> param);
    Map<String, Object> getScheduleStatus(Map<String, Object> param);
    Map<String, Object> postLedPlaylist(Map<String, Object> param);

    Map<String, Object> postLedPower(Map<String, Object> param) throws SchedulerException, ParseException;
    Map<String, Object> putLedPower(Map<String, Object> param) throws SchedulerException, ParseException;
    Map<String, Object> deleteLedPower(Map<String, Object> param) throws SchedulerException;

    Map<String, Object> postLight(Map<String, Object> param) throws SchedulerException, ParseException;
    Map<String, Object> putLight(Map<String, Object> param) throws SchedulerException, ParseException;
    Map<String, Object> deleteLight(Map<String, Object> param) throws SchedulerException;
}
