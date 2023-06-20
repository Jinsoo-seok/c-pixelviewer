package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminSettingVo implements Serializable {

    private static final long serialVersionUID = 8887096718322941951L;

    private Integer programId;

    private Integer viewTopmostEn;
    private Integer viewTemphumiEn;

    private Integer ledCommType;
    private Integer ledPresetEn;
    private Integer ledInputSelectEn;
    private Integer ledBrightnessControlEn;
    private Integer ledPresetCount;

    private Integer pwrControlEn;

    private Integer loginEn;

    private String imgDefaultPlaytime;

    private Integer extenalinfoArea;
}