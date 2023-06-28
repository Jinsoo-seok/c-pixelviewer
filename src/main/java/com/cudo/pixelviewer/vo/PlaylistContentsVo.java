package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class PlaylistContentsVo implements Serializable {

    private static final long serialVersionUID = 6624135788238183579L;

    @JsonIgnore
    private Integer contentId;
    @JsonIgnore
    private Integer type;
    @JsonIgnore
    private String ctsNm;


    private String ctsPath;
    private Integer playtime;

    private Boolean weatherFl;
    private Boolean airInfoFl;
    private Boolean stretch;

    private String thumbnailImageNm;

}