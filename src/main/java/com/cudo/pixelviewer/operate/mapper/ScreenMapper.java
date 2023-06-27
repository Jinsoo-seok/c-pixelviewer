package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.ScreenVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScreenMapper {

    List<ScreenVo> getScreenList();

//    ScreenVo getScreen(String screenId);
    Map<String, Object> getScreen(String screenId);
    List<Map<String, Object>> getScreenAllocateDisplays(String screenId);

    int postScreenValid(Map<String, Object> param);
    int postScreen(Map<String, Object> param);

    int deleteScreenValid(Map<String, Object> param);
    int deleteScreen(Map<String, Object> param);

    int patchScreenNameValid(Map<String, Object> param);
    int patchScreenName(Map<String, Object> param);

    int putScreenValid(Map<String, Object> param);
    int putScreen(Map<String, Object> param);
    int putScreenDeleteDisplays(Map<String, Object> param);
    int saveAllocateDisplays(Map<String, Object> param);


}
