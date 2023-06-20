package com.cudo.pixelviewer.bo.mapper;

import com.cudo.pixelviewer.vo.AdminSettingVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminSettingMapper {

    List<AdminSettingVo> getAdminSettingList();

    int patchLayerTopMost(Map<String, Object> param);

    int patchTempHumi(Map<String, Object> param);

    int patchControlType(Map<String, Object> param);

    int patchLedPresetEnable(Map<String, Object> param);

    int patchLedPresetCount(Map<String, Object> param);

    int patchLedInputEnable(Map<String, Object> param);

}