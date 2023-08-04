package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedPlayScheduleVo {
    Long scheduleId;
    Long presetId;
    String schStartDate;
    String schEndDate;
    String timeStart;
    String timeEnd;
    String runDayWeek;
    Integer layerId;
    Integer playlistId;
}
