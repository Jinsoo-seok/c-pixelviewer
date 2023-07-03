package com.cudo.pixelviewer.bo.mapper;

import com.cudo.pixelviewer.vo.AdminSettingVo;
import com.cudo.pixelviewer.vo.DisplaySettingVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminSettingMapper {

//    List<AdminSettingVo> getAdminSettingList();
    List<Map<String, Object>> getAdminSettingList();

//    int putAdminSetting(Map<String, Object> param);
    int putAdminSetting(List<Map<String, Object>> param);

//    int patchLayerTopMost(Map<String, Object> param);
//
//    int patchTempHumi(Map<String, Object> param);
//
//    int patchControlType(Map<String, Object> param);
//
//    int patchLedPresetEnable(Map<String, Object> param);
//
//    int patchLedPresetCount(Map<String, Object> param);
//
//    int patchLedInputEnable(Map<String, Object> param);


    List<DisplaySettingVo> getDisplayInfoList();

    DisplaySettingVo getDisplayInfo(String displayId);

    int postDisplayInfo(Map<String, Object> param);

    int putDisplayInfoValid(Map<String, Object> param);
    int putDisplayInfo(Map<String, Object> param);

    int deleteDisplayInfoValid(Map<String, Object> param);
    int deleteDisplayInfo(Map<String, Object> param);

    int patchWeatherImg(String settingKey, String originalFilename);

    String getTestPattern();

}