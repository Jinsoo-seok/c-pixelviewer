package com.cudo.pixelviewer.vo;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LightListScheduleVo {
    Long listId;
    Long scheduleId;
    Integer runtime;
    Integer BrightnessVal;
}
