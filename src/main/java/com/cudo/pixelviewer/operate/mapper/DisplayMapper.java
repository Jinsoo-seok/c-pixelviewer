package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.DisplayVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DisplayMapper {

    List<DisplayVo> getDisplayList();

    DisplayVo getDisplay(String displayId);

    List<Map<String, Object>> getDisplayPortlist();

    int patchDisplayTestpatternValid(Map<String, Object> param);
    int patchDisplayTestpattern(Map<String, Object> param);

}