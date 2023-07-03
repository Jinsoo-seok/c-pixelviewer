package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrightnessScheduleVo {
    Long scheduleId;
    String scheduleName;
    String startDate;
    String endDate;
    String type;
}
