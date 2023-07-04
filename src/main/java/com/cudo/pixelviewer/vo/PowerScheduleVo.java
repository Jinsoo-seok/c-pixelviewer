package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PowerScheduleVo {
    Long scheduleId;
    String schStartDate;
    String schEndDate;
    String timePwrOn;
    String timePwrOff;
    String runDayWeek;
}
