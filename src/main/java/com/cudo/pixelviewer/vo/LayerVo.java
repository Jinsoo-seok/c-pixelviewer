package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

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

    private Boolean subFirstEn;
    private Boolean subSecondEn;
    private Boolean exVideoEn;
    private Boolean weatherEn;
    private Boolean airEn;

    private Map<String, Object> layerObjectList;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    private String updateDate;
}