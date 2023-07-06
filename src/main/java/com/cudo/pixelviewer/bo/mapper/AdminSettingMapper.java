package com.cudo.pixelviewer.bo.mapper;

import com.cudo.pixelviewer.vo.DisplaySettingVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminSettingMapper {

    List<Map<String, Object>> getAdminSettingList();

    int putAdminSetting(List<Map<String, Object>> param);


    List<DisplaySettingVo> getDisplayInfoList();

    DisplaySettingVo getDisplayInfo(String displayId);

    int postDisplayInfo(Map<String, Object> param);

    int putDisplayInfoValid(Map<String, Object> param);
    int putDisplayInfo(Map<String, Object> param);

    int deleteDisplayInfoValid(Map<String, Object> param);
    String displayUsedCheck(Map<String, Object> param);
    int deleteDisplayInfo(Map<String, Object> param);


    int patchWeatherImg(String settingKey, String originalFilename);

    String getTestPattern();

    String getValue(String key);

}