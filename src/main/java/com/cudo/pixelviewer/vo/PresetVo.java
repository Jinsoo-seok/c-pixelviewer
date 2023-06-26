package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PresetVo implements Serializable {

    private static final long serialVersionUID = 2191902407715205413L;

    private Integer presetId;

    private Integer screenId;

    private String presetNm;

    private Integer rowsize;
    private Integer columnsize;

    private Boolean userStyleYn;

    private String presetStatus;
}