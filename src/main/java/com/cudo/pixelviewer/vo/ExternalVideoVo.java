package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExternalVideoVo implements Serializable {

    private static final long serialVersionUID = -2146168113004076236L;

    private Integer exVideoId;

    private Integer objectId;

    private Integer type;

    private Integer posX;
    private Integer posY;

    private Integer width;
    private Integer height;

    private Integer ord;

    private String rtspUrl;

    private Integer videoNm;
    private String videoFormat;
}