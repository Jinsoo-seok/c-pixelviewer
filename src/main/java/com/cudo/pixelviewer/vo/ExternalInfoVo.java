package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExternalInfoVo implements Serializable {

    private static final long serialVersionUID = 8656551749037290517L;

    private Integer exInfoId;
    @JsonIgnore
    private Integer screenId;
    @JsonIgnore
    private Integer presetId;
    @JsonIgnore
    private Integer layerId;
    @JsonIgnore
    private Integer objectId;

    @JsonIgnore
    private Integer type;

    private Integer posX;
    private Integer posY;
    private Integer width;
    private Integer height;
    private Integer ord;

    @JsonIgnore
    private String imagePath;

    @JsonIgnore
    private String forecolor;
    @JsonIgnore
    private String fontFl;

    private String fontNm;
    private Integer fontSize;
    private String fontColor;
    private Integer borderSize;
    private String borderColor;
    private String backColor;

    @JsonIgnore
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    private String updateDate;
}