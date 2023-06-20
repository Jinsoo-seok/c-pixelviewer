package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DisplaySettingVo implements Serializable {

    private static final long serialVersionUID = 7501741676458000824L;

    private Integer displayId;
    private String displayNm;
    private String gpuNm;

    private Integer posX;
    private Integer posY;

    private Integer width;
    private Integer height;

    private Integer primaryFl;
}