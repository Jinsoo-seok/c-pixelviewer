package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class contentIncludedVo implements Serializable {

    private static final long serialVersionUID = 6579185683703449050L;

    private Integer presetId;
    private String presetNm;

    private Integer layerId;
    private String layerNm;

    private Integer playlistId;
    private String playlistNm;
}