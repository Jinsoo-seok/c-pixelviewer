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
public class PlaylistStatusVo {
    Long scheduleId;
    Long presetId;
    Long layerId;
    Long playListId;
    String scheduleName;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    List<Integer> scheduleDay;
}
