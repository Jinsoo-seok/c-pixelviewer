package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlaylistVo implements Serializable {

    private static final long serialVersionUID = 6264357852367254731L;

    private Integer playlistId;

    private String playlistNm;

    private Integer layerId;

    private Integer presetId;

    private Integer screenId;

    private String contentIdList;

}