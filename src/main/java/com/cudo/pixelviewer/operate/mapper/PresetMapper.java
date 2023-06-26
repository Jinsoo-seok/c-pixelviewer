package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.LayerToAgentVo;
import com.cudo.pixelviewer.vo.LayerVo;
import com.cudo.pixelviewer.vo.PresetVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PresetMapper {

    List<PresetVo> getPresetList();

    PresetVo getPreset(String presetId);
    List<LayerVo> getPresetLayers(String presetId);

    int postPresetValid(Map<String, Object> param);
    int postPreset(Map<String, Object> param);

    int deletePresetValid(Map<String, Object> param);
    int deletePreset(Map<String, Object> param);

    int patchPresetNameValid(Map<String, Object> param);
    int patchPresetName(Map<String, Object> param);

    int putPresetValid(Map<String, Object> param);
    int putPreset(Map<String, Object> param);
    int putPresetDeleteLayers(Map<String, Object> param);
    int saveLayer(Map<String, Object> param);

    // Preset Run
    List<LayerToAgentVo> getPresetLayersToAgent(String presetId);
}