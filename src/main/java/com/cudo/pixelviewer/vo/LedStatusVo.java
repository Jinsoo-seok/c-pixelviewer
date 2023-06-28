package com.cudo.pixelviewer.vo;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
public class LedStatusVo {
    private Integer powerState;
    private Integer presetNumber;
    private String inputSource;
    private Double brightness;

}
