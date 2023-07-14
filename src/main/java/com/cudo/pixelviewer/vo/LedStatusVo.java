package com.cudo.pixelviewer.vo;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
public class LedStatusVo {
    private String ip;
    private String presetNumber;
    private String inputSource;
    private Float brightness;

}
