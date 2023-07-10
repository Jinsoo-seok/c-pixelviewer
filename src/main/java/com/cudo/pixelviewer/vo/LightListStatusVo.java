package com.cudo.pixelviewer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LightListStatusVo {
    Long listId;
    String time;
    String brightness;
}
