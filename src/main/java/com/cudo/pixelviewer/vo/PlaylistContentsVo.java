package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlaylistContentsVo implements Serializable {

    private static final long serialVersionUID = 6624135788238183579L;

    private Integer contentId;

    private Integer type;

    private String ctsNm;
    private String ctsPath;
    private Integer playtime;

    private Boolean weatherFl;
    private Boolean airInfoFl;
    private Boolean stretch;

}