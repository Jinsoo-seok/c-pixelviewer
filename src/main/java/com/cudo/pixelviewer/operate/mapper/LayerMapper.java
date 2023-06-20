package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.LayerVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LayerMapper {

    List<LayerVo> getLayerList();

    LayerVo getLayer(String layerId);

    int postLayerValid(Map<String, Object> param);
    int postLayer(Map<String, Object> param);

    int deleteLayerValid(Map<String, Object> param);
    int deleteLayer(Map<String, Object> param);

    int putLayerValid(Map<String, Object> param);
    int putLayer(Map<String, Object> param);

}