package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubtitleVo implements Serializable {

    private static final long serialVersionUID = 8728602685820408253L;

    private Integer subtitleId;
    private Integer objectId;
    private Integer posX;
    private Integer posY;
    private Integer width;
    private Integer height;
    private Integer ord;
    private Integer scrollWay;
    private Integer scrollSpeed;

    private String subtitleStyle;
}