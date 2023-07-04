package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleVo {
    Long scheduleId;
    String scheduleName;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    String type;
}
