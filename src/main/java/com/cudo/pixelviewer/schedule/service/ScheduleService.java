package com.cudo.pixelviewer.schedule.service;

import java.util.Map;

public interface ScheduleService {
    Map<String, Object> getCalenderStatus(Map<String, Object> param);
    Map<String, Object> getScheduleStatus(Map<String, Object> param);
    Map<String, Object> setLedContent(Map<String, Object> param);
}
