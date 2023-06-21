package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DisplayVo implements Serializable {

    private static final long serialVersionUID = -5477575129266580919L;

    private Integer srcDisplayId;
    private Integer screenId;
    private Integer displayId;

    private String displayNm;

    private Integer row;
    private Integer column;

    private Integer posX;
    private Integer posY;

    private Integer width;
    private Integer height;

    private Integer primaryFl;
    private Integer patternFl;
}