package com.cudo.pixelviewer.operate.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SubtitleMapper {

    Integer postSubtitleValid(Map<String, Object> param);
    int postSubtitle(Map<String, Object> param);
    int putSubtitle(Map<String, Object> param);
    int postSubtitleLayer(Map<String, Object> param);

}