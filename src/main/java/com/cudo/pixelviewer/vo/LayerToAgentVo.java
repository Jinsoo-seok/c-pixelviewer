package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LayerToAgentVo implements Serializable {

    private static final long serialVersionUID = -5874309104121533616L;

    private Integer layerId;

    private Integer posX;
    private Integer posY;

    private Integer width;
    private Integer height;

    private Integer ord;
}