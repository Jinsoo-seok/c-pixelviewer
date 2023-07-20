package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class LayerStatusRunVo implements Serializable {

    private static final long serialVersionUID = -2335185412443244521L;

    private Integer layerId;
    private String layerNm;

    private String viewerStatus;
    private Boolean viewerYn;

    @JsonIgnore
    private Integer playlistId;
    @JsonIgnore
    private String playlistNm;

    public Map<String, Object> getPlaylistInfo() {
        Map<String, Object> playlistInfo = new HashMap<>();
        playlistInfo.put("playlistId", playlistId);
        playlistInfo.put("playlistNm", playlistNm);
        return playlistInfo;
    }
}