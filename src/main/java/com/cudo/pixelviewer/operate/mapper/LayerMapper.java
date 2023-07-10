package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.LayerVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LayerMapper {

    List<Map<String, Object>> getLayerList();
    List<Integer> getLayerListPlayList(Integer layerId);

//    LayerVo getLayer(String layerId);
    Map<String, Object> getLayer(String layerId);
    List<Map<String, Object>> getLayerLayerObject(String layerId);
    Map<String, Object> getLayerObjectExternalVideo(Integer objectId);
    Map<String, Object> getLayerObjectExternalInfo(Integer objectId);
    Map<String, Object> getLayerObjectExternalSubtitle(Integer objectId);

    int postLayerValid(Map<String, Object> param);
    int postLayer(Map<String, Object> param);

    int deleteLayerValid(Map<String, Object> param);
    int deleteLayer(Map<String, Object> param);

    int putLayerValid(Map<String, Object> param);
    int putLayer(Map<String, Object> param);


    // Viewer Status
    int updateViewerStatus(Map<String, Object> param);

}