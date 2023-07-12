package com.cudo.pixelviewer.vo;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
public class LedStatusVo {
    private Integer powerState;
    private String presetNumber;
    private String inputSource;
    private Float brightness;

}
