package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayListPowerScheduleVo {
    Long scheduleId;
    String scheduleName;
    String startDateTime;
    String endDateTime;
}
