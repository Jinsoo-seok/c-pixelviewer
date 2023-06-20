package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlaylistContentsVo implements Serializable {

    private static final long serialVersionUID = 6624135788238183579L;

    private Integer itemId;
    private Integer ordNo;

    private Integer playlistId;

    private Integer type;

    private String ctsNm;
    private String ctsPath;
    private String playtime;

    private Integer weatherFl;
    private Integer airInfoFl;

}