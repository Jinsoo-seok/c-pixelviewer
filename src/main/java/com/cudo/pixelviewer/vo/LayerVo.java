package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LayerVo implements Serializable {

    private static final long serialVersionUID = -3712370800902083774L;

    private Integer layerId;

    private Integer presetId;

    private Integer screenId;

    private String layerNm;

    private Integer posX;
    private Integer posY;

    private Integer width;
    private Integer height;

    private Integer ord;

    private Boolean subtitleFirstYn;
    private Boolean subtitleSecondYn;

}