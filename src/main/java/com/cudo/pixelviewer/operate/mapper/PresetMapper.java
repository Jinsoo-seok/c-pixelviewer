package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.PresetVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PresetMapper {

    List<PresetVo> getPresetList();

    PresetVo getPreset(String screenId);

    int postPresetValid(Map<String, Object> param);
    int postPreset(Map<String, Object> param);

    int deletePresetValid(Map<String, Object> param);
    int deletePreset(Map<String, Object> param);

    int patchPresetNameValid(Map<String, Object> param);
    int patchPresetName(Map<String, Object> param);

    int putPresetValid(Map<String, Object> param);
    int putPreset(Map<String, Object> param);
}