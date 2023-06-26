package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminSettingVo implements Serializable {

    private static final long serialVersionUID = 8887096718322941951L;

    @JsonIgnore
    private Integer programId;

    private Boolean viewTopmostEn;
    private Boolean viewTemphumiEn;

    private String ledCommType;
    private Boolean ledPresetEn;
    private Boolean ledInputSelectEn;
    private Boolean ledBrightnessControlEn;
    private Integer ledPresetCount;

    private Boolean pwrControlEn;

    private Boolean loginEn;

    private String imgDefaultPlaytime;

    private Integer extenalinfoArea;
}