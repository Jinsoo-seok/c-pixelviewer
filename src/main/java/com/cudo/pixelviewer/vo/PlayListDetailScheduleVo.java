package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayListDetailScheduleVo {
    Long scheduleId;
    Integer preset;
    Integer playList;
    String scheduleName;
    String startDateTime;
    String endDateTime;
    List<Integer> scheduleDay;
}
