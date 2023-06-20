package com.cudo.pixelviewer.operate.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SubtitleMapper {

    Integer postSubtitleValid(Map<String, Object> param);
    int postSubtitle(Map<String, Object> param);
    int putSubtitle(Map<String, Object> param);
    int postSubtitleLayer(Map<String, Object> param);

    int deleteSubtitleValid(Map<String, Object> param);
    int deleteSubtitle(Map<String, Object> param);

    Integer patchSubtitleTextValid(Map<String, Object> param);
    int patchSubtitleText(Map<String, Object> param);

    Integer patchSubtitleLocationValid(Map<String, Object> param);
    int patchSubtitleLocation(Map<String, Object> param);

    Integer patchSubtitleSizeValid(Map<String, Object> param);
    int patchSubtitleSize(Map<String, Object> param);

    Integer patchSubtitleStyleValid(Map<String, Object> param);
    int patchSubtitleStyle(Map<String, Object> param);

    Integer patchSubtitleScrollValid(Map<String, Object> param);
    int patchSubtitleScroll(Map<String, Object> param);
}