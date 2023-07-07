package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LightScheduleVo {
    Long scheduleId;
    String schNm;
    String schStartDate;
    String schEndDate;
    String runDayWeek;
    Long listId;
    String runtime;
    String BrightnessVal;
}
