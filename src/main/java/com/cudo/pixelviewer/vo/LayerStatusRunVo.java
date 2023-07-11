package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LayerStatusRunVo implements Serializable {

    private static final long serialVersionUID = -2335185412443244521L;

    private Integer layerId;
    private String layerNm;

    private String viewerStatus;
    private Boolean viewerYn;
}