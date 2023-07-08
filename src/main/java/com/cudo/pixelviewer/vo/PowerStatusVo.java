package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerStatusVo {
    Long scheduleId;
    String scheduleName;
    String startDate;
    String endDate;
    String powerOnTime;
    String powerOffTime;
    List<Integer> scheduleDay;
}
